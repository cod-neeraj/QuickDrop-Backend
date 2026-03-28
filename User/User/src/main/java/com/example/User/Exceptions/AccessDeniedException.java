package com.example.User.Exceptions;

public class AccessDeniedException extends RuntimeException{
    public AccessDeniedException (String message){
        super(message);
    }
}
