package ru.clevertec.banking.exception.handler;


import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.reactive.result.view.ViewResolver;
import reactor.core.publisher.Mono;
import ru.clevertec.banking.exception.GatewayAuthorizationException;
import ru.clevertec.banking.exception.GatewayException;

import java.util.Map;
import java.util.Optional;

@Component
@Order(-2)
public class GlobalWebExceptionHandler extends
        AbstractErrorWebExceptionHandler {
    private static final String STATUS = "status";

    public GlobalWebExceptionHandler(ApiErrorAttributes apiErrorAttributes,
                                     WebProperties webProperties,
                                     ApplicationContext applicationContext,
                                     ServerCodecConfigurer configurer,
                                     ObjectProvider<ViewResolver> viewResolvers) {
        super(apiErrorAttributes, webProperties.getResources(), applicationContext);
        this.setMessageWriters(configurer.getWriters());
        this.setMessageReaders(configurer.getReaders());
        this.setViewResolvers(viewResolvers.orderedStream().toList());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(
            ErrorAttributes errorAttributes) {
        return RouterFunctions.route(
                RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(
            ServerRequest request) {
        Map<String, Object> errorPropertiesMap = getErrorAttributes(request,
                ErrorAttributeOptions.defaults());
        HttpStatus status = this.getStatus(errorPropertiesMap, request);
        errorPropertiesMap.put(STATUS, status.value());

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorPropertiesMap));
    }

    private HttpStatus getStatus(Map<String, Object> errorPropertiesMap, ServerRequest request) {
        Optional<Object> optionalException = request.attribute(ErrorAttributes.ERROR_ATTRIBUTE);

        if (optionalException.isPresent() &&
                optionalException.get() instanceof GatewayException authorizationException) {
            return authorizationException.getHttpStatus();
        }
        HttpStatus status = HttpStatus.resolve((Integer) errorPropertiesMap.get(STATUS));
        return status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status;
    }
}
