package com.supermarket.shared.exception;

import jakarta.persistence.EntityNotFoundException;

import java.util.UUID;

public class ResourceNotFoundException extends EntityNotFoundException {

    public ResourceNotFoundException(String resource, UUID id) {
        super(String.format("%s not found: %s", resource, id));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
