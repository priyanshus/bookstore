package com.bookstore.common;

public class EntityNotFound {
    private String error;
    private String message;

    public EntityNotFound(String error, String message) {
        this.error = error;
        this.message = message;
    }

    public EntityNotFound() {
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
