package ru.kata.spring.boot_security.demo.controller;

public class DeletingResult {
    private String message;
    private boolean currentUserDeleted;

    public DeletingResult(String message, boolean currentUserDeleted) {
        this.message = message;
        this.currentUserDeleted = currentUserDeleted;
    }

    public String getMessage() {
        return message;
    }

    public boolean isCurrentUserDeleted() {
        return currentUserDeleted;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCurrentUserDeleted(boolean currentUserDeleted) {
        this.currentUserDeleted = currentUserDeleted;
    }
}
