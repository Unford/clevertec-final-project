package ru.clevertec.banking.customer.exception;

import org.springframework.http.HttpStatus;
import ru.clevertec.banking.advice.exception.ServiceException;

public class InvalidCustomerTypeException extends ServiceException {
    public InvalidCustomerTypeException(String message) {
        super(message);
    }

    public InvalidCustomerTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCustomerTypeException(Throwable cause) {
        super(cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
