package ru.clevertec.banking.customer.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import ru.clevertec.banking.customer.entity.CustomerType;
import ru.clevertec.banking.customer.exception.InvalidCustomerTypeException;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCustomersPageableRequest {

    @DateTimeFormat(pattern = "dd.MM.yyyy")
    @PastOrPresent(message = "Register date should be in the past or today")
    private LocalDate registerDate;

    private String customerType;

    @JsonIgnore
    private CustomerType customerTypeEnum;

    @PositiveOrZero
    private Integer page = 0;

    @Positive
    private Integer size = 5;

    public Pageable toPageable() {
        return PageRequest.of(page, size);
    }

    @JsonSetter
    public void setCustomerType(String customerType) {
        this.customerType = Objects.nonNull(customerType) ?
                            customerType.toUpperCase()
                                        .trim() : null;

        setCustomerTypeEnum(this.customerType);
    }

    public void setCustomerTypeEnum(String customerType) {
         switch (customerType) {
            case "LEGAL" -> this.customerTypeEnum = CustomerType.valueOf("LEGAL");
            case "PHYSIC" -> this.customerTypeEnum = CustomerType.valueOf("PHYSIC");
            default -> throw new InvalidCustomerTypeException(String.format("Invalid customer type: %s", customerType));
        }
    }
}
