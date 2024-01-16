package ru.clevertec.banking.deposit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.banking.deposit.model.dto.request.CreateDepositRequest;
import ru.clevertec.banking.deposit.model.dto.request.UpdateDepositRequest;
import ru.clevertec.banking.deposit.model.dto.response.DepositResponse;
import ru.clevertec.banking.deposit.service.DepositService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/deposits")
@RequiredArgsConstructor
public class DepositController {
    private final DepositService depositService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Page<DepositResponse> findAll(Pageable pageable, Authentication authentication) {
        return depositService.findPageByRole(pageable, authentication);
    }


    @GetMapping("/{iban}")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR " +
            "@customSecurityExpression.hasUserRoleAndOwnDeposit(#iban)")
    public DepositResponse findByAccountIban(@PathVariable String iban) {
        return depositService.findByAccountIban(iban);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR " +
            "@customSecurityExpression.hasUserRoleAndIdEquals(#customerId)")
    public List<DepositResponse> findAllByCustomerId(@PathVariable UUID customerId) {
        return depositService.findAllByCustomerId(customerId);
    }

    @DeleteMapping("/{iban}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_SUPER_USER')")
    public void deleteByAccountIban(@PathVariable String iban) {
        depositService.deleteByAccountIban(iban);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN') OR " +
            "@customSecurityExpression.hasUserRoleAndIdEquals(#createDepositRequest.customerId)")
    public DepositResponse createDeposit(@RequestBody @Valid CreateDepositRequest createDepositRequest) {
        return depositService.save(createDepositRequest);
    }

    @PatchMapping("/{iban}")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR " +
            "@customSecurityExpression.hasUserRoleAndOwnDeposit(#iban)")
    public DepositResponse updateDeposit(@PathVariable String iban,
                                         @RequestBody @Valid UpdateDepositRequest updateDepositRequest) {
        return depositService.update(iban, updateDepositRequest);
    }

}
