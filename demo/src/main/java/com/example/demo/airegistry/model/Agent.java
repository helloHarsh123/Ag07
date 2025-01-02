package com.example.demo.airegistry.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import java.util.List;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Document(collection = "agents")
public class Agent {
    @Id
    private String id;
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @NotEmpty(message = "At least one use case is required")
    private List<String> useCases;
    
    @NotEmpty(message = "At least one example prompt is required")
    private List<String> examplePrompts;
    
    @NotNull(message = "API details are required")
    private ApiDetails apiDetails;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    private boolean active = true;
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getUseCases() { return useCases; }
    public void setUseCases(List<String> useCases) { this.useCases = useCases; }
    public List<String> getExamplePrompts() { return examplePrompts; }
    public void setExamplePrompts(List<String> examplePrompts) { this.examplePrompts = examplePrompts; }
    public ApiDetails getApiDetails() { return apiDetails; }
    public void setApiDetails(ApiDetails apiDetails) { this.apiDetails = apiDetails; }
    public boolean getActive(){ return this.active; };
    public void setActive(boolean status){ this.active = status; }
}
