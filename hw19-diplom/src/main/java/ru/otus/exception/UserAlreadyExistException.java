package ru.otus.exception;

public class UserAlreadyExistException extends CommonBusinessException {
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
