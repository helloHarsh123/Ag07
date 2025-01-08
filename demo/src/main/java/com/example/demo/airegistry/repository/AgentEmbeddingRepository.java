package com.example.demo.airegistry.repository;

import com.example.demo.airegistry.model.AgentEmbedding;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;
import java.util.Optional;

public interface AgentEmbeddingRepository extends MongoRepository<AgentEmbedding, String> {
    
    @Aggregation(pipeline = { "{$vectorSearch: {index: 'vector_index', queryVector: ?0, path: 'embedding', numCandidates: 100, limit: ?1}}" })
    List<AgentEmbedding> findSimilarEmbeddings(double[] queryVector, int limit);
    
    Optional<AgentEmbedding> findByAgentId(String agentId);
}
