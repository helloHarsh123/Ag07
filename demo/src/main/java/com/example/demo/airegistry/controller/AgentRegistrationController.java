package com.example.demo.airegistry.controller;

import com.example.demo.airegistry.model.Agent;
import com.example.demo.airegistry.service.AgentRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/agents")
public class AgentRegistrationController {
    
    @Autowired
    private AgentRegistrationService registrationService;
    
    @PostMapping
    public ResponseEntity<Agent> registerAgent(@Valid @RequestBody Agent agent) {
        return ResponseEntity.ok(registrationService.registerAgent(agent));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Agent> updateAgent(
            @PathVariable String id,
            @Valid @RequestBody Agent agent) {
        return ResponseEntity.ok(registrationService.updateAgent(id, agent));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgent(@PathVariable String id) {
        registrationService.deleteAgent(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Agent> getAgent(@PathVariable String id) {
        return ResponseEntity.ok(registrationService.getAgent(id));
    }
    
    @GetMapping
    public ResponseEntity<Page<Agent>> getAllAgents(Pageable pageable) {
        return ResponseEntity.ok(registrationService.getAllAgents(pageable));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Agent>> searchByUseCase(
            @RequestParam String useCase) {
        return ResponseEntity.ok(registrationService.findAgentsByUseCase(useCase));
    }
}
