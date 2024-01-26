package ru.clevertec.banking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import ru.clevertec.banking.configuration.PostgreSQLContainerConfig;
import ru.clevertec.banking.controller.security.WithMockUUIDJwtUser;
import ru.clevertec.banking.dto.CreditRequest;
import ru.clevertec.banking.dto.CreditRequestForUpdate;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.clevertec.banking.util.CreditFactory.getRequest;

@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {PostgreSQLContainerConfig.class})
@EnableAutoConfiguration(exclude = {RabbitAutoConfiguration.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@Tag("integration")
public class CreditControllerImplTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;


    @Test
    @DisplayName("test should return expected response, status ok")
    @WithMockUUIDJwtUser(roles = {"ADMIN"})
    void getAllTest() throws Exception {
        mockMvc.perform(get("/api/v1/credits"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    @DisplayName("test should return status forbidden, access denied")
    @WithMockUUIDJwtUser(roles = {"USER"})
    void getAllAccessDeniedTest() throws Exception {
        mockMvc.perform(get("/api/v1/credits"))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("test should return expected response, status created")
    @WithMockUUIDJwtUser(roles = {"ADMIN"})
    void createTest() throws Exception {
        CreditRequest request = getRequest();

        mockMvc.perform(post("/api/v1/credits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.iban").value(request.iban()))
                .andExpect(jsonPath("$.customer_id").value(request.customer_id()))
                .andExpect(jsonPath("$.contractNumber").value(request.contractNumber()));
    }

    @Test
    @DisplayName("test should return status bad request, invalid data")
    @WithMockUUIDJwtUser(roles = {"ADMIN"})
    void createInvalidDataTest() throws Exception {
        CreditRequest request = new CreditRequest("what is it",
                "11-0216512-2-0",
                LocalDate.of(2001, 3, 25),
                BigDecimal.valueOf(-100500.500),
                BigDecimal.valueOf(-888.88),
                "USD",
                LocalDate.of(2022, 11, 11),
                BigDecimal.valueOf(11.11),
                "DE98770400440532013000234500",
                true,
                false,
                "INVALID"
        );

        mockMvc.perform(post("/api/v1/credits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("test should return expected response, status ok")
    @WithMockUUIDJwtUser(value = "fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4", roles = {"USER"})
    void updateTest() throws Exception {
        CreditRequestForUpdate request = new CreditRequestForUpdate(
                "11-0216671-1-0",
                LocalDate.of(2030, 11, 11),
                BigDecimal.valueOf(22.11),
                false,
                false,
                "PHYSIC"
        );

        mockMvc.perform(patch("/api/v1/credits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.customer_type").value(request.customer_type()))
                .andExpect(jsonPath("$.isClosed").value(request.isClosed()))
                .andExpect(jsonPath("$.possibleRepayment").value(request.possibleRepayment()))
                .andExpect(jsonPath("$.rate").value(request.rate()));
    }

    @Test
    @DisplayName("test should return status not found, uncorrect card number")
    @WithMockUUIDJwtUser(roles = {"ADMIN"})
    void updateNotFoundTest() throws Exception {
        CreditRequestForUpdate request = new CreditRequestForUpdate(
                "WHAAAAAAAT",
                LocalDate.of(2030, 11, 11),
                BigDecimal.valueOf(22.11),
                false,
                false,
                "PHYSIC"
        );

        mockMvc.perform(patch("/api/v1/credits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @DisplayName("test should return status no content")
    @WithMockUUIDJwtUser(roles = {"SUPER_USER"})
    void deleteByContractNumberTest() throws Exception {
        String contractNumber = "11-0216444-2-0";

        mockMvc.perform(delete("/api/v1/credits/" + contractNumber))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("test should return status forbidden, access denied")
    @WithMockUUIDJwtUser(roles = {"ADMIN"})
    void deleteByContractNumberAccessDeniedTest() throws Exception {
        String contractNumber = "11-0216444-2-0";

        mockMvc.perform(delete("/api/v1/credits/" + contractNumber))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }


    @Test
    @DisplayName("test should return expected response, status ok")
    @WithMockUUIDJwtUser(value = "fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4", roles = {"USER"})
    void getByContractNumberTest() throws Exception {
        String contractNumber = "11-0216133-2-0";

        mockMvc.perform(get("/api/v1/credits/by-contract-number/" + contractNumber))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contractNumber").isNotEmpty())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    @DisplayName("test should return expected response, status forbidden")
    @WithMockUUIDJwtUser(value = "a3e8c29f-7c16-4a5d-b1c1-2d32f31a8d71", roles = {"USER"})
    void getByContractNumberAccessDenied() throws Exception {
        String contractNumber = "11-0216133-2-0";

        mockMvc.perform(get("/api/v1/credits/by-contract-number/" + contractNumber))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }


    @Test
    @DisplayName("test should return expected response, status ok")
    @WithMockUUIDJwtUser(value = "fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4", roles = {"USER"})
    void getByCustomerIdTest() throws Exception {
        String customer_id = "fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4";

        mockMvc.perform(get("/api/v1/credits/by-customer-id/" + customer_id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    @DisplayName("test should return status forbidden, access denied")
    @WithMockUUIDJwtUser(value = "fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4", roles = {"USER"})
    void getByCustomerIdAccessDenied() throws Exception {
        String customer_id = "8a7b3f5e-6d12-47f9-8c9a-1fcb4d3c928f";

        mockMvc.perform(get("/api/v1/credits/by-customer-id/" + customer_id))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

}
