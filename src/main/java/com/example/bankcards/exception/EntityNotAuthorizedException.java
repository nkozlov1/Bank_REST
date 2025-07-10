package com.example.bankcards.exception;

public class EntityNotAuthorizedException extends RuntimeException {
    public EntityNotAuthorizedException(String username) {
        super(username);
    }
}
