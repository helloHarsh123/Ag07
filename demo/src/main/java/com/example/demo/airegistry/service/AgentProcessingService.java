package com.example.demo.airegistry.service;

import com.example.demo.airegistry.model.Agent;
import com.example.demo.airegistry.model.AgentEmbedding;
import com.example.demo.airegistry.model.ApiDetails;
import com.example.demo.airegistry.repository.AgentRepository;
import com.example.demo.airegistry.repository.AgentEmbeddingRepository;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.huggingface.HuggingFaceEmbeddingModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgentProcessingService {
    
    @Autowired
    private AgentRepository agentRepository;
    
    @Autowired
    private AgentEmbeddingRepository embeddingRepository;

    @Autowired
    private HuggingFaceEmbeddingModel embeddingModel;
    
    @Value("thenlper/gte-small")
    private String modelId;
    
    public void processUnprocessedAgents() {
        List<Agent> activeAgents = agentRepository.findByActiveTrue();
        for (Agent agent : activeAgents) {
            if (!embeddingRepository.findByAgentId(agent.getId()).isPresent()) {
                processAgent(agent);
            }
        }
    }
    
    private void processAgent(Agent agent) {
        try {
            String content = generateAgentContent(agent);
            Embedding embedding = embeddingModel.embed(content).content();
            
            AgentEmbedding agentEmbedding = new AgentEmbedding();
            agentEmbedding.setAgentId(agent.getId());
            agentEmbedding.setContent(content);
            agentEmbedding.setEmbedding(embedding.vectorAsList());
            agentEmbedding.setCreatedAt(LocalDateTime.now());
            
            // Set metadata
            AgentEmbedding.EmbeddingMetadata metadata = new AgentEmbedding.EmbeddingMetadata();
            metadata.setModelId(modelId);
            metadata.setDimensions(embedding.vector().length);
            metadata.setGeneratedAt(LocalDateTime.now());
            agentEmbedding.setMetadata(metadata);
            
            embeddingRepository.save(agentEmbedding);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to process agent: " + agent.getId(), e);
        }
    }
    
    private String generateAgentContent(Agent agent) {
        ApiDetails apiDetails = agent.getApiDetails();
        return String.format(
            """
            Agent Name: %s
            Description: %s
            Use Cases: %s
            Example Prompts: %s
            API Details:
            - Endpoint: %s
            - Auth Type: %s
            - Version: %s
            """,
            agent.getName(),
            agent.getDescription(),
            (agent.getUseCases()!=null && !agent.getUseCases().isEmpty())?String.join(", ", agent.getUseCases()):"",
            (agent.getExamplePrompts()!=null && !agent.getExamplePrompts().isEmpty())?String.join("\n", agent.getExamplePrompts()):"",
            (apiDetails!=null)?apiDetails.getEndpoint():"",
            (apiDetails!=null)?apiDetails.getAuthType():"",
            (apiDetails!=null)?apiDetails.getApiVersion():""
        );
    }
    
    // Method to find similar agents
    public List<AgentEmbedding> findSimilarAgents(String agentId, int limit) {
        AgentEmbedding sourceEmbedding = embeddingRepository.findByAgentId(agentId)
            .orElseThrow(() -> new RuntimeException("Agent embedding not found"));
            
        return embeddingRepository.findSimilarEmbeddings(
            sourceEmbedding.getEmbedding().stream()
                .mapToDouble(Float::doubleValue)
                .toArray(),
            limit
        );
    }
    
    // Method to find similar agents by text query
    public List<AgentEmbedding> findSimilarAgentsByText(String queryText, int limit) {
        Embedding queryEmbedding = embeddingModel.embed(queryText).content();
        
        return embeddingRepository.findSimilarEmbeddings(
            queryEmbedding.vectorAsList().stream()
                .mapToDouble(Float::doubleValue)
                .toArray(),
            limit
        );
    }
}