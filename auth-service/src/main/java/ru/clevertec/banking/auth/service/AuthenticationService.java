package ru.clevertec.banking.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.clevertec.banking.auth.dto.UserCredentialsDto;
import ru.clevertec.banking.auth.dto.message.RegisterMessagePayload;
import ru.clevertec.banking.auth.dto.request.AuthenticationRequest;
import ru.clevertec.banking.auth.dto.response.AuthenticationResponse;
import ru.clevertec.banking.auth.entity.Role;
import ru.clevertec.banking.auth.exception.RefreshTokenException;
import ru.clevertec.banking.auth.exception.UserOperationException;
import ru.clevertec.banking.auth.util.PasswordGenerator;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserService userService;
    private final RefreshTokenService refreshService;
    private final JwtTokenService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("Method to authenticate User started");
        UserCredentialsDto user = userService.getByEmail(request.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        return Optional.of(user)
                       .map(this::generateAndSetRefreshToken)
                       .map(userService::save)
                       .map(this::buildResultedAuthenticationResponse)
                       .orElseThrow(() -> new UserOperationException("Failed to authenticate user"));
    }

    @Async
    public void registerAsync(RegisterMessagePayload payload) {
        log.info("Method to register User started");

        Optional.of(payload)
                .map(this::buildUserCredentials)
                .map(this::generateAndSetRefreshToken)
                .map(userService::save)
                .ifPresentOrElse(
                        user -> log.info("User successfully registered: {}", user),
                        () -> log.error("Failed to register user")
                );
    }

    public void registerAdmins(UserCredentialsDto admin) {
        UserCredentialsDto userCredentialsDto = generateAndSetRefreshToken(admin);
        userService.save(userCredentialsDto);
    }

    private UserCredentialsDto buildUserCredentials(RegisterMessagePayload payload) {
        return new UserCredentialsDto().setId(payload.getId())
                                       .setEmail(payload.getEmail())
                                       .setRole(Role.USER)
                                       .setPassword(PasswordGenerator.generatePassword(16));
    }

    private UserCredentialsDto generateAndSetRefreshToken(UserCredentialsDto user) {
        String refreshToken =
                refreshService.generateRefreshToken(user.getId(), Collections.singletonList(user.getRole()));
        user.setRefreshToken(refreshToken);
        return user;
    }

    private String generateJwtToken(UserCredentialsDto user) {
        return jwtService.generateToken(user.getId(), Collections.singletonList(user.getRole()));
    }

    private AuthenticationResponse buildResultedAuthenticationResponse(UserCredentialsDto savedUser) {
        String jwtToken = generateJwtToken(savedUser);
        log.info("Exiting authenticate method");
        return AuthenticationResponse.builder()
                                     .token(jwtToken)
                                     .refreshToken(savedUser.getRefreshToken())
                                     .build();
    }

    public AuthenticationResponse refresh(String refreshToken) {
        if (refreshService.isRefreshTokenNotExpired(refreshToken)) {
            UUID userId = refreshService.extractId(refreshToken);
            if (refreshService.isRefreshTokenValid(refreshToken)) {

                String newAccessToken =
                        jwtService.generateToken(userId, refreshService.extractAuthorities(refreshToken));
                String newRefreshToken =
                        refreshService.generateRefreshToken(userId, refreshService.extractAuthorities(refreshToken));

                refreshService.updateRefreshToken(newRefreshToken);

                return AuthenticationResponse.builder()
                                             .token(newAccessToken)
                                             .refreshToken(newRefreshToken)
                                             .build();
            } else {
                log.warn("RefreshTokenException");
                throw new RefreshTokenException("Refresh token incorrect");
            }
        } else {
            log.warn("RefreshTokenException");
            throw new RefreshTokenException("Refresh token expired");
        }
    }
}
