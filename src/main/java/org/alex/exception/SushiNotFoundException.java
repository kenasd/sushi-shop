package org.alex.exception;

public class SushiNotFoundException extends RuntimeException {

    public SushiNotFoundException(String message) {
        super(message);
    }
}
