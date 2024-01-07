package ru.clevertec.banking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.clevertec.banking.dto.UserCredentialsDto;
import ru.clevertec.banking.dto.request.AuthenticationRequest;
import ru.clevertec.banking.dto.response.AuthenticationResponse;
import ru.clevertec.banking.entity.Role;
import ru.clevertec.banking.exception.RefreshTokenException;

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

    public AuthenticationResponse refresh(String refreshToken) {
        if (refreshService.isRefreshTokenNotExpired(refreshToken)) {
            Long userId = refreshService.extractId(refreshToken);
            if (refreshService.isRefreshTokenValid(refreshToken)) {

                String newAccessToken = jwtService.generateToken(userId, refreshService.extractAuthorities(refreshToken));
                String newRefreshToken = refreshService.generateRefreshToken(userId, refreshService.extractAuthorities(refreshToken));

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

    // TODO надо бы переписать этот ужас, но под highload работает в два раза быстрее
    private CompletableFuture<AuthenticationResponse> registerAsync(AuthenticationRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Method to register User started");
            UserCredentialsDto user = new UserCredentialsDto()
                    .setEmail(request.getEmail())
                    .setPassword(passwordEncoder.encode(request.getPassword()))
                    .setRole(Role.USER);

            user = userService.save(user);

            var refreshToken =
                    refreshService.generateRefreshToken(user.getId(), Collections.singletonList(user.getRole()));
            user.setRefreshToken(refreshToken);
            userService.save(user);

            var jwtToken = jwtService.generateToken(user.getId(), Collections.singletonList(user.getRole()));

            log.info("Exiting register method");
            return AuthenticationResponse.builder()
                                         .token(jwtToken)
                                         .refreshToken(refreshToken)
                                         .build();
        });
    }

    private AuthenticationResponse authenticate(AuthenticationRequest request, UserCredentialsDto user) {
        log.info("Method to authenticate User started");
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        var jwtToken = jwtService.generateToken(user.getId(), Collections.singletonList(user.getRole()));
        String newRefreshToken = refreshService.generateRefreshToken(user.getId(), Collections.singletonList(user.getRole()));

        user.setRefreshToken(newRefreshToken);
        userService.save(user);

        log.info("Exiting authenticate method");
        return AuthenticationResponse.builder()
                                     .token(jwtToken)
                                     .refreshToken(newRefreshToken)
                                     .build();
    }
}
