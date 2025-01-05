package com.example.demo.airegistry.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.TextIndexed;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "agent_embeddings")
public class AgentEmbedding {
    @Id
    private String id;
    private String agentId;
    @TextIndexed
    private String content;
    private List<Float> embedding;
    private LocalDateTime createdAt;
    private EmbeddingMetadata metadata;

    // Getters for main class
    public String getId() {
        return id;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getContent() {
        return content;
    }

    public List<Float> getEmbedding() {
        return embedding;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public EmbeddingMetadata getMetadata() {
        return metadata;
    }

    // Setters for main class
    public void setId(String id) {
        this.id = id;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setEmbedding(List<Float> embedding) {
        this.embedding = embedding;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setMetadata(EmbeddingMetadata metadata) {
        this.metadata = metadata;
    }

    // Inner class for additional metadata
    public static class EmbeddingMetadata {
        private String modelId;
        private int dimensions;
        private LocalDateTime generatedAt;

        // Getters for inner class
        public String getModelId() {
            return modelId;
        }

        public int getDimensions() {
            return dimensions;
        }

        public LocalDateTime getGeneratedAt() {
            return generatedAt;
        }

        // Setters for inner class
        public void setModelId(String modelId) {
            this.modelId = modelId;
        }

        public void setDimensions(int dimensions) {
            this.dimensions = dimensions;
        }

        public void setGeneratedAt(LocalDateTime generatedAt) {
            this.generatedAt = generatedAt;
        }
    }
}