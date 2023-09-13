package com.example.bookstore.exception;

public class RegistrationException extends Exception {
    public RegistrationException() {
        super();
    }

    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(String message, Throwable ex) {
        super(message, ex);
    }
}
