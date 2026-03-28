package com.example.User.Exceptions;

public class MessagePublishException extends RuntimeException {
    public MessagePublishException(String message) {
        super(message);
    }
}
