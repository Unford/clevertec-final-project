package ru.clevertec.banking.deposit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import net.datafaker.Faker;
import org.hamcrest.core.AllOf;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.clevertec.banking.deposit.configuration.DataFakerConfiguration;
import ru.clevertec.banking.deposit.configuration.PostgresContainerConfiguration;
import ru.clevertec.banking.deposit.controller.security.WithMockUUIDJwtUser;
import ru.clevertec.banking.deposit.message.consumer.DepositConsumer;
import ru.clevertec.banking.deposit.model.dto.request.CreateDepositRequest;
import ru.clevertec.banking.deposit.model.dto.request.UpdateDepositRequest;
import ru.clevertec.banking.deposit.model.dto.response.DepositResponse;
import ru.clevertec.banking.deposit.repository.DepositRepository;
import ru.clevertec.banking.deposit.util.RandomDepositFactory;
import ru.clevertec.banking.deposit.util.SpringUnitCompositeTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.clevertec.banking.deposit.util.FileReaderUtil.readFile;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {PostgresContainerConfiguration.class, DataFakerConfiguration.class})
@EnableAutoConfiguration(exclude = {RabbitAutoConfiguration.class})
@SpringUnitCompositeTest
@Tag("integration")
class DepositControllerImplIntegrationTest {
    @MockBean
    DepositConsumer consumer;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    Faker faker;
    @Autowired
    RandomDepositFactory depositFactory;
    @Autowired
    ObjectMapper objectMapper;
    @SpyBean
    DepositRepository depositRepository;

    @Test
    @WithMockUUIDJwtUser(roles = {"ADMIN"})
    void shouldReturnPageOfAllDeposits() throws Exception {
        int expectedSize = 14;
        mockMvc.perform(get("/api/v1/deposits"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.content.size()").value(expectedSize))
                .andExpect(jsonPath("$.numberOfElements").value(expectedSize));
    }

    @Test
    @WithMockUUIDJwtUser(value = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc738", roles = {"USER"})
    void shouldReturnPageOfUserDeposits() throws Exception {
        int expectedSize = 4;
        String expectedUUID = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc738";
        mockMvc.perform(get("/api/v1/deposits"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.content.size()").value(expectedSize))
                .andExpect(jsonPath("$.numberOfElements").value(expectedSize))
                .andExpect(jsonPath("$.content..customer_id", everyItem(equalTo(expectedUUID))));
    }

    @Test
    @WithMockUUIDJwtUser(roles = {"ADMIN"})
    void shouldReturnDepositWithAdmin() throws Exception {
        String iban = "183456789";
        mockMvc.perform(get("/api/v1/deposits/" + iban))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.acc_info.acc_iban").value(iban))
        ;
    }

    @Test
    @WithMockUUIDJwtUser(roles = {"USER"})
    void shouldReturnForbiddenForNotUserDeposit() throws Exception {
        String iban = "183456789";
        mockMvc.perform(get("/api/v1/deposits/" + iban))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
        ;
    }

    @Test
    @WithMockUUIDJwtUser(roles = {"USER"})
    void shouldReturnNotFoundToUserWhenFindByIban() throws Exception {
        String iban = "IAMNOTEXIST";
        mockMvc.perform(get("/api/v1/deposits/" + iban))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
        ;
    }

    @Test
    @WithMockUUIDJwtUser(roles = {"ADMIN"})
    void shouldReturnListOfByCustomerId() throws Exception {
        String customerId = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc738";
        int expectedSize = 4;
        mockMvc.perform(get("/api/v1/deposits/customer/" + customerId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$..customer_id", everyItem(equalTo(customerId))))
                .andExpect(jsonPath("$.size()").value(expectedSize))
        ;
    }

    @Test
    @WithMockUUIDJwtUser(value = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc738", roles = {"USER"})
    void shouldReturnListOfByToUserCustomerId() throws Exception {
        String customerId = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc738";
        int expectedSize = 4;
        mockMvc.perform(get("/api/v1/deposits/customer/" + customerId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$..customer_id", everyItem(equalTo(customerId))))
                .andExpect(jsonPath("$.size()").value(expectedSize))
        ;
    }

    @Test
    @WithMockUUIDJwtUser(roles = {"USER"})
    void shouldReturnForbiddenToUserWithDifferentId() throws Exception {
        String customerId = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc738";
        mockMvc.perform(get("/api/v1/deposits/customer/" + customerId))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))

        ;
    }

    @Test
    @WithMockUUIDJwtUser(roles = {"USER", "ADMIN"})
    void shouldReturnForbiddenToNotSuperUserWhenDelete() throws Exception {
        String iban = faker.finance().iban("BY");
        mockMvc.perform(delete("/api/v1/deposits/" + iban))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))

        ;
    }

    @Test
    @WithMockUUIDJwtUser(roles = {"SUPER_USER"})
    void shouldReturnNoContentToSuperUserWhenDelete() throws Exception {
        String iban = faker.finance().iban("BY");
        mockMvc.perform(delete("/api/v1/deposits/" + iban))
                .andDo(print())
                .andExpect(status().isNoContent())
        ;
    }

    @Test
    @WithMockUUIDJwtUser(value = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc738", roles = {"USER"})
    void shouldReturnForbiddenWhenUserCreateDepositWithDiffId() throws Exception {
        CreateDepositRequest depositRequest = depositFactory.createDepositRequest();
        mockMvc.perform(post("/api/v1/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
        ;
    }

    @Test
    @WithMockUUIDJwtUser(value = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc738", roles = {"USER"})
    void shouldReturnForbiddenWhenUpdateUserWithDiffId() throws Exception {
        CreateDepositRequest depositRequest = depositFactory.createDepositRequest();
        mockMvc.perform(post("/api/v1/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
        ;
    }

    @Test
    @WithMockUUIDJwtUser(roles = {"ADMIN"})
    void shouldReturnBadRequestWhenCreateDepositWithNotUniqueIban() throws Exception {
        CreateDepositRequest depositRequest = depositFactory.createDepositRequest();
        depositRequest.getAccInfo().setAccIban("42333789");
        mockMvc.perform(post("/api/v1/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
        ;

    }

    @Test
    @WithMockUUIDJwtUser(roles = {"ADMIN"})
    void shouldReturnBadRequestWhenCreateDepositWithInvalidData() throws Exception {
        CreateDepositRequest depositRequest = depositFactory.createDepositRequest()
                .setCustomerId(null)
                .setCustomerType(null);
        depositRequest.getAccInfo().setAccIban("123")
                .setAccOpenDate(LocalDate.now().plusDays(10))
                .setCurrAmount(BigDecimal.valueOf(-1))
                .setCurrAmountCurrency("INVALID");
        depositRequest.getDepInfo()
                .setRate(null)
                .setTermVal(-100)
                .setTermScale(null)
                .setDepType(null)
                .setAutoRenew(null);


        mockMvc.perform(post("/api/v1/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", AllOf.allOf(containsString("customerId"),
                        containsString("depInfo"),
                        containsString("accInfo"),
                        containsString("customerType"))))
        ;

    }

    @Test
    @WithMockUUIDJwtUser(roles = {"ADMIN"})
    void shouldReturnUpdatedDeposit() throws Exception {
        UpdateDepositRequest updateDepositRequest = depositFactory.createUpdateDepositRequest();
        String iban = "944454324";
        mockMvc.perform(patch("/api/v1/deposits/" + iban)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDepositRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.acc_info.acc_iban").value(iban))
                .andExpect(jsonPath("$.dep_info.dep_type").value(updateDepositRequest.getDepInfo().getDepType().toString()))
                .andExpect(jsonPath("$.dep_info.auto_renew").value(updateDepositRequest.getDepInfo().getAutoRenew()))
        ;

    }

    @Test
    @WithMockUUIDJwtUser(value = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc738", roles = {"USER"})
    void shouldReturnUpdatedCustomerDeposit() throws Exception {
        UpdateDepositRequest updateDepositRequest = depositFactory.createUpdateDepositRequest();
        String iban = "942414214";
        mockMvc.perform(patch("/api/v1/deposits/" + iban)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDepositRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.acc_info.acc_iban").value(iban))
                .andExpect(jsonPath("$.dep_info.dep_type").value(updateDepositRequest.getDepInfo().getDepType().toString()))
                .andExpect(jsonPath("$.dep_info.auto_renew").value(updateDepositRequest.getDepInfo().getAutoRenew()))
        ;

    }


    @Test
    @WithMockUUIDJwtUser(roles = {"USER"})
    void shouldReturnForbiddenToUpdateForUser() throws Exception {
        UpdateDepositRequest updateDepositRequest = depositFactory.createUpdateDepositRequest();
        String iban = "944454324";
        mockMvc.perform(patch("/api/v1/deposits/" + iban)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDepositRequest)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
        ;

    }


    @Nested
    @WireMockTest(httpPort = 7777)
    class DepositSaveTest {

        @Test
        @WithMockUUIDJwtUser(roles = {"ADMIN"})
        void shouldReturnCreatedByAdminDeposit() throws Exception {
            CreateDepositRequest depositRequest = depositFactory.createDepositRequest();

            WireMock.stubFor(WireMock.get("/api/v1/customers/" + depositRequest.getCustomerId().toString())
                    .willReturn(WireMock.okJson(readFile("/customer/get-customer-by-id-response.json"))
                            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

            DepositResponse depositResponse = objectMapper.readValue(
                    mockMvc.perform(post("/api/v1/deposits")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(depositRequest)))
                            .andDo(print())
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$.id").isNotEmpty())
                            .andExpect(jsonPath("$.customer_id").value(depositRequest.getCustomerId().toString()))
                            .andExpect(jsonPath("$.acc_info.acc_iban").value(depositRequest.getAccInfo().getAccIban()))
                            .andReturn().getResponse().getContentAsString(), DepositResponse.class);

            depositRepository.deleteById(depositResponse.getId());
        }

        @Test
        @WithMockUUIDJwtUser(roles = {"ADMIN"})
        void shouldReturnNotFoundCustomerWhenCreateDepositByAdmin() throws Exception {
            CreateDepositRequest depositRequest = depositFactory.createDepositRequest();

            WireMock.stubFor(WireMock.get("/api/v1/customers/" + depositRequest.getCustomerId().toString())
                    .willReturn(WireMock.jsonResponse(readFile("/customer/get-customer-by-id-not-found-response.json"),
                                    HttpStatus.NOT_FOUND.value())
                            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

            mockMvc.perform(post("/api/v1/deposits")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(depositRequest)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
            ;
        }


        @Test
        @WithMockUUIDJwtUser(value = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc738", roles = {"USER"})
        void shouldReturnCreatedByUserDeposit() throws Exception {
            CreateDepositRequest depositRequest = depositFactory.createDepositRequest();
            depositRequest.setCustomerId(UUID.fromString("1a72a05f-4b8f-43c5-a889-1ebc6d9dc738"));

            WireMock.stubFor(WireMock.get("/api/v1/customers/" + depositRequest.getCustomerId().toString())
                    .willReturn(WireMock.okJson(readFile("/customer/get-customer-by-id-response.json"))
                            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

            DepositResponse depositResponse = objectMapper.readValue(
                    mockMvc.perform(post("/api/v1/deposits")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(depositRequest)))
                            .andDo(print())
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$.id").isNotEmpty())
                            .andExpect(jsonPath("$.customer_id").value(depositRequest.getCustomerId().toString()))
                            .andExpect(jsonPath("$.acc_info.acc_iban").value(depositRequest.getAccInfo().getAccIban()))
                            .andReturn().getResponse().getContentAsString(), DepositResponse.class);

            depositRepository.deleteById(depositResponse.getId());
        }


    }


}
