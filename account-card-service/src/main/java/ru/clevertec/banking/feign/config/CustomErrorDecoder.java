package ru.clevertec.banking.feign.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import ru.clevertec.banking.advice.exception.ResourceNotFoundException;
import ru.clevertec.banking.exception.RestApiServerException;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus responseStatus = HttpStatus.valueOf(response.status());

        if (responseStatus.is5xxServerError()) {
            return new RestApiServerException("Api currency_rate is unavailable", responseStatus);
        } else if (responseStatus.isSameCodeAs(HttpStatus.NOT_FOUND)) {
            return new ResourceNotFoundException("Actual currency rate not found");
        } else return new RestApiServerException("Exception while getting currency rate details",responseStatus);
    }
}
