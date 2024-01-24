package ru.clevertec.banking.customer.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class AuthMessagePayload {

    @JsonProperty("customer_id")
    private UUID id;

    private String email;
}