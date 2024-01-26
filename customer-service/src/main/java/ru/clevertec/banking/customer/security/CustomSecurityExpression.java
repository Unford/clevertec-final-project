package ru.clevertec.banking.customer.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.clevertec.banking.security.model.Role;

import java.util.UUID;
import java.util.function.Predicate;

@Service
public class CustomSecurityExpression {

    public boolean hasUserRoleAndIdEquals(final UUID customerId) {
        return checkUserRoleAndApply(authentication ->
               customerId.equals(authentication.getPrincipal()));

    }

    private boolean checkUserRoleAndApply(Predicate<Authentication> predicate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().contains(Role.USER.toAuthority())) {
            return predicate.test(authentication);
        }
        return false;
    }

}
