package ru.clevertec.banking.exception;


public class RefreshTokenException extends RuntimeException {
    public RefreshTokenException(String message) {
        super(message);
    }
}
