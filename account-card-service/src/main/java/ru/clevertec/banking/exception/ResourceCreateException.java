package ru.clevertec.banking.exception;

import org.springframework.http.HttpStatus;
import ru.clevertec.banking.advice.exception.ServiceException;

public class ResourceCreateException extends ServiceException {
    public ResourceCreateException(String message) {
        super(message);
    }

    public ResourceCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceCreateException(Throwable cause) {
        super(cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
