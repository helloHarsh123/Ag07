package com.example.demo.airegistry.repository;

import com.example.demo.airegistry.model.Agent;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface AgentRepository extends MongoRepository<Agent, String> {
    boolean existsByName(String name);
    List<Agent> findByActiveTrue();
    Optional<Agent> findByNameAndActiveTrue(String name);
    List<Agent> findByUseCasesContainingIgnoreCase(String useCase);
}
