package ru.clevertec.banking.exception.handler;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ApiErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request,
                                                  ErrorAttributeOptions options) {
        return super.getErrorAttributes(
                        request, options.including(ErrorAttributeOptions.Include.MESSAGE))
                .entrySet().stream()
                .filter(e -> e.getKey().equals("message") || e.getKey().equals("status"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }

}
