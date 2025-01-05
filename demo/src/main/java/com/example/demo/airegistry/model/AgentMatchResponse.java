package com.example.demo.airegistry.model;

import java.util.List;

public class AgentMatchResponse {
    private List<Agent> selectedAgents;
    private String confidence;
    private String reasoning;
    private String suggestedPrompt;

    // Getters and setters
    public List<Agent> getSelectedAgents() {
        return selectedAgents;
    }

    public void setSelectedAgents(List<Agent> selectedAgents) {
        this.selectedAgents = selectedAgents;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getReasoning() {
        return reasoning;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }

    public String getSuggestedPrompt() {
        return suggestedPrompt;
    }

    public void setSuggestedPrompt(String suggestedPrompt) {
        this.suggestedPrompt = suggestedPrompt;
    }
}