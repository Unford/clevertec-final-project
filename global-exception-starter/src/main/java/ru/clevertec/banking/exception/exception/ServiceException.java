package ru.clevertec.banking.exception.exception;

import org.springframework.http.HttpStatus;

public abstract class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public abstract HttpStatus getHttpStatus();
}
