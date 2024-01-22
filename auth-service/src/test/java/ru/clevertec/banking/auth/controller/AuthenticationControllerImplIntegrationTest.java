package ru.clevertec.banking.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.clevertec.banking.auth.configuration.PostgresContainerConfiguration;
import ru.clevertec.banking.auth.configuration.RabbitMQContainerConfiguration;
import ru.clevertec.banking.auth.dto.request.AuthenticationRequest;
import ru.clevertec.banking.auth.dto.request.RefreshTokenRequest;
import ru.clevertec.banking.auth.repository.RefreshTokenRepository;
import ru.clevertec.banking.auth.repository.UserCredentialsRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@EnableAutoConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {PostgresContainerConfiguration.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Sql(scripts = "classpath:data/db/update-previous-table-state.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AuthenticationControllerImplIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @SpyBean
    RefreshTokenRepository refreshTokenRepository;
    @SpyBean
    UserCredentialsRepository userCredentialsRepository;

    @Test
    @DisplayName("Should authenticate user and provide with new tokens")
    void shouldAuthenticateUserAndProvideWithNewTokens() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("test1@example.com", "password1");

        mockMvc.perform(post("/api/v1/auth/signing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    @DisplayName("Should fail user authentication with non existing email")
    void shouldFailUserAuthenticationWithNonExistingEmail() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("NotRegisteredEmail@example.com", "anyPassword");
        String errorMessage = String.format("User with email: %s not found", request.getEmail());

        mockMvc.perform(post("/api/v1/auth/signing")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
               .andDo(print())
               .andExpect(status().isNotFound())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
               .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
               .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    @DisplayName("Should fail user authentication with wrong credentials")
    void shouldFailUserAuthenticationWithWrongCredentials() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("test1@example.com", "WRONGPASS");
        String errorMessage = "Bad credentials";

        mockMvc.perform(post("/api/v1/auth/signing")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
               .andDo(print())
               .andExpect(status().isUnauthorized())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
               .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()))
               .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    @DisplayName("Should refresh user tokens and provide them")
    void shouldRefreshUserTokensAndProvideThem() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("eyJhbGciOiJIUzI1NiJ9.eyJhdXRob3JpdGllcyI6WyJVU0VSIl0sInN1YiI6IjFhNzJhMDVmLTRiOGYtNDNjNS1hODg5LTFlYmM2ZDlkYzcyOSIsImlhdCI6MTcwNTk1MTY4NiwiZXhwIjoxNzM3NTA4NjEyfQ.A95EowuYZwBQxiK93ROA2vH-FLCd5B1aD-v9Rkowv08");

        mockMvc.perform(post("/api/v1/auth/token-refreshing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    @DisplayName("Should return refresh token is expired")
    void shouldReturnRefreshTokenInvalid() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("eyJhbGciOiJIUzI1NiJ9.eyJhdXRob3JpdGllcyI6WyJVU0VSIl0sInN1YiI6IjVhNzJhMDVmLTRiOGYtNDNjNS1hODg5LTFlYmM2ZDlkYzcyOSIsImlhdCI6MTcwNTk1MTA4MSwiZXhwIjoxNzA1OTUxMDg3fQ.yhbnBVDRq-3uJ6WSZ20L0iH_h-RWbNZZG90xvI9Umw0");

        mockMvc.perform(post("/api/v1/auth/token-refreshing")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
               .andDo(print())
               .andExpect(status().isInternalServerError())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
               .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
