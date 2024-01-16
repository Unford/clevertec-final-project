package ru.clevertec.banking.deposit.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.clevertec.banking.deposit.model.dto.response.DepositResponse;
import ru.clevertec.banking.deposit.service.DepositService;
import ru.clevertec.banking.security.model.Role;

import java.util.UUID;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class CustomSecurityExpression {
    private final DepositService depositService;


    public boolean hasUserRoleAndIdEquals(final UUID customerId) {
        return checkUserRoleAndApply(authentication ->
               customerId.equals(authentication.getPrincipal()));

    }

    public boolean hasUserRoleAndOwnDeposit(final String iban) {
        return checkUserRoleAndApply((authentication -> {
            UUID userId = (UUID) authentication.getPrincipal();
            DepositResponse deposit = depositService.findByAccountIban(iban);
            return deposit.getCustomerId().equals(userId);
        }));
    }



    private boolean checkUserRoleAndApply(Predicate<Authentication> predicate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().contains(Role.USER.toAuthority())) {
            return predicate.test(authentication);
        }
        return false;
    }

}
