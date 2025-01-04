package com.example.demo.airegistry.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "agent_processing_status")
public class AgentProcessingStatus {
    @Id
    private String id;
    private String agentId;
    private ProcessingStatus status;
    private LocalDateTime lastProcessedAt;
    private String processingResult;
    private String errorMessage;

    public enum ProcessingStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }

    public String getId() {
        return id;
    }

    public String getAgentId() {
        return agentId;
    }

    public ProcessingStatus getStatus() {
        return status;
    }

    public LocalDateTime getLastProcessedAt() {
        return lastProcessedAt;
    }

    public String getProcessingResult() {
        return processingResult;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public void setStatus(ProcessingStatus status) {
        this.status = status;
    }

    public void setLastProcessedAt(LocalDateTime lastProcessedAt) {
        this.lastProcessedAt = lastProcessedAt;
    }

    public void setProcessingResult(String processingResult) {
        this.processingResult = processingResult;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}