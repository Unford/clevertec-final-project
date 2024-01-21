package ru.clevertec.banking.customer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.clevertec.banking.customer.client.configuration.FeignConfiguration;
import ru.clevertec.banking.customer.dto.response.client.credit.CreditResponse;

import java.util.List;
import java.util.UUID;

@FeignClient(value = "${clevertec.feign.client.credit.name}",
             path = "${clevertec.feign.client.credit.path}",
             configuration = FeignConfiguration.class)
public interface CreditClient {

    @GetMapping(value = "/by-customer-id/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<CreditResponse> findAllByCustomerId(@PathVariable("customerId") UUID customerId);
}

