package com.RollinMoment.RollinMomentServer.exception.member;

public class InvalidEmailException extends RuntimeException{
    public InvalidEmailException(String message) {
        super(message);
    }
}
