package com.example.demo.exception;

public class EntityNotAllowedException extends Exception {

    public EntityNotAllowedException() {
        super();
    }

    public EntityNotAllowedException(String message) {
        super(message);
    }

    public EntityNotAllowedException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public EntityNotAllowedException(Throwable throwable) {
        super(throwable);
    }
}
