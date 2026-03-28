package com.example.Product.Exceptions;

public class AccessDeniedException extends RuntimeException{
    public AccessDeniedException (String message){
        super(message);
    }
}
