package com.example.demo.airegistry.service;


import com.example.demo.airegistry.model.Agent;
import com.example.demo.airegistry.model.ApiDetails;
import com.example.demo.airegistry.repository.AgentRepository;
import com.example.demo.airegistry.exception.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
public class AgentRegistrationService {
    
    @Autowired
    private AgentRepository agentRepository;
    
    public Agent registerAgent(Agent agent) {
        if (agentRepository.existsByName(agent.getName())) {
            throw new AgentAlreadyExistsException("Agent with name " + agent.getName() + " already exists");
        }
        validateAgent(agent);
        return agentRepository.save(agent);
    }
    
    public Agent updateAgent(String id, Agent agent) {
        Agent existingAgent = agentRepository.findById(id)
            .orElseThrow(() -> new AgentNotFoundException("Agent not found with id: " + id));
            
        if (!existingAgent.getName().equals(agent.getName()) && 
            agentRepository.existsByName(agent.getName())) {
            throw new AgentAlreadyExistsException("Agent with name " + agent.getName() + " already exists");
        }
        
        validateAgent(agent);
        agent.setId(id);
        return agentRepository.save(agent);
    }
    
    public void deleteAgent(String id) {
        Agent agent = agentRepository.findById(id)
            .orElseThrow(() -> new AgentNotFoundException("Agent not found with id: " + id));
        agent.setActive(false);
        agentRepository.save(agent);
    }
    
    public Agent getAgent(String id) {
        return agentRepository.findById(id)
            .orElseThrow(() -> new AgentNotFoundException("Agent not found with id: " + id));
    }
    
    public Page<Agent> getAllAgents(Pageable pageable) {
        return agentRepository.findAll(pageable);
    }
    
    public List<Agent> findAgentsByUseCase(String useCase) {
        return agentRepository.findByUseCasesContainingIgnoreCase(useCase);
    }
    
    private void validateAgent(Agent agent) {
        if (agent.getName() == null || agent.getName().trim().isEmpty()) {
            throw new ValidationException("Agent name cannot be empty");
        }
        
        if (agent.getApiDetails() == null) {
            throw new ValidationException("API details cannot be empty");
        }
        
        validateApiDetails(agent.getApiDetails());
        validateUseCases(agent.getUseCases());
        validateExamplePrompts(agent.getExamplePrompts());
    }
    
    private void validateApiDetails(ApiDetails apiDetails) {
        if (apiDetails.getEndpoint() == null || !apiDetails.getEndpoint().matches("^https?://.*")) {
            throw new ValidationException("Invalid API endpoint URL");
        }
        
        if (apiDetails.getTimeoutMs() < 0 || apiDetails.getTimeoutMs() > 300000) {
            throw new ValidationException("Invalid timeout value");
        }
    }
    
    private void validateUseCases(List<String> useCases) {
        if (useCases == null || useCases.isEmpty()) {
            throw new ValidationException("At least one use case is required");
        }
        
        if (useCases.size() > 20) {
            throw new ValidationException("Maximum 20 use cases allowed");
        }
        
        for (String useCase : useCases) {
            if (useCase == null || useCase.trim().isEmpty()) {
                throw new ValidationException("Use cases cannot be empty");
            }
            if (useCase.length() > 200) {
                throw new ValidationException("Use case description too long (max 200 characters)");
            }
        }
    }
    
    private void validateExamplePrompts(List<String> prompts) {
        if (prompts == null || prompts.isEmpty()) {
            throw new ValidationException("At least one example prompt is required");
        }
        
        if (prompts.size() > 10) {
            throw new ValidationException("Maximum 10 example prompts allowed");
        }
        
        for (String prompt : prompts) {
            if (prompt == null || prompt.trim().isEmpty()) {
                throw new ValidationException("Example prompts cannot be empty");
            }
            if (prompt.length() > 500) {
                throw new ValidationException("Example prompt too long (max 500 characters)");
            }
        }
    }
}
