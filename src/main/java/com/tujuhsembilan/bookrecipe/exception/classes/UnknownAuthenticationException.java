package com.tujuhsembilan.bookrecipe.exception.classes;

public class UnknownAuthenticationException extends RuntimeException{
    public UnknownAuthenticationException(String message){
        super(message);
    }
}
