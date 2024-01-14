package ru.clevertec.banking.customer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.clevertec.banking.customer.dto.UserCredentialsDto;
import ru.clevertec.banking.customer.dto.request.AuthenticationRequest;
import ru.clevertec.banking.customer.dto.response.AuthenticationResponse;
import ru.clevertec.banking.customer.entity.Role;
import ru.clevertec.banking.customer.exception.RefreshTokenException;
import ru.clevertec.banking.customer.exception.UserOperationException;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserService userService;
    private final RefreshTokenService refreshService;
    private final JwtTokenService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public CompletableFuture<AuthenticationResponse> registerOrAuthenticateAsync(AuthenticationRequest request) {
        Optional<UserCredentialsDto> user = userService.getOptionalByEmail(request.getEmail());

        return user.map(userEntity -> CompletableFuture.completedFuture(authenticate(request, user.get())))
                   .orElseGet(() -> registerAsync(request));
    }

    private CompletableFuture<AuthenticationResponse> registerAsync(AuthenticationRequest request) {
        log.info("Method to register User started");

        return CompletableFuture.supplyAsync(() -> buildUserCredentials(request))
                                .thenApplyAsync(user -> {
                                    String encodedPassword = passwordEncoder.encode(request.getPassword());
                                    user.setPassword(encodedPassword);
                                    return user;
                                })
                                .thenApplyAsync(userService::save)
                                .thenApplyAsync(this::generateAndSetRefreshToken)
                                .thenApplyAsync(userService::save)
                                .thenApplyAsync(this::buildResultedAuthenticationResponse);
    }

    private AuthenticationResponse authenticate(AuthenticationRequest request, UserCredentialsDto user) {
        log.info("Method to authenticate User started");
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        return Optional.of(user)
                       .map(this::generateAndSetRefreshToken)
                       .map(userService::save)
                       .map(this::buildResultedAuthenticationResponse)
                       .orElseThrow(() -> new UserOperationException("User not found"));
    }

    private UserCredentialsDto buildUserCredentials(AuthenticationRequest request) {
        return new UserCredentialsDto().setEmail(request.getEmail())
                                       .setRole(Role.USER);
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
            Long userId = refreshService.extractId(refreshToken);
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
