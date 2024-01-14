package ru.clevertec.banking.customer.repository;

import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import ru.clevertec.banking.customer.entity.Customer;
import ru.clevertec.banking.customer.entity.CustomerType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@UtilityClass
public class CustomerSpecification {

    public static Specification<Customer> filterChannels(LocalDate registerDate, CustomerType customerType) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Optional.ofNullable(registerDate)
                    .ifPresent(value -> predicates.add(criteriaBuilder.equal(root.get("registerDate"), value)));

            Optional.ofNullable(customerType)
                    .ifPresent(value -> predicates.add(criteriaBuilder.equal(root.get("customerType"), value)));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}