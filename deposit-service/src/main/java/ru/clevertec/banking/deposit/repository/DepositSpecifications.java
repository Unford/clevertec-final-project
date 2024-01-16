package ru.clevertec.banking.deposit.repository;

import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import ru.clevertec.banking.deposit.model.domain.Deposit;
import ru.clevertec.banking.security.model.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class DepositSpecifications {
    public static Specification<Deposit> filterByUserId(Authentication authentication) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (authentication.getAuthorities().contains(Role.USER.toAuthority())) {
                UUID customerId = (UUID) authentication.getPrincipal();
                predicates.add(criteriaBuilder.equal(root.get("customerId"), customerId));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
