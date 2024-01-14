package ru.clevertec.banking.auth.exception;


import org.springframework.http.HttpStatus;
import ru.clevertec.banking.advice.exception.ServiceException;

public class RefreshTokenException extends ServiceException {

    public RefreshTokenException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
