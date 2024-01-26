package ru.clevertec.banking.exception;

import org.springframework.http.HttpStatus;
import ru.clevertec.banking.advice.exception.ServiceException;

public class RestApiServerException extends ServiceException {
    private HttpStatus status;

    public RestApiServerException(String message) {
        super(message);
    }

    public RestApiServerException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public RestApiServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestApiServerException(Throwable cause) {
        super(cause);
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return status;
    }
}
