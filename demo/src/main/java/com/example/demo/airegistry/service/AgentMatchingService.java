package com.example.demo.airegistry.service;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.huggingface.HuggingFaceChatModel;
import dev.langchain4j.model.huggingface.HuggingFaceEmbeddingModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.airegistry.model.Agent;
import com.example.demo.airegistry.model.AgentEmbedding;
import com.example.demo.airegistry.model.AgentMatchResponse;
import com.example.demo.airegistry.repository.AgentEmbeddingRepository;
import com.example.demo.airegistry.repository.AgentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class AgentMatchingService {
    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private HuggingFaceChatModel chatModel;

    @Autowired
    private HuggingFaceEmbeddingModel embeddingModel;

    @Autowired
    private AgentEmbeddingRepository embeddingRepository;

    public AgentMatchResponse matchAgentForTask(String userTask) {        
        // Step 2: Get full agent details for the relevant matches
        List<Agent> relevantAgents = findSimilarAgentsByText(userTask, 5);

        // Step 3: Format agent information for the chat model
        String agentContext = formatAgentContext(relevantAgents);

        // Step 4: Create task matching prompt
        String prompt = createTaskMatchingPrompt(agentContext, userTask);

        // Step 5: Get matching recommendation from chat model
        AiMessage response = chatModel.generate(UserMessage.from(prompt)).content();

        return parseResponse(response.text(), relevantAgents);
    }

    List<Agent> findSimilarAgentsByText(String task, int limit){
        Embedding queryEmbedding = embeddingModel.embed(task).content();
        
        List<AgentEmbedding> relevantEmbeddings =  embeddingRepository.findSimilarEmbeddings(
            queryEmbedding.vectorAsList().stream()
                .mapToDouble(Float::doubleValue)
                .toArray(),
            limit
        );

        return relevantEmbeddings.stream()
                .map(embedding -> agentRepository.findById(embedding.getAgentId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
 
    }

    private String formatAgentContext(List<Agent> agents) {
        StringBuilder context = new StringBuilder();
        for (int i = 0; i < agents.size(); i++) {
            Agent agent = agents.get(i);
            context.append(String.format("""
                Agent %d:
                Name: %s
                Use Cases:
                %s
                Example Prompts:
                %s
                
                """,
                i + 1,
                agent.getName(),
                formatUseCases(agent.getUseCases()),
                formatExamplePrompts(agent.getExamplePrompts())
            ));
        }
        return context.toString();
    }

    private String formatUseCases(List<String> useCases) {
        return useCases.stream()
                .map(useCase -> "- " + useCase)
                .collect(Collectors.joining("\n"));
    }

    private String formatExamplePrompts(List<String> prompts) {
        return prompts.stream()
                .map(prompt -> "- " + prompt)
                .collect(Collectors.joining("\n"));
    }

    private String createTaskMatchingPrompt(String agentContext, String userTask) {
        return String.format("""
            You are an AI assistant helping to match user tasks with the most appropriate AI agents.
            Review the following AI agents' capabilities, use cases, and example prompts.
            Then recommend the best agent(s) for the user's task.
            
            Available Agents:
            %s
            
            User Task: %s
            
            Provide your response in the following format:
            SELECTED_AGENTS: [List agent numbers, comma-separated]
            CONFIDENCE: [High/Medium/Low]
            REASONING: [Explain why these agents are suitable]
            SUGGESTED_PROMPT: [Provide a recommended prompt based on the example prompts]
            """, agentContext, userTask);
    }

    private AgentMatchResponse parseResponse(String modelResponse, List<Agent> availableAgents) {
        // Parse the structured response from the model
        AgentMatchResponse response = new AgentMatchResponse();
        
        String[] lines = modelResponse.split("\n");
        for (String line : lines) {
            if (line.startsWith("SELECTED_AGENTS:")) {
                String[] agentNumbers = line.substring("SELECTED_AGENTS:".length()).trim()
                    .split(",");
                List<Agent> selectedAgents = new ArrayList<>();
                for (String num : agentNumbers) {
                    int index = Integer.parseInt(num.trim()) - 1;
                    if (index >= 0 && index < availableAgents.size()) {
                        selectedAgents.add(availableAgents.get(index));
                    }
                }
                response.setSelectedAgents(selectedAgents);
            } else if (line.startsWith("CONFIDENCE:")) {
                response.setConfidence(line.substring("CONFIDENCE:".length()).trim());
            } else if (line.startsWith("REASONING:")) {
                response.setReasoning(line.substring("REASONING:".length()).trim());
            } else if (line.startsWith("SUGGESTED_PROMPT:")) {
                response.setSuggestedPrompt(line.substring("SUGGESTED_PROMPT:".length()).trim());
            }
        }
        
        return response;
    }
}