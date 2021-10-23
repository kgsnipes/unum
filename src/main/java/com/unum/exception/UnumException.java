package com.unum.exception;

public class UnumException extends Exception {

    public UnumException(String message) {
        super(message);
    }

    public UnumException(String message, Throwable cause) {
        super(message, cause);
    }
}
