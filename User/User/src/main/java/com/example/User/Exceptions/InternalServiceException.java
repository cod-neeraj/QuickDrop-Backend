package com.example.User.Exceptions;

public class InternalServiceException extends RuntimeException {
    public InternalServiceException(String message) {
        super(message);
    }
}
