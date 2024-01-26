package ru.clevertec.banking.customer.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.clevertec.banking.advice.exception.ResourceNotFoundException;
import ru.clevertec.banking.customer.configuration.PostgresContainerConfiguration;
import ru.clevertec.banking.customer.dto.message.CustomerMessagePayload;
import ru.clevertec.banking.customer.dto.request.CreateCustomerRequest;
import ru.clevertec.banking.customer.dto.request.GetCustomersPageableRequest;
import ru.clevertec.banking.customer.dto.response.CustomerResponse;
import ru.clevertec.banking.customer.entity.Customer;
import ru.clevertec.banking.customer.message.producer.CustomerProducer;
import ru.clevertec.banking.customer.repository.CustomerRepository;
import ru.clevertec.banking.customer.testutil.builders.CreateCustomerRequestTestBuilder;
import ru.clevertec.banking.customer.testutil.builders.CustomerMessagePayloadTestBuilder;
import ru.clevertec.banking.customer.testutil.builders.CustomerResponseTestBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PostgresContainerConfiguration.class})
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Sql(scripts = "classpath:data/db/insert-customer-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class CustomerServiceIntegrationTest {

    @Autowired
    CustomerService customerService;
    @SpyBean
    CustomerRepository customerRepository;
    @MockBean
    CustomerProducer customerProducer;

    @ParameterizedTest
    @MethodSource({"typeAndSizeSource"})
    @DisplayName("should return customers pageable by type or without")
    void shouldReturnCustomersPageable(List<String> expectedType, int expectedSize) {
        GetCustomersPageableRequest request = new GetCustomersPageableRequest();
        if (expectedType.size() == 1)
            request.setCustomerType(expectedType.get(0));


        Page<CustomerResponse> actualCustomers = customerService.getCustomersPageable(request);

        assertThat(actualCustomers)
                  .extracting(CustomerResponse::getCustomerType)
                  .containsAnyElementsOf(expectedType)
                  .hasSize(expectedSize);
    }

    static Stream<Arguments> typeAndSizeSource() {
        return Stream.of(Arguments.of(List.of("PHYSIC"), 2), Arguments.of(List.of("LEGAL"), 3),
                         Arguments.of(Arrays.asList("PHYSIC", "LEGAL"), 5));
    }

    @Test
    @DisplayName("should return customer by id")
    void shouldReturnCustomerById() {
        UUID id = UUID.fromString("1a72a05f-4b8f-43c5-a889-1ebc6d9dc729");
        String expectedCustomerName = "Test Customer 1";
        String expectedEmail = "test1@example.com";

        CustomerResponse actualCustomer = customerService.getCustomersById(id);

        assertThat(actualCustomer)
                  .extracting(CustomerResponse::getId,
                              CustomerResponse::getCustomerFullname,
                              CustomerResponse::getEmail)
                  .containsExactly(id, expectedCustomerName, expectedEmail);
    }

    @Test
    @DisplayName("should return customer by unp")
    void shouldReturnCustomerByUnp() {
        String unp = "1567318";
        String expectedCustomerName = "Test Customer 1";
        String expectedEmail = "test1@example.com";

        CustomerResponse actualCustomer = customerService.getCustomersByUnp(unp);

        assertThat(actualCustomer)
                  .extracting(CustomerResponse::getUnp,
                              CustomerResponse::getCustomerFullname,
                              CustomerResponse::getEmail)
                  .containsExactly(unp, expectedCustomerName, expectedEmail);
    }

    @Test
    @DisplayName("should throw NotFoundException by id")
    void shouldThrowExceptionWhenCustomerNotFound() {
        UUID id = UUID.randomUUID();
        Assertions.assertThatThrownBy(() -> customerService.getCustomersById(id))
                  .isInstanceOf(ResourceNotFoundException.class)
                  .hasMessageContaining(String.format("Customer with id %s not found", id));
    }

    @Test
    @DisplayName("should throw NotFoundException by unp")
    void shouldThrowExceptionWhenCustomerNotFoundByUnp() {
        String unp = "000000000";
        Assertions.assertThatThrownBy(() -> customerService.getCustomersByUnp(unp))
                  .isInstanceOf(ResourceNotFoundException.class)
                  .hasMessageContaining(String.format("Customer with unp %s not found", unp));
    }

    @Test
    @DisplayName("should save customer")
    void shouldSaveCustomer() {
        UUID uuid = UUID.randomUUID();
        CreateCustomerRequest request = new CreateCustomerRequestTestBuilder().build();
        request.setEmail("test22@example.com").setId(uuid).setUnp("2222222");
        CustomerResponse expectedSavedCustomer = new CustomerResponseTestBuilder().build();
        expectedSavedCustomer.setEmail("test22@example.com").setId(uuid).setUnp("2222222");

        long countBeforeSave = customerRepository.count();
        CustomerResponse savedCustomer = customerService.saveCustomer(request);
        long actualCount = customerRepository.count();

        assertThat(actualCount).isEqualTo(countBeforeSave + 1);
        assertThat(savedCustomer)
                .extracting(CustomerResponse::getId, CustomerResponse::getUnp, CustomerResponse::getEmail)
                .containsExactly(uuid, "2222222", "test22@example.com");
        verify(customerProducer, times(1)).prepareAndProduceForward(savedCustomer);
    }


    @DisplayName("should update customer with unp and without from message")
    @ParameterizedTest
    @MethodSource("provideUnpValues")
    @Sql(scripts = "classpath:data/db/update-previous-table-state.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldUpdateCustomerFromMessage(String unpValue) {
        CustomerMessagePayload payload = new CustomerMessagePayloadTestBuilder().build();
        payload.setEmail("newEmail@mail.ru").setUnp(unpValue);

        UUID customerIdToUpdate = payload.getId();

        Optional<Customer> byId = customerRepository.findById(customerIdToUpdate);
        assertThat(byId)
                  .isPresent();
        assertThat(byId.get()
                                  .getEmail())
                  .isEqualTo("test1@example.com");

        customerService.saveOrUpdateFromMessage(payload);

        Optional<Customer> byIdUpdated = customerRepository.findById(customerIdToUpdate);
        assertThat(byIdUpdated)
                  .map(Customer::getEmail)
                  .contains("newEmail@mail.ru");

        if (unpValue != null) {
            verify(customerRepository, times(1))
                    .findByIdOrEmailOrUnp(customerIdToUpdate, payload.getEmail(), unpValue);
        } else {
            verify(customerRepository, times(1))
                    .findByIdOrEmail(customerIdToUpdate, payload.getEmail());
        }
    }

    private static Stream<String> provideUnpValues() {
        return Stream.of("123456789", null);
    }

    @Test
    @DisplayName("should save customer from message")
    @Sql(scripts = "classpath:data/db/update-previous-table-state.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldSaveCustomerFromMessage() {
        UUID uuid = UUID.randomUUID();
        CustomerMessagePayload payload = new CustomerMessagePayloadTestBuilder().build();
        payload.setEmail("newEmail@mail.ru").setId(uuid).setUnp("3333333");

        long countBeforeSave = customerRepository.count();
        customerService.saveOrUpdateFromMessage(payload);
        long actualCount = customerRepository.count();

        assertThat(actualCount).isEqualTo(countBeforeSave + 1);
    }

    @Test
    @DisplayName("should softly delete customer")
    @Sql(scripts = "classpath:data/db/update-previous-table-state.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldSoftlyDeleteCustomer() {
        UUID customerIdToDelete = UUID.fromString("1a72a05f-4b8f-43c5-a889-1ebc6d9dc729");
        Assertions.assertThat(customerRepository.existsById(customerIdToDelete))
                .isTrue();

        customerService.deleteCustomer(customerIdToDelete);

        Assertions.assertThat(customerRepository.existsById(customerIdToDelete))
                  .isFalse();
    }
}
