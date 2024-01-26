package ru.clevertec.banking.customer.exception;

import org.springframework.http.HttpStatus;
import ru.clevertec.banking.advice.exception.ServiceException;

public class InvalidCustomerTypeException extends ServiceException {
    public InvalidCustomerTypeException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}