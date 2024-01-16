package ru.clevertec.banking.customer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.clevertec.banking.customer.client.configuration.FeignConfiguration;
import ru.clevertec.banking.customer.dto.response.client.account.AccountWithCardResponse;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "${clevertec.feign.client.account-card.name}",
             path = "${clevertec.feign.client.account-card.path}",
             configuration= FeignConfiguration.class)
public interface AccountAndLinkedCardsClient {

    @GetMapping(value = "/by-customer-id/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<AccountWithCardResponse> findAllByCustomerId(@PathVariable("uuid") UUID customerId);
}
