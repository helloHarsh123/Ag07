package com.example.demo.airegistry.exception;

public class AgentAlreadyExistsException extends RuntimeException {
    public AgentAlreadyExistsException(String message) {
        super(message);
    }
}
