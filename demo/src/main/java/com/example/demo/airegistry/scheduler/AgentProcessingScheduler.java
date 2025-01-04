package com.example.demo.airegistry.scheduler;

import com.example.demo.airegistry.service.AgentProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AgentProcessingScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentProcessingScheduler.class);
    
    @Autowired
    private AgentProcessingService processingService;
    
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void processAgents() {
        logger.info("Starting scheduled agent processing");
        try {
            processingService.processUnprocessedAgents();
            logger.info("Completed scheduled agent processing");
        } catch (Exception e) {
            logger.error("Error during scheduled agent processing", e);
        }
    }
}