package ru.clevertec.banking.exception;

import org.springframework.http.HttpStatus;
import ru.clevertec.banking.advice.exception.ServiceException;

public class CreditOperationException extends ServiceException {
    public CreditOperationException(String message) {
        super(message);
    }

    public CreditOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreditOperationException(Throwable cause) {
        super(cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
