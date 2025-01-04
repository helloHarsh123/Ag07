package com.example.demo.airegistry.service;

import com.example.demo.airegistry.dto.OpenAIRequest;
import com.example.demo.airegistry.dto.OpenAIResponse;
import com.example.demo.airegistry.model.Agent;
import com.example.demo.airegistry.model.AgentProcessingStatus;
import com.example.demo.airegistry.repository.AgentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.airegistry.repository.AgentProcessingStatusRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class AgentProcessingService {

    Logger logger = LoggerFactory.getLogger(AgentProcessingService.class);
    
    @Autowired
    private AgentRepository agentRepository;
    
    @Autowired
    private AgentProcessingStatusRepository processingStatusRepository;
    
    @Autowired
    private RestTemplate openaiRestTemplate;
    
    @Value("${openai.api.url}")
    private String openaiApiUrl;
    
    public void processUnprocessedAgents() {
        List<Agent> activeAgents = agentRepository.findByActiveTrue();
        
        for (Agent agent : activeAgents) {
            logger.info(agent.getName());
            AgentProcessingStatus status = processingStatusRepository
                .findByAgentId(agent.getId())
                .orElse(createNewProcessingStatus(agent));
            
            if (shouldProcess(status)) {
                logger.info("processing: " + agent.getName());
                processAgent(agent, status);
            }
        }
    }
    
    private boolean shouldProcess(AgentProcessingStatus status) {
        return status.getStatus() == AgentProcessingStatus.ProcessingStatus.FAILED || status.getStatus() == AgentProcessingStatus.ProcessingStatus.PENDING ||
               (status.getStatus() == AgentProcessingStatus.ProcessingStatus.COMPLETED &&
                status.getLastProcessedAt().plusHours(1).isBefore(LocalDateTime.now()));
    }
    
    private AgentProcessingStatus createNewProcessingStatus(Agent agent) {
        AgentProcessingStatus status = new AgentProcessingStatus();
        status.setAgentId(agent.getId());
        status.setStatus(AgentProcessingStatus.ProcessingStatus.PENDING);
        return processingStatusRepository.save(status);
    }
    
    private void processAgent(Agent agent, AgentProcessingStatus status) {
        try {
            status.setStatus(AgentProcessingStatus.ProcessingStatus.IN_PROGRESS);
            status = processingStatusRepository.save(status);
            
            String processingResult = processWithOpenAI(agent);
            
            status.setStatus(AgentProcessingStatus.ProcessingStatus.COMPLETED);
            status.setProcessingResult(processingResult);
            status.setLastProcessedAt(LocalDateTime.now());
            status.setErrorMessage(null);
            
        } catch (Exception e) {
            status.setStatus(AgentProcessingStatus.ProcessingStatus.FAILED);
            status.setErrorMessage(e.getMessage());
        }
        
        processingStatusRepository.save(status);
    }
    
    private String processWithOpenAI(Agent agent) {
        OpenAIRequest request = createOpenAIRequest(agent);
        OpenAIResponse response = openaiRestTemplate.postForObject(
            openaiApiUrl,
            request,
            OpenAIResponse.class
        );
        
        if (response != null && !response.getChoices().isEmpty()) {
            return response.getChoices().get(0).getMessage().getContent();
        }
        
        throw new RuntimeException("Failed to get response from OpenAI");
    }
    
    private OpenAIRequest createOpenAIRequest(Agent agent) {
        OpenAIRequest request = new OpenAIRequest();
        OpenAIRequest.Message systemMessage = new OpenAIRequest.Message();
        systemMessage.setRole("system");
        systemMessage.setContent("You are processing an AI agent with the following configuration. " +
                               "Analyze its capabilities and provide insights.");
        
        OpenAIRequest.Message userMessage = new OpenAIRequest.Message();
        userMessage.setRole("user");
        userMessage.setContent(String.format(
            "Agent Name: %s\nDescription: %s\nUse Cases: %s\nExample Prompts: %s",
            agent.getName(),
            agent.getDescription(),
            String.join(", ", agent.getUseCases()),
            String.join("\n", agent.getExamplePrompts())
        ));
        
        request.setMessages(Arrays.asList(systemMessage, userMessage));
        return request;
    }
}
