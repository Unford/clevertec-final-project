package ru.clevertec.banking.exception;

public class UserOperationException extends RuntimeException {
    public UserOperationException(String message) {
        super(message);
    }
}
