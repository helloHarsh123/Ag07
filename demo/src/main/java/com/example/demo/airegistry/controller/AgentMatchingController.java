package com.example.demo.airegistry.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.airegistry.model.AgentMatchResponse;
import com.example.demo.airegistry.service.AgentMatchingService;

@RestController
@RequestMapping("/api/match")
public class AgentMatchingController {

    @Autowired
    private AgentMatchingService agentMatchingService;

    @PostMapping
    public ResponseEntity<AgentMatchResponse> matchAgentForTask(@RequestBody TaskRequest request) {
        AgentMatchResponse response = agentMatchingService.matchAgentForTask(request.getTask());
        return ResponseEntity.ok(response);
    }
}

class TaskRequest {
    private String task;

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}