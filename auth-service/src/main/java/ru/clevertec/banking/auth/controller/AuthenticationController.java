package ru.clevertec.banking.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.banking.auth.dto.request.AuthenticationRequest;
import ru.clevertec.banking.auth.dto.request.RefreshTokenRequest;
import ru.clevertec.banking.auth.dto.response.AuthenticationResponse;
import ru.clevertec.banking.auth.service.AuthenticationService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signing")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponse registerOrAuthenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return authenticationService.authenticate(request);
    }

    @PostMapping("/token-refreshing")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponse refresh(
            @RequestBody RefreshTokenRequest refreshTokenRequest
    ) {
        return authenticationService.refresh(refreshTokenRequest.getRefreshToken());
    }
}
