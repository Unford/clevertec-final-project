package ru.clevertec.banking.advice.exception;

import org.springframework.http.HttpStatus;

public class ResourceUniqueException extends ServiceException{
    public ResourceUniqueException(String message) {
        super(message);
    }

    public ResourceUniqueException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceUniqueException(Throwable cause) {
        super(cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
