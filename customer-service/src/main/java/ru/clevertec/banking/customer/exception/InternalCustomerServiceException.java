package ru.clevertec.banking.customer.exception;

import org.springframework.http.HttpStatus;
import ru.clevertec.banking.advice.exception.ServiceException;

public class InternalCustomerServiceException extends ServiceException {
    public InternalCustomerServiceException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
