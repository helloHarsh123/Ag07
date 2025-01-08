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
import java.util.Arrays;
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
        double[] embeddingArray =  queryEmbedding.vectorAsList().stream()
        .mapToDouble(Float::doubleValue)
        .toArray();
        List<AgentEmbedding> relevantEmbeddings =  embeddingRepository.findSimilarEmbeddings(
            embeddingArray,
            limit
        );
        List<Agent> agentList = new ArrayList<>();
        for(AgentEmbedding embedding: relevantEmbeddings){
            Optional<Agent> agent = agentRepository.findById(embedding.getAgentId());
            if(agent.isPresent()) {
                agentList.add(agent.get());
            }
        }
        return agentList;
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
                (agent.getUseCases() != null)?formatUseCases(agent.getUseCases()):"",
                (agent.getExamplePrompts() != null)?formatExamplePrompts(agent.getExamplePrompts()):""
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
        AgentMatchResponse response = new AgentMatchResponse();
        
        // Find the last occurrence of SELECTED_AGENTS:
        int lastStructuredResponseStart = modelResponse.lastIndexOf("SELECTED_AGENTS:");
        if (lastStructuredResponseStart == -1) {
            response.setReasoning("Response format not recognized: " + modelResponse);
            return response;
        }
        
        // Extract only the final structured part of the response
        String structuredPart = modelResponse.substring(lastStructuredResponseStart);
        String[] lines = structuredPart.split("\n");
        
        // Process each line
        for (String line : lines) {
            line = line.trim();
            try {
                if (line.startsWith("SELECTED_AGENTS:")) {
                    String agentsPart = line.substring("SELECTED_AGENTS:".length()).trim();
                    agentsPart = agentsPart.replaceAll("[\\[\\]]", "");
                    String[] agentNumbers = agentsPart.split(",");
                    
                    List<Agent> selectedAgents = new ArrayList<>();
                    for (String num : agentNumbers) {
                        try {
                            String cleanNum = num.trim();
                            if (!cleanNum.isEmpty()) {
                                int index = Integer.parseInt(cleanNum) - 1;
                                if (index >= 0 && index < availableAgents.size()) {
                                    selectedAgents.add(availableAgents.get(index));
                                }
                            }
                        } catch (NumberFormatException e) {
                            continue;
                        }
                    }
                    response.setSelectedAgents(selectedAgents);
                } else if (line.startsWith("CONFIDENCE:")) {
                    response.setConfidence(line.substring("CONFIDENCE:".length()).trim());
                } else if (line.startsWith("REASONING:")) {
                    StringBuilder fullReasoning = new StringBuilder(line.substring("REASONING:".length()).trim());
                    int i = Arrays.asList(lines).indexOf(line) + 1;
                    while (i < lines.length && 
                        !lines[i].trim().startsWith("SELECTED_AGENTS:") && 
                        !lines[i].trim().startsWith("CONFIDENCE:") && 
                        !lines[i].trim().startsWith("SUGGESTED_PROMPT:")) {
                        fullReasoning.append("\n").append(lines[i].trim());
                        i++;
                    }
                    response.setReasoning(fullReasoning.toString().trim());
                } else if (line.startsWith("SUGGESTED_PROMPT:")) {
                    StringBuilder fullPrompt = new StringBuilder(line.substring("SUGGESTED_PROMPT:".length()).trim());
                    int i = Arrays.asList(lines).indexOf(line) + 1;
                    while (i < lines.length && 
                        !lines[i].trim().startsWith("SELECTED_AGENTS:") && 
                        !lines[i].trim().startsWith("CONFIDENCE:") && 
                        !lines[i].trim().startsWith("REASONING:")) {
                        fullPrompt.append("\n").append(lines[i].trim());
                        i++;
                    }
                    response.setSuggestedPrompt(fullPrompt.toString().trim());
                }
            } catch (Exception e) {
                System.err.println("Error processing line: " + line + "\nError: " + e.getMessage());
            }
        }
        
        // Add validation and default values
        if (response.getSelectedAgents() == null) {
            response.setSelectedAgents(new ArrayList<>());
        }
        if (response.getConfidence() == null) {
            response.setConfidence("Low");
        }
        if (response.getReasoning() == null) {
            response.setReasoning("No reasoning provided");
        }
        if (response.getSuggestedPrompt() == null) {
            response.setSuggestedPrompt("No prompt suggested");
        }
        
        return response;
    }
}