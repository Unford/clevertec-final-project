package ru.clevertec.banking.exception;

import org.springframework.http.HttpStatus;

public class GatewayAuthorizationException extends GatewayException {
    public GatewayAuthorizationException() {
        super();
    }

    public GatewayAuthorizationException(String message) {
        super(message);
    }

    public GatewayAuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public GatewayAuthorizationException(Throwable cause) {
        super(cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
