package ru.clevertec.banking.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for {@link ru.clevertec.banking.entity.Customer}
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponse implements Serializable  {

    @JsonProperty("customer_id")
    private UUID id;

    @JsonProperty("customer_type")
    private String customerType;

    private String unp;

    @JsonProperty("register_date")
    private LocalDate registerDate;

    private String email;

    private String phoneCode;

    private String phoneNumber;

    private String customerFullname;
}