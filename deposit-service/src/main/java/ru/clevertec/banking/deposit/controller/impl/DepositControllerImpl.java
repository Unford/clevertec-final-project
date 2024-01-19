package ru.clevertec.banking.deposit.controller.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.banking.deposit.controller.DepositController;
import ru.clevertec.banking.deposit.model.dto.request.CreateDepositRequest;
import ru.clevertec.banking.deposit.model.dto.request.UpdateDepositRequest;
import ru.clevertec.banking.deposit.model.dto.response.DepositResponse;
import ru.clevertec.banking.deposit.service.DepositService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/deposits")
@RequiredArgsConstructor
public class DepositControllerImpl implements DepositController {
    private final DepositService depositService;

    @Override
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public Page<DepositResponse> findAll(Pageable pageable) {
        return depositService.findPageByRole(pageable, SecurityContextHolder.getContext().getAuthentication());
    }


    @Override
    @GetMapping("/{iban}")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR " +
            "@customSecurityExpression.hasUserRoleAndOwnDeposit(#iban, authentication)")
    public DepositResponse findByAccountIban(@PathVariable String iban) {
        return depositService.findByAccountIban(iban);
    }

    @Override
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR " +
            "@customSecurityExpression.hasUserRoleAndIdEquals(#customerId, authentication)")
    public List<DepositResponse> findAllByCustomerId(@PathVariable UUID customerId) {
        return depositService.findAllByCustomerId(customerId);
    }

    @Override
    @DeleteMapping("/{iban}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_SUPER_USER')")
    public void deleteByAccountIban(@PathVariable String iban) {
        depositService.deleteByAccountIban(iban);
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN') OR " +
            "@customSecurityExpression.hasUserRoleAndIdEquals(#createDepositRequest.customerId, authentication)")
    public DepositResponse createDeposit(@RequestBody @Valid CreateDepositRequest createDepositRequest) {
        return depositService.save(createDepositRequest);

    }

    @Override
    @PatchMapping("/{iban}")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR " +
            "@customSecurityExpression.hasUserRoleAndOwnDeposit(#iban, authentication)")
    public DepositResponse updateDeposit(@PathVariable String iban,
                                         @RequestBody @Valid UpdateDepositRequest updateDepositRequest) {
        return depositService.update(iban, updateDepositRequest);
    }

}
