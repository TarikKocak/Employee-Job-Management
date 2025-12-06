package com.webapp.demo_app.service;

public class IncompleteJobException extends RuntimeException {
    public IncompleteJobException(String message) {
        super(message);
    }
}
