package ru.clevertec.banking.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.clevertec.banking.customer.configuration.PostgresContainerConfiguration;
import ru.clevertec.banking.customer.configuration.RabbitMQContainerConfiguration;
import ru.clevertec.banking.customer.controller.security.WithMockUUIDJwtUser;
import ru.clevertec.banking.customer.dto.message.AuthMessage;
import ru.clevertec.banking.customer.dto.request.CreateCustomerRequest;
import ru.clevertec.banking.customer.repository.CustomerRepository;
import ru.clevertec.banking.customer.testutil.builders.CreateCustomerRequestTestBuilder;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.clevertec.banking.customer.testutil.FileReaderUtil.readFile;


@AutoConfigureMockMvc(addFilters = false)
@EnableAutoConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {PostgresContainerConfiguration.class, RabbitMQContainerConfiguration.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Sql(scripts = "classpath:data/db/update-previous-table-state.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CustomerControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    RabbitAdmin rabbitAdmin;
    @SpyBean
    CustomerRepository customerRepository;

    @Test
    @WithMockUUIDJwtUser(value = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc738")
    @DisplayName("should get customers pageable")
    void shouldGetCustomersPageable() throws Exception {
        int expectedSize = 5;

        mockMvc.perform(get("/api/v1/customers"))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
               .andExpect(jsonPath("$.content.size()").value(expectedSize))
               .andExpect(jsonPath("$.numberOfElements").value(expectedSize));
    }

    @Test
    @WithMockUUIDJwtUser(value = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc738")
    @DisplayName("should get customers pageable by register date")
    void shouldGetCustomersPageableByRegisterDate() throws Exception {
        int expectedSize = 3;

        mockMvc.perform(get("/api/v1/customers?registerDate=18.01.2024"))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
               .andExpect(jsonPath("$.content.size()").value(expectedSize))
               .andExpect(jsonPath("$.numberOfElements").value(expectedSize));
    }

    @ParameterizedTest
    @MethodSource("searchParams")
    @WithMockUUIDJwtUser(value = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc738")
    @DisplayName("should get customers pageable by customer type")
    void shouldGetCustomersPageableByCustomerType(String customerType, int expectedSize) throws Exception {
        mockMvc.perform(get("/api/v1/customers").param("customerType", customerType))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
               .andExpect(jsonPath("$.content.size()").value(expectedSize))
               .andExpect(jsonPath("$.numberOfElements").value(expectedSize));
    }

    static Stream<Arguments> searchParams() {
        return Stream.of(
                Arguments.of("LEGAL", 3),
                Arguments.of("PHYSIC", 2)
        );
    }

    @Test
    @WithMockUUIDJwtUser(value = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729")
    @DisplayName("should get customer by id")
    void shouldGetCustomerById() throws Exception {
        String expectedResponse = readFile("/customer/get-customer-by-id.json");

        mockMvc.perform(get("/api/v1/customers/1a72a05f-4b8f-43c5-a889-1ebc6d9dc729"))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
               .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("should return Unauthorized for get customer by id")
    void shouldReturnUnauthorizedForGetCustomerById() throws Exception {
        mockMvc.perform(get("/api/v1/customers/1a72a05f-4b8f-43c5-a889-1ebc6d9dc729"))
               .andDo(print())
               .andExpect(status().isUnauthorized())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
               .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    @WithMockUUIDJwtUser(value = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729")
    @DisplayName("should get customer by unp")
    void shouldGetCustomerByUnp() throws Exception {
        String expectedResponse = readFile("/customer/get-customer-by-id.json");

        mockMvc.perform(get("/api/v1/customers/by-unp/1567318"))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
               .andExpect(content().json(expectedResponse));
    }

    @Test
    @WithMockUUIDJwtUser(value = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729")
    @DisplayName("should return NotFound for get customer by unp")
    void shouldReturnNotFoundForGetCustomerByUnp() throws Exception {
        mockMvc.perform(get("/api/v1/customers/by-unp/blablabla"))
               .andDo(print())
               .andExpect(status().isNotFound())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
               .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()));
    }

    @Nested
    @WireMockTest(httpPort = 7777)
    class GetCustomerProducts {

        @Test
        @WithMockUUIDJwtUser(value = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729", roles = {"USER"})
        @DisplayName("should return Forbidden for user accessing different customer products")
        void shouldReturnForbiddenForUserAccessingDifferentCustomerProducts() throws Exception {
            mockMvc.perform(get("/api/v1/customers/9a72a05f-4b8f-43c5-a889-1ebc6d9dc738/banking-products"))
                   .andDo(print())
                   .andExpect(status().isForbidden())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                   .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
        }

        @Test
        @WithMockUUIDJwtUser(value = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729", roles = {"USER"})
        @DisplayName("should return authenticated customer products")
        void shouldReturnAuthenticatedCustomerProducts() throws Exception {
            String expectedResponse = readFile("/client/get-customer-products.json");

            WireMock.stubFor(WireMock.get("/api/v1/accounts/by-customer-id/1a72a05f-4b8f-43c5-a889-1ebc6d9dc729")
                            .willReturn(WireMock.okJson(
                                                    readFile("/client/get-accounts-with-linked-cards-by-customer-id.json"))
                                            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

            WireMock.stubFor(WireMock.get("/api/v1/credits/by-customer-id/1a72a05f-4b8f-43c5-a889-1ebc6d9dc729")
                            .willReturn(WireMock.okJson(readFile("/client/get-credits-by-customer-id.json"))
                                            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

            WireMock.stubFor(WireMock.get("/api/v1/deposits/customer/1a72a05f-4b8f-43c5-a889-1ebc6d9dc729")
                            .willReturn(WireMock.okJson(readFile("/client/get-deposits-by-customer-id.json"))
                                            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));


            mockMvc.perform(get("/api/v1/customers/1a72a05f-4b8f-43c5-a889-1ebc6d9dc729/banking-products"))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                   .andExpect(content().json(expectedResponse));
        }
    }

    @Nested
    class SaveCustomerAndProduceMessage {

        private final String AUTH_QUEUE_NAME = "auth";

        @BeforeEach
        void setUp() {
            rabbitAdmin.purgeQueue(AUTH_QUEUE_NAME, true);
        }

        @AfterEach
        void tearDown() {
            rabbitAdmin.purgeQueue(AUTH_QUEUE_NAME, true);
        }

        @Test
        @WithMockUUIDJwtUser(value = "9a72a05f-4b8f-43c5-a889-1ebc6d9dc729", roles = {"ADMIN"})
        @DisplayName("should save customer and produce message forward")
        void shouldSaveCustomerAndProduceMessage() throws Exception {
            CreateCustomerRequest createCustomerRequest = new CreateCustomerRequestTestBuilder().build();
            UUID id = UUID.randomUUID();
            createCustomerRequest.setId(id).setEmail("newEmail@mail.ru").setCustomerType("PHYSIC");

            mockMvc.perform(post("/api/v1/customers")
                                    .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(createCustomerRequest)))
                   .andDo(print())
                   .andExpect(status().isCreated())
                   .andExpect(jsonPath("$.customer_id").isNotEmpty())
                   .andExpect(jsonPath("$.email").value(createCustomerRequest.getEmail()));

            await().atMost(30, TimeUnit.SECONDS)
                   .until(isMessageInQueue(id, createCustomerRequest.getEmail()), equalTo(true));
        }

        @Test
        @WithMockUUIDJwtUser(value = "9a72a05f-4b8f-43c5-a889-1ebc6d9dc729", roles = {"USER"})
        @DisplayName("should return Forbidden for user saving customer only Admin allowed")
        void shouldReturnForbiddenForUserSavingCustomer() throws Exception {
            CreateCustomerRequest createCustomerRequest = new CreateCustomerRequestTestBuilder().build();
            UUID id = UUID.randomUUID();
            createCustomerRequest.setId(id).setEmail("dontSaveMe@mail.ru").setCustomerType("PHYSIC");


            mockMvc.perform(post("/api/v1/customers")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(createCustomerRequest)))
                   .andDo(print())
                   .andExpect(status().isForbidden())
                   .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
        }

        @Test
        @WithMockUUIDJwtUser(value = "9a72a05f-4b8f-43c5-a889-1ebc6d9dc729", roles = {"ADMIN"})
        @DisplayName("should return BadRequest for saving customer with invalid type")
        void shouldReturnInvalidCustomerTypeForSavingCustomer() throws Exception {
            CreateCustomerRequest createCustomerRequest = new CreateCustomerRequestTestBuilder().build();
            UUID id = UUID.randomUUID();
            createCustomerRequest.setId(id).setEmail("dontSaveMe2@mail.ru").setCustomerType("BlaBla");


            mockMvc.perform(post("/api/v1/customers")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(createCustomerRequest)))
                   .andDo(print())
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                   .andExpect(jsonPath("$.message").value("Invalid customer type: BLABLA"));
        }

        private Callable<Boolean> isMessageInQueue(UUID id, String email) {
            return () -> {
                int queueMessageCount = Objects.requireNonNull(rabbitTemplate.execute(
                                                       session -> session.queueDeclare(
                                                               AUTH_QUEUE_NAME,
                                                               true,
                                                               false,
                                                               false,
                                                               null
                                                       )
                                               ))
                                               .getMessageCount();

                Message receivedMessage = rabbitTemplate.receive(AUTH_QUEUE_NAME);
                AuthMessage authMessage = objectMapper.readValue(receivedMessage.getBody(), AuthMessage.class);

                if (authMessage != null) {
                    return queueMessageCount == 1 &&
                           authMessage.getPayload().getEmail().equals(email) &&
                           authMessage.getPayload().getId().equals(id);
                }
                return false;
            };
        }
    }

    @Test
    @WithMockUUIDJwtUser(value = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729", roles = {"SUPER_USER"})
    @DisplayName("should softly delete customer by id")
    void shouldSoftlyDeleteAccountById() throws Exception {
        long beforeDeletionCount = customerRepository.count();
        mockMvc.perform(delete("/api/v1/customers/1a72a05f-4b8f-43c5-a889-1ebc6d9dc729"))
               .andDo(print())
               .andExpect(status().isNoContent());

        long afterDeletionCount = customerRepository.count();
        Assertions.assertEquals(afterDeletionCount, beforeDeletionCount - 1);
    }

    @Test
    @WithMockUUIDJwtUser(value = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729", roles = {"ADMIN"})
    @DisplayName("should return Forbidden for customer deleting Only SUPER_USER allowed")
    void shouldReturnForbiddenForCustomerDeleting() throws Exception {
        mockMvc.perform(delete("/api/v1/customers/1a72a05f-4b8f-43c5-a889-1ebc6d9dc729"))
               .andDo(print())
               .andExpect(status().isForbidden())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
               .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

}