package ru.clevertec.banking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.banking.dto.account.AccountRequest;
import ru.clevertec.banking.dto.account.AccountRequestForUpdate;
import ru.clevertec.banking.dto.account.AccountResponse;
import ru.clevertec.banking.dto.account.AccountWithCardResponse;
import ru.clevertec.banking.service.AccountService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    private final AccountService service;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_USER') or authentication.principal.equals(#request.customer_id())" +
            " and hasRole('ROLE_USER')")
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping
    public AccountResponse create(@RequestBody @Valid AccountRequest request) {
        log.info(request.toString());
        return service.save(request);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_USER')")
    @GetMapping
    public Page<AccountWithCardResponse> getAll(@PageableDefault(sort = {"iban"}) Pageable pageable) {
        return service.getAll(pageable);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_USER') or authentication.principal.equals(#uuid) " +
            "and hasRole('ROLE_USER')")
    @GetMapping("/by-customer-id/{uuid}")
    public List<AccountWithCardResponse> findByCustomer(@PathVariable UUID uuid) {
        return service.findByCustomer(uuid);
    }

    @PostAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_USER') or authentication.principal.equals(returnObject.customer_id()) " +
            "and hasRole('ROLE_USER')")
    @GetMapping("/by-iban/{iban}")
    public AccountResponse findByIban(@PathVariable String iban) {
        return service.findByIban(iban);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_USER') or authentication.principal.equals(" +
            "@accountServiceImpl.findByIban(#request.iban()).customer_id()) and hasRole('ROLE_USER')")
    @PatchMapping
    public AccountResponse update(@RequestBody @Valid AccountRequestForUpdate request) {
        return service.update(request);
    }

    @PreAuthorize("hasRole('ROLE_SUPER_USER')")
    @DeleteMapping({"/{iban}"})
    public void delete(@PathVariable String iban) {
        service.deleteByIban(iban);
    }

}
