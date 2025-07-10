package com.example.bankcards.exception;

public class UserNotAuthorizedException extends EntityNotAuthorizedException {
    public UserNotAuthorizedException(String username) {
        super("User:" + username + " is not authorized");
    }
}
