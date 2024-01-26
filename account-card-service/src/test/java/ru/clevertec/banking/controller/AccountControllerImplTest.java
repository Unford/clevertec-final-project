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
import ru.clevertec.banking.dto.account.AccountRequest;
import ru.clevertec.banking.dto.account.AccountRequestForUpdate;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {PostgreSQLContainerConfig.class})
@EnableAutoConfiguration(exclude = {RabbitAutoConfiguration.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@Tag("integration")
public class AccountControllerImplTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Test
    @DisplayName("test should return expected response and status ok")
    @WithMockUUIDJwtUser(roles = {"ADMIN"})
    void getAllTest() throws Exception {

        mockMvc.perform(get("/api/v1/accounts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.numberOfElements").value(5));
    }

    @Test
    @DisplayName("test should return expected response and status ok with USER role")
    @WithMockUUIDJwtUser(value = "8a7b3f5e-6d12-47f9-8c9a-1fcb4d3c928f", roles = {"USER"})
    void findByCustomerTest() throws Exception {
        String customer_id = "8a7b3f5e-6d12-47f9-8c9a-1fcb4d3c928f";

        mockMvc.perform(get("/api/v1/accounts/by-customer-id/" + customer_id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    @DisplayName("test should return expected response and status ok with USER role")
    @WithMockUUIDJwtUser(value = "8a7b3f5e-6d12-47f9-8c9a-1fcb4d3c928f", roles = {"USER"})
    void findByIbanTest() throws Exception {
        String customer_id = "8a7b3f5e-6d12-47f9-8c9a-1fcb4d3c928f";
        String iban = "FR1420041010050500013M026060";

        mockMvc.perform(get("/api/v1/accounts/by-iban/" + iban))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.iban").value(iban))
                .andExpect(jsonPath("$.customer_id").value(customer_id));
    }

    @Test
    @DisplayName("test should return status FORBIDDEN, user has not access")
    @WithMockUUIDJwtUser(roles = {"USER"})
    void findByIbanAccessDeniedTest() throws Exception {
        String iban = "FR1420041010050500013M026060";

        mockMvc.perform(get("/api/v1/accounts/by-iban/" + iban))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("test should return expected response after update, status ok")
    @WithMockUUIDJwtUser(roles = {"ADMIN"})
    void updateTest() throws Exception {
        AccountRequestForUpdate requestForUpdate = new AccountRequestForUpdate(
                "FR1420041010050500013M026060",
                "New Better Name", null, null);

        mockMvc.perform(patch("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestForUpdate)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value(requestForUpdate.name()));
    }

    @Test
    @DisplayName("test should return status No content")
    @WithMockUUIDJwtUser(roles = {"SUPER_USER"})
    void deleteTest() throws Exception {
        String iban = "AABBCCCDDDDEEEEEEEEEEEEEEEEE";

        mockMvc.perform(delete("/api/v1/accounts/" + iban))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("test should return status FORBIDDEN")
    @WithMockUUIDJwtUser(roles = {"ADMIN,USER"})
    void deleteAccessDeniedTest() throws Exception {
        String iban = "US58469159383322778899012345";

        mockMvc.perform(delete("/api/v1/accounts/" + iban))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("test should return expected response, status created")
    @WithMockUUIDJwtUser(roles = {"ADMIN"})
    void createTest() throws Exception {
        AccountRequest request = new AccountRequest("Название счёта №7",
                "FR1420041010050500013M026066",
                BigDecimal.valueOf(2800.31),
                "933",
                LocalDate.of(1000, 10, 10),
                true,
                "8a7b3f5e-6d12-47f9-8c9a-1fcb4d3c928f",
                "PHYSIC",
                BigDecimal.valueOf(0.01));

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value(request.name()))
                .andExpect(jsonPath("$.customer_id").value(request.customer_id()))
                .andExpect(jsonPath("$.iban").value(request.iban()));
    }

    @Test
    @DisplayName("test should return status bad request, invalid data")
    @WithMockUUIDJwtUser(roles = {"ADMIN"})
    void createInvalidDataTest() throws Exception {
        AccountRequest request = new AccountRequest("Название счёта №7",
                "FR1420041010050500013M026066",
                BigDecimal.valueOf(-2800.31),
                null,
                LocalDate.of(1000, 10, 10),
                true,
                "8a7b3f5e-6d12-47f9-8c9a",
                "JESUS",
                BigDecimal.valueOf(0.01));

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

}
