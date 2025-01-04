package com.example.demo.airegistry.repository;

import com.example.demo.airegistry.model.AgentProcessingStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AgentProcessingStatusRepository extends MongoRepository<AgentProcessingStatus, String> {
    List<AgentProcessingStatus> findByStatusAndLastProcessedAtBefore(
        AgentProcessingStatus.ProcessingStatus status, 
        LocalDateTime time
    );
    
    List<AgentProcessingStatus> findByStatus(AgentProcessingStatus.ProcessingStatus status);
    
    Optional<AgentProcessingStatus> findByAgentId(String agentId);
}
