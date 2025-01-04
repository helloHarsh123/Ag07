package com.example.demo.airegistry.exception;

import java.util.Map;

public class ErrorResponse {
    private String code;
    private String message;
    private Map<String, String> errors;
    
    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public ErrorResponse(String code, String message, Map<String, String> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }
    
    public String getCode(){return this.code;}
    public void setCode(String code){ this.code = code; }
    public String getMessage(){return this.message;}
    public void setMessage(String message){ this.message = message; }
    public Map<String, String> getErrors(){ return this.errors; }
    public void setErrors(Map<String,String> errors){ this.errors = errors; }
}