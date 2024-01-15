package ru.clevertec.banking.deposit.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.clevertec.banking.deposit.model.dto.response.CustomerResponse;

import java.util.UUID;

@FeignClient(name = "${clevertec.feign.client.customer.name}",
        path = "${clevertec.feign.client.customer.path}")
public interface CustomerClient {

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    CustomerResponse findByCustomerId(@PathVariable("id") UUID customerId);
}
