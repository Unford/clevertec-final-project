package ru.clevertec.banking.auth.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import ru.clevertec.banking.auth.dto.UserCredentialsDto;
import ru.clevertec.banking.auth.dto.message.RegisterMessagePayload;
import ru.clevertec.banking.auth.dto.request.AuthenticationRequest;
import ru.clevertec.banking.auth.dto.response.AuthenticationResponse;
import ru.clevertec.banking.auth.entity.Role;
import ru.clevertec.banking.auth.exception.RefreshTokenException;
import ru.clevertec.banking.auth.exception.UserOperationException;
import ru.clevertec.banking.auth.testutil.builders.UserCredentialsDtoTestBuilder;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private JwtTokenService jwtTokenService;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("should authenticate user")
    void shouldAuthenticateUser() {
        //given
        AuthenticationRequest request = AuthenticationRequest.builder()
                                                             .email("email@mail.ru")
                                                             .password("qwerty")
                                                             .build();

        UserCredentialsDto user = new UserCredentialsDtoTestBuilder().build();
        //when
        doReturn(user)
                .when(userService)
                .getByEmail(request.getEmail());

        doReturn(user)
                .when(userService)
                .save(user);

        Authentication mockAuthentication = Mockito.mock(Authentication.class);
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        )).thenReturn(mockAuthentication);

        doReturn("refresh-token")
                .when(refreshTokenService)
                .generateRefreshToken(user.getId(), Collections.singletonList(user.getRole()));

        doReturn("jwt-token")
                .when(jwtTokenService)
                .generateToken(user.getId(), Collections.singletonList(user.getRole()));

        //then
        AuthenticationResponse actualResponse = authenticationService.authenticate(request);
        AuthenticationResponse expectedResponse = new AuthenticationResponse("jwt-token", "refresh-token");

        assertEquals(expectedResponse.getRefreshToken(), actualResponse.getRefreshToken());
        assertEquals(expectedResponse.getToken(), actualResponse.getToken());
    }

    @Test
    @DisplayName("should throw UserOperationException when can't authenticate user")
    void shouldThrowUserOperationExceptionWhenCantAuthenticateUser() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                                                             .email("notFound@mail.ru")
                                                             .password("fake")
                                                             .build();

        UserCredentialsDto user = new UserCredentialsDtoTestBuilder().build();
        //when
        doReturn(user)
                .when(userService)
                .getByEmail(request.getEmail());

        doReturn(null)
                .when(userService)
                .save(user);

        Authentication mockAuthentication = Mockito.mock(Authentication.class);
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        )).thenReturn(mockAuthentication);

        doReturn("refresh-token")
                .when(refreshTokenService)
                .generateRefreshToken(user.getId(), Collections.singletonList(user.getRole()));

        Assertions.assertThrows(UserOperationException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    @DisplayName("should register user async with message from queue")
    void shouldRegisterUserAsyncWithMessageFromQueue() {
        // given
        UUID uuid = UUID.randomUUID();
        UserCredentialsDto user = new UserCredentialsDtoTestBuilder().build();
        user.setId(uuid).setEmail("email@mail.ru");
        RegisterMessagePayload payload = new RegisterMessagePayload(uuid, "email@mail.ru");

        // when
        doReturn("refresh-token")
                .when(refreshTokenService)
                .generateRefreshToken(user.getId(), Collections.singletonList(user.getRole()));

        doReturn(user)
                .when(userService)
                .save(any(UserCredentialsDto.class));

        // then
        authenticationService.registerAsync(payload);

        verify(userService, times(1)).save(any(UserCredentialsDto.class));
    }

    @Test
    @DisplayName("should log fail when register user async with message from queue")
    void shouldLogFailWhenRegisterUserAsyncWithMessageFromQueue() {
        // given
        UUID uuid = UUID.randomUUID();
        UserCredentialsDto user = new UserCredentialsDtoTestBuilder().build();
        user.setId(uuid).setEmail("email@mail.ru");
        RegisterMessagePayload payload = new RegisterMessagePayload(uuid, "email@mail.ru");

        // when
        doReturn("refresh-token")
                .when(refreshTokenService)
                .generateRefreshToken(user.getId(), Collections.singletonList(user.getRole()));

        doReturn(null)
                .when(userService)
                .save(any(UserCredentialsDto.class));

        // then
        authenticationService.registerAsync(payload);

        verify(userService, times(1)).save(any(UserCredentialsDto.class));
    }

    @Test
    @DisplayName("should register admins when app starts")
    void shouldRegisterAdminsWhenAppStarts() {
        //given
        UserCredentialsDto admin = new UserCredentialsDtoTestBuilder().build();

        doReturn("admin-refresh")
                .when(refreshTokenService)
                .generateRefreshToken(admin.getId(), Collections.singletonList(admin.getRole()));

        authenticationService.registerAdmins(admin);

        verify(userService, times(1)).save(admin);
    }

    @Test
    @DisplayName("should refresh token for user")
    void shouldRefreshTokenForUser() {
        //given
        String oldRefresh = "I'm old and need refresh";
        UUID uuidFromRefresh = UUID.randomUUID();
        List<Role> rolesFromRefresh = Collections.singletonList(Role.USER);

        //when
        doReturn(uuidFromRefresh)
                .when(refreshTokenService)
                .extractId(oldRefresh);
        doReturn(rolesFromRefresh)
                .when(refreshTokenService)
                .extractAuthorities(oldRefresh);

        doReturn(true)
                .when(refreshTokenService)
                .isRefreshTokenValid(oldRefresh);
        doReturn(true)
                .when(refreshTokenService)
                .isRefreshTokenNotExpired(oldRefresh);

        String newRefresh = "I'm new refresh";
        String newJwt = "I'm new jwt";
        doReturn(newRefresh)
                .when(refreshTokenService)
                .generateRefreshToken(uuidFromRefresh, rolesFromRefresh);
        doReturn(newJwt)
                .when(jwtTokenService)
                .generateToken(uuidFromRefresh, rolesFromRefresh);

        //then
        AuthenticationResponse expectedResponse = new AuthenticationResponse(newJwt, newRefresh);
        AuthenticationResponse actualResponse = authenticationService.refresh(oldRefresh);
        Assertions.assertEquals(expectedResponse.getRefreshToken(), actualResponse.getRefreshToken());
        Assertions.assertEquals(expectedResponse.getToken(), actualResponse.getToken());
        verify(refreshTokenService, times(1)).updateRefreshToken(newRefresh);
    }

    @Test
    @DisplayName("should throw RefreshTokenException when refresh token incorrect")
    void shouldThrowRefreshTokenExceptionWhenIncorrect() {
        //given
        String oldRefresh = "I'm incorrect refresh";
        UUID uuidFromRefresh = UUID.randomUUID();

        //when
        doReturn(uuidFromRefresh)
                .when(refreshTokenService)
                .extractId(oldRefresh);

        doReturn(true)
                .when(refreshTokenService)
                .isRefreshTokenNotExpired(oldRefresh);
        doReturn(false)
                .when(refreshTokenService)
                .isRefreshTokenValid(oldRefresh);


        //then
        Assertions.assertThrows(RefreshTokenException.class, () -> authenticationService.refresh(oldRefresh), "Refresh token incorrect");
    }

    @Test
    @DisplayName("should throw RefreshTokenException when refresh token expired")
    void shouldThrowRefreshTokenExceptionWhenExpired() {
        //given
        String oldRefresh = "I'm expired refresh";

        //when
        doReturn(false)
                .when(refreshTokenService)
                .isRefreshTokenNotExpired(oldRefresh);

        //then
        Assertions.assertThrows(RefreshTokenException.class, () -> authenticationService.refresh(oldRefresh), "Refresh token expired");
    }
}
