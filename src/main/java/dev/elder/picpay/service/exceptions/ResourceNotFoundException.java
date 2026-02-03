package dev.elder.picpay.service.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(Object id) {
        super("Resource not found. " + id);
    }
}
