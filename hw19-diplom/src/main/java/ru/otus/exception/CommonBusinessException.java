package ru.otus.exception;

public class CommonBusinessException extends RuntimeException {
    public CommonBusinessException(String message) {
        super(message);
    }
}
