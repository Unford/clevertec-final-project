package ru.clevertec.banking.auth.exception;

import org.springframework.http.HttpStatus;
import ru.clevertec.banking.advice.exception.ServiceException;

public class UserOperationException extends ServiceException {

    public UserOperationException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
