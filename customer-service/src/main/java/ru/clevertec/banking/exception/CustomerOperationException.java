package ru.clevertec.banking.exception;


import org.springframework.http.HttpStatus;
import ru.clevertec.banking.advice.exception.ServiceException;

public class CustomerOperationException extends ServiceException {
    public CustomerOperationException(String message) {
        super(message);
    }

    public CustomerOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomerOperationException(Throwable cause) {
        super(cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
