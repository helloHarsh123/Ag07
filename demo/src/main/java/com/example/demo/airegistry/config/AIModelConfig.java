package com.example.demo.airegistry.config;
import dev.langchain4j.model.huggingface.HuggingFaceEmbeddingModel;
import dev.langchain4j.model.huggingface.HuggingFaceChatModel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static java.time.Duration.ofSeconds;

@Configuration
public class AIModelConfig {

    private static final String accessToken = System.getenv("HUGGING_FACE_ACCESS_TOKEN");
    @Bean
    HuggingFaceEmbeddingModel embeddingModel() {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalStateException("HUGGING_FACE_ACCESS_TOKEN env variable is not set or is empty.");
        }
        return HuggingFaceEmbeddingModel.builder()
        .accessToken(accessToken)
        .modelId("thenlper/gte-small")
        .waitForModel(true)
        .timeout(ofSeconds(60))
        .build();
    }

    @Bean
    HuggingFaceChatModel chatModel() {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalStateException("HUGGING_FACE_ACCESS_TOKEN env variable is not set or is empty.");
        }
        return HuggingFaceChatModel.builder()
        .timeout(ofSeconds(25))
        .modelId("mistralai/Mistral-7B-Instruct-v0.3")
        .temperature(0.1)
        .maxNewTokens(150)
        .accessToken(accessToken)
        .waitForModel(true)
        .build();
    }
}