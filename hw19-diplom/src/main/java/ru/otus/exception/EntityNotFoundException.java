package ru.otus.exception;

public class EntityNotFoundException extends CommonBusinessException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
