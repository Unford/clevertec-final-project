package ru.clevertec.banking.customer.dto.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.clevertec.banking.customer.entity.CustomerType;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerMessagePayload {

    @JsonProperty("customer_id")
    private UUID id;

    @JsonProperty("customer_type")
    private String customerType;

    private String unp;

    @JsonProperty("register_date")
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate registerDate;

    private String email;

    private String phoneCode;

    private String phoneNumber;

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