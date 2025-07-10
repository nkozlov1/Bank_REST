package com.example.bankcards.exception;

public class PermissionNotAllowed extends RuntimeException {
    public PermissionNotAllowed(Long id) {
        super("Not allowed permission for this card:" + id);
    }
}
