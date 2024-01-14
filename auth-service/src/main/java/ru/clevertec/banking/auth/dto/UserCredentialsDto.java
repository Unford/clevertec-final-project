package ru.clevertec.banking.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ru.clevertec.banking.auth.entity.Role;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserCredentialsDto implements Serializable {
    private UUID id;

    @NotNull
    @Email(message = "Not correct email provided")
    private String email;

    @NotNull
    @Size(message = "Password need to be between 4 and 100 characters", min = 4, max = 100)
    private String password;

    @NotNull
    private Role role;

    private String refreshToken;
}