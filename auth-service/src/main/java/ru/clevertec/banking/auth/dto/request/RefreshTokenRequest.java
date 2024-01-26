package ru.clevertec.banking.auth.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {
    String refreshToken;
}