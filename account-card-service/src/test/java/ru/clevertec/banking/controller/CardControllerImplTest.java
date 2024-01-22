package ru.clevertec.banking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import ru.clevertec.banking.configuration.PostgreSQLContainerConfig;
import ru.clevertec.banking.controller.security.WithMockUUIDJwtUser;
import ru.clevertec.banking.dto.card.CardRequest;
import ru.clevertec.banking.dto.card.CardRequestForUpdate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.clevertec.banking.util.FileReaderUtils.*;

@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {PostgreSQLContainerConfig.class})
@EnableAutoConfiguration(exclude = {RabbitAutoConfiguration.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@Tag("integration")
public class CardControllerImplTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Test
    @DisplayName("test should return expected response, status created")
    @WithMockUUIDJwtUser(roles = {"ADMIN"})
    void createTest() throws Exception {
        CardRequest request = new CardRequest(
                "6666666666666666",
                "6666 6666 6666 6666",
                "US58469159383322778899012345",
                "8a7b3f5e-6d12-47f9-8c9a-1fcb4d3c928f",
                "PHYSIC",
                "NEW CARDHOLDER",
                "ACTIVE");

        mockMvc.perform(post("/api/v1/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.iban").value(request.iban()))
                .andExpect(jsonPath("$.customer_id").value(request.customer_id()))
                .andExpect(jsonPath("$.card_number").value(request.card_number()));
    }

    @Test
    @DisplayName("test should return status bad request, invalid data")
    @WithMockUUIDJwtUser(roles = {"ADMIN"})
    void createInvalidData() throws Exception {
        CardRequest request = new CardRequest(
                "6666666666666666",
                "6666 6666 6666 6666",
                "US58469159383322778899012345",
                "UNCORRECT UUID",
                "INVALID DATA",
                "NEW CARDHOLDER",
                "WHAT IS");

        mockMvc.perform(post("/api/v1/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("test should return expected response, status ok")
    @WithMockUUIDJwtUser(roles = {"ADMIN"})
    void getAllTest() throws Exception {
        mockMvc.perform(get("/api/v1/cards"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    @DisplayName("test should return status forbidden, access denied")
    @WithMockUUIDJwtUser(roles = {"USER"})
    void getAllAccessDeniedTest() throws Exception {
        mockMvc.perform(get("/api/v1/cards"))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("test should return expected response, status ok")
    @WithMockUUIDJwtUser(value = "8a7b3f5e-6d12-47f9-8c9a-1fcb4d3c928f", roles = {"USER"})
    void findByCustomerTest() throws Exception {
        String customer_id = "8a7b3f5e-6d12-47f9-8c9a-1fcb4d3c928f";

        mockMvc.perform(get("/api/v1/cards/by-customer-id/" + customer_id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    @DisplayName("test should return status forbidden, access denied")
    @WithMockUUIDJwtUser(value = "fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4", roles = {"USER"})
    void findByCustomerAccessDeniedTest() throws Exception {
        String customer_id = "8a7b3f5e-6d12-47f9-8c9a-1fcb4d3c928f";

        mockMvc.perform(get("/api/v1/cards/by-customer-id/" + customer_id))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("test should return status no content")
    @WithMockUUIDJwtUser(roles = {"SUPER_USER"})
    void deleteTest() throws Exception {
        String cardNumber = "5200000000001096";

        mockMvc.perform(delete("/api/v1/accounts/" + cardNumber))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("test should return status forbidden, access denied")
    @WithMockUUIDJwtUser(roles = {"ADMIN"})
    void deleteAccessDeniedTest() throws Exception {
        String cardNumber = "5200000000001096";

        mockMvc.perform(delete("/api/v1/accounts/" + cardNumber))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Nested
    @WireMockTest(httpPort = 6666)
    class WithCurrencyClient {

        @Test
        @DisplayName("test should return expected response, status ok")
        @WithMockUUIDJwtUser(value = "fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4", roles = {"USER"})
        void updateTest() throws Exception {
            CardRequestForUpdate request = new CardRequestForUpdate(
                    "5218347602398745",
                    null,
                    "PHYSIC",
                    "INACTIVE");

            WireMock.stubFor(WireMock.get("/api/v1/currencies")
                    .willReturn(WireMock.okJson(readFile("/get-actual-currency.json"))
                            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

            mockMvc.perform(patch("/api/v1/cards")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.customer_type").value(request.customer_type()))
                    .andExpect(jsonPath("$.card_status").value(request.card_status()))
                    .andExpect(jsonPath("$.iban").isNotEmpty());
        }

        @Test
        @DisplayName("test should return status forbidden, access denied")
        @WithMockUUIDJwtUser(value = "8a7b3f5e-6d12-47f9-8c9a-1fcb4d3c928f", roles = {"USER"})
        void updateAccessDeniedTest() throws Exception {
            CardRequestForUpdate request = new CardRequestForUpdate(
                    "5218347602398745",
                    null,
                    "PHYSIC",
                    "INACTIVE");

            WireMock.stubFor(WireMock.get("/api/v1/currencies")
                    .willReturn(WireMock.okJson(readFile("/get-actual-currency.json"))
                            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

            mockMvc.perform(patch("/api/v1/cards")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
        }

        @Test
        @DisplayName("test should return status not found, uncorrect card number")
        @WithMockUUIDJwtUser(roles = {"ADMIN"})
        void updateNotFoundTest() throws Exception {
            CardRequestForUpdate request = new CardRequestForUpdate(
                    "NOT FOUND NUMBER CARD",
                    null,
                    "PHYSIC",
                    "INACTIVE");

            WireMock.stubFor(WireMock.get("/api/v1/currencies")
                    .willReturn(WireMock.okJson(readFile("/get-actual-currency.json"))
                            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

            mockMvc.perform(patch("/api/v1/cards")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()));
        }

        @Test
        @DisplayName("test should return expected response with balance, status ok")
        @WithMockUUIDJwtUser(value = "a3e8c29f-7c16-4a5d-b1c1-2d32f31a8d71", roles = {"USER"})
        void findByCardNumberTest() throws Exception {
            String cardNumber = "4532015112890366";

            WireMock.stubFor(WireMock.get("/api/v1/currencies")
                    .willReturn(WireMock.okJson(readFile("/get-actual-currency.json"))
                            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

            mockMvc.perform(get("/api/v1/cards/by-card-number/" + cardNumber))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.card_balance").isNotEmpty())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        }

        @Test
        @DisplayName("test should return status forbidden, access denied")
        @WithMockUUIDJwtUser(value = "8a7b3f5e-6d12-47f9-8c9a-1fcb4d3c928f", roles = {"USER"})
        void findByCardNumberAccessDeniedTest() throws Exception {
            String cardNumber = "4532015112890366";

            WireMock.stubFor(WireMock.get("/api/v1/currencies")
                    .willReturn(WireMock.okJson(readFile("/get-actual-currency.json"))
                            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

            mockMvc.perform(get("/api/v1/cards/by-card-number/" + cardNumber))
                    .andDo(print())
                    .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
        }
    }
}
