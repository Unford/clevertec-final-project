package ru.clevertec.banking.auth.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterMessagePayload {

    @JsonProperty("customer_id")
    private UUID id;

    private String email;
}