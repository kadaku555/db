package com.example.demo.exception;

public class EntityAlreadyExistsException extends Exception {

    public EntityAlreadyExistsException() {
        super();
    }

    public EntityAlreadyExistsException(String message) {
        super(message);
    }

    public EntityAlreadyExistsException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public EntityAlreadyExistsException(Throwable throwable) {
        super(throwable);
    }
}
