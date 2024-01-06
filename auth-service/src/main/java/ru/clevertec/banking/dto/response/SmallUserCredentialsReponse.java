package ru.clevertec.banking.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ru.clevertec.banking.entity.Role;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SmallUserCredentialsReponse implements Serializable {
    private Long id;

    @NotNull
    @Email(message = "Not correct email provided")
    private String email;

    @NotNull
    private Role role;
}
