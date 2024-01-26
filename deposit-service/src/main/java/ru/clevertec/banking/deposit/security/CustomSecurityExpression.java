package ru.clevertec.banking.deposit.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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


    public boolean hasUserRoleAndIdEquals(final UUID customerId, Authentication authentication) {
        return checkUserRoleAndApply(authentication, a ->
                customerId.equals(a.getPrincipal()));

    }

    public boolean hasUserRoleAndOwnDeposit(final String iban, Authentication authentication) {
        return checkUserRoleAndApply(authentication, (a -> {
            UUID userId = (UUID) a.getPrincipal();
            DepositResponse deposit = depositService.findByAccountIban(iban);
            return deposit.getCustomerId().equals(userId);
        }));
    }


    private boolean checkUserRoleAndApply(Authentication authentication, Predicate<Authentication> predicate) {
        if (authentication.getAuthorities().contains(Role.USER.toAuthority())) {
            return predicate.test(authentication);
        }
        return false;
    }

}
