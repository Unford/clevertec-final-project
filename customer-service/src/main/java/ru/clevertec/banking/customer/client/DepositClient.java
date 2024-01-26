package ru.clevertec.banking.customer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.clevertec.banking.customer.client.configuration.FeignConfiguration;
import ru.clevertec.banking.customer.dto.response.client.deposit.DepositResponse;

import java.util.List;
import java.util.UUID;

@FeignClient(value = "${clevertec.feign.client.deposit.name}",
             path = "${clevertec.feign.client.deposit.path}",
             configuration = FeignConfiguration.class)
public interface DepositClient {

    @GetMapping(value = "/customer/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<DepositResponse> findAllByCustomerId(@PathVariable("customerId") UUID customerId);

}
