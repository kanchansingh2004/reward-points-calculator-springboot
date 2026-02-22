package com.retail.rewards.exception;

/**
 * Custom exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Constructor with error message.
     * 
     * @param message the error message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
