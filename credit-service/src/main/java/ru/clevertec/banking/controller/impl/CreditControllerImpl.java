package ru.clevertec.banking.controller.impl;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.banking.controller.CreditController;
import ru.clevertec.banking.dto.CreditRequest;
import ru.clevertec.banking.dto.CreditRequestForUpdate;
import ru.clevertec.banking.dto.CreditResponse;
import ru.clevertec.banking.service.CreditService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/credits")
public class CreditControllerImpl implements CreditController {
    private final CreditService service;

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_USER')")
    @GetMapping
    public Page<CreditResponse> getAll(@PageableDefault(sort = {"contractNumber"}) Pageable pageable) {
        return service.getAll(pageable);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_USER') or authentication.principal.equals(#request.customer_id())" +
            " and hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CreditResponse create(@RequestBody @Valid CreditRequest request) {
        return service.save(request);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_USER') or authentication.principal.equals(" +
            "@creditServiceImpl.findByContractNumber(#request.contractNumber()).customer_id()) and hasRole('ROLE_USER')")
    @PatchMapping
    public CreditResponse update(@RequestBody @Valid CreditRequestForUpdate request) {
        return service.update(request);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_SUPER_USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{contractNumber}")
    public void deleteByContractNumber(@PathVariable String contractNumber) {
        service.delete(contractNumber);
    }

    @Override
    @PostAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_USER') or authentication.principal.equals(returnObject.customer_id()) " +
            "and hasRole('ROLE_USER')")
    @GetMapping("/by-contract-number/{contractNumber}")
    public CreditResponse getByContractNumber(@PathVariable String contractNumber) {
        return service.findByContractNumber(contractNumber);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_USER') or authentication.principal.equals(#customerId) " +
            "and hasRole('ROLE_USER')")
    @GetMapping("/by-customer-id/{customerId}")
    public List<CreditResponse> getByCustomerId(@PathVariable UUID customerId) {
        return service.findByCustomer(customerId);
    }
}
