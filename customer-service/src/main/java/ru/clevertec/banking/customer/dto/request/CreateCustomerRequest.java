package ru.clevertec.banking.customer.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.*;
import lombok.*;
import ru.clevertec.banking.customer.entity.CustomerType;
import ru.clevertec.banking.customer.validator.CreateCustomerProfileValidation;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@CreateCustomerProfileValidation
public class CreateCustomerRequest {

    @JsonProperty("customer_id")
    private UUID id;

    @NotBlank
    @Size(max = 10, message = "Customer type should be less than 10 characters")
    @JsonProperty("customer_type")
    private String customerType;

    @Size(max = 100, message = "UNP should be less than 100 characters")
    private String unp;

    @NotNull
    @JsonFormat(pattern = "dd.MM.yyyy")
    @PastOrPresent(message = "Register date should be in the past or today")
    @JsonProperty("register_date")
    private LocalDate registerDate;

    @Email(message = "Email should be valid")
    @NotBlank
    @Size(max = 100, message = "Email should be less than 100 characters")
    private String email;

    @NotBlank
    @Size(max = 10, message = "Phone code should be less than 10 characters")
    private String phoneCode;

    @NotBlank
    @Size(max = 20, message = "Phone number should be less than 20 characters")
    private String phoneNumber;

    @NotBlank
    @Size(max = 255, message = "Customer fullname should be less than 255 characters")
    @JsonProperty("customer_fullname")
    private String customerFullname;

    @JsonSetter(nulls = Nulls.SKIP)
    public void setUnp(String unp) {
        this.unp = (Objects.equals(customerType, CustomerType.LEGAL.toString())) ? unp : null;
    }

    @JsonSetter(nulls = Nulls.FAIL)
    public void setCustomerType(String customerType) {
        this.customerType = customerType.toUpperCase().trim();
    }
}