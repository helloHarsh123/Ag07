package com.example.demo.airegistry.model;

import java.util.Map;

import javax.validation.constraints.*;

public class ApiDetails {
    @NotBlank(message = "API endpoint is required")
    @Pattern(regexp = "^https?://.*", message = "Endpoint must be a valid HTTP/HTTPS URL")
    private String endpoint;
    
    @NotBlank(message = "Authentication type is required")
    @Pattern(regexp = "^(Bearer|Basic|API-Key|None)$", message = "Invalid authentication type")
    private String authType;
    
    @NotBlank(message = "API version is required")
    private String apiVersion;
    
    private Map<String, String> headers;
    
    @Min(value = 0, message = "Timeout cannot be negative")
    @Max(value = 300000, message = "Timeout cannot exceed 5 minutes")
    private int timeoutMs = 30000;
    
    // Getters and Setters
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getAuthType() { return authType; }
    public void setAuthType(String authType) { this.authType = authType; }
    public String getApiVersion() { return apiVersion; }
    public void setApiVersion(String apiVersion) { this.apiVersion = apiVersion; }
    public Map<String, String> getHeaders() { return headers; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }
    public int getTimeoutMs(){return this.timeoutMs;}
    public void setTimeoutMs(int timeout){this.timeoutMs = timeout;}
}