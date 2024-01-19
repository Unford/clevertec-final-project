package ru.clevertec.banking.customer.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.clevertec.banking.advice.exception.ResourceNotFoundException;
import ru.clevertec.banking.customer.dto.message.CustomerMessagePayload;
import ru.clevertec.banking.customer.dto.request.CreateCustomerRequest;
import ru.clevertec.banking.customer.dto.request.GetCustomersPageableRequest;
import ru.clevertec.banking.customer.dto.response.CustomerResponse;
import ru.clevertec.banking.customer.entity.Customer;
import ru.clevertec.banking.customer.exception.InternalCustomerServiceException;
import ru.clevertec.banking.customer.mapper.CustomerMapper;
import ru.clevertec.banking.customer.message.producer.CustomerProducer;
import ru.clevertec.banking.customer.repository.CustomerRepository;
import ru.clevertec.banking.customer.testutil.builders.CreateCustomerRequestTestBuilder;
import ru.clevertec.banking.customer.testutil.builders.CustomerMessagePayloadTestBuilder;
import ru.clevertec.banking.customer.testutil.builders.CustomerResponseTestBuilder;
import ru.clevertec.banking.customer.testutil.builders.CustomerTestBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @InjectMocks
    private CustomerService customerService;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerMapper customerMapper;
    @Mock
    private CustomerProducer customerProducer;
    @Captor
    private ArgumentCaptor<Specification<Customer>> specificationCaptor;

    @Test
    @DisplayName("should return pageable list of one customer")
    void getCustomersPageable() {
        //given
        GetCustomersPageableRequest request = new GetCustomersPageableRequest();
        CustomerResponse response = new CustomerResponseTestBuilder().build();

        Customer customer = new CustomerTestBuilder().build();
        List<Customer> customerList = Collections.singletonList(customer);
        Page<Customer> expectedCustomerPage =
                new PageImpl<>(customerList, Pageable.ofSize(request.getSize()), customerList.size());

        //when
        doReturn(expectedCustomerPage)
                .when(customerRepository)
                .findAll(specificationCaptor.capture(), eq(request.toPageable()));

        doReturn(response)
                .when(customerMapper)
                .toCustomerResponse(customer);

        //then
        Page<CustomerResponse> resultPage = customerService.getCustomersPageable(request);

        assertNotNull(resultPage);
        assertEquals(expectedCustomerPage.getTotalElements(), resultPage.getTotalElements());
    }

    @Test
    @DisplayName("should return empty list customers")
    void getZeroCustomersPageable() {
        //given
        GetCustomersPageableRequest request = new GetCustomersPageableRequest();
        request.setPage(4);

        Page<Customer> expectedCustomerPage =
                new PageImpl<>(Collections.emptyList(), Pageable.ofSize(request.getSize()), 0);

        //when
        doReturn(expectedCustomerPage)
                .when(customerRepository)
                .findAll(specificationCaptor.capture(), eq(request.toPageable()));

        //then
        Page<CustomerResponse> resultPage = customerService.getCustomersPageable(request);

        assertEquals(expectedCustomerPage.getSize(), resultPage.getSize());
    }

    @Test
    @DisplayName("should return customer by id")
    void getCustomersById() {
        //given
        CustomerResponse expectedCustomer = new CustomerResponseTestBuilder().build();
        Customer customer = new CustomerTestBuilder().build();

        //when
        doReturn(expectedCustomer)
                .when(customerMapper)
                .toCustomerResponse(customer);

        doReturn(Optional.of(customer))
                .when(customerRepository)
                .findById(expectedCustomer.getId());

        //then
        CustomerResponse result = customerService.getCustomersById(expectedCustomer.getId());

        assertNotNull(result);
        assertEquals(expectedCustomer, result);
    }

    @Test
    @DisplayName("get by id should throw ResourceNotFoundException")
    void getCustomerByIdThrowsResourceNotFoundException() {
        //given
        UUID id = UUID.fromString("2a91a04f-4b8f-67c5-a889-1ebc6d9dc729");

        //when
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomersById(id));

        //then
        String expectedMessage = "Customer with id %s not found".formatted(id);
        String actualMessage = exception.getMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("should return customer by unp")
    void getCustomersByUnp() {
        //given
        CustomerResponse expectedCustomer = new CustomerResponseTestBuilder().build();
        Customer customer = new CustomerTestBuilder().build();

        //when
        doReturn(expectedCustomer)
                .when(customerMapper)
                .toCustomerResponse(customer);

        doReturn(Optional.of(customer))
                .when(customerRepository)
                .findByUnpAndUnpNotNull(expectedCustomer.getUnp());

        //then
        CustomerResponse result = customerService.getCustomersByUnp(expectedCustomer.getUnp());

        assertNotNull(result);
        assertEquals(expectedCustomer, result);
    }

    @Test
    @DisplayName("get by unp should throw ResourceNotFoundException")
    void getCustomerByUnpThrowsResourceNotFoundException() {
        //given
        String unp = "123456789";
        //when
        Exception exception =
                assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomersByUnp(unp));

        //then
        String expectedMessage = "Customer with unp %s not found".formatted(unp);
        String actualMessage = exception.getMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("should save customer by given request")
    void saveCustomer() {
        //given
        CreateCustomerRequest request = new CreateCustomerRequestTestBuilder().build();
        Customer customer = new CustomerTestBuilder().build();
        CustomerResponse expectedCustomer = new CustomerResponseTestBuilder().build();

        //when
        doReturn(customer)
                .when(customerMapper)
                .toEntity(request);

        doReturn(customer)
                .when(customerRepository)
                .save(customer);

        doReturn(expectedCustomer)
                .when(customerMapper)
                .toCustomerResponse(customer);

        doNothing()
                .when(customerProducer)
                .prepareAndProduceForward(expectedCustomer);

        //then
        CustomerResponse result = customerService.saveCustomer(request);
        assertNotNull(result);
        assertEquals(expectedCustomer, result);
    }

    @Test
    @DisplayName("save should throw InternalCustomerServiceException")
    void saveCustomerThrowsInternalCustomerServiceException() {
        //given
        CreateCustomerRequest request = new CreateCustomerRequestTestBuilder().build();

        //when
        Exception exception =
                assertThrows(InternalCustomerServiceException.class, () -> customerService.saveCustomer(request));

        //then
        String expectedMessage = "Failed to create customer";
        String actualMessage = exception.getMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("should save customer by given RabbitMQ message")
    void saveCustomerFromMessage() {
        //given
        CustomerMessagePayload payload = new CustomerMessagePayloadTestBuilder().build();
        Customer customer = new CustomerTestBuilder().build();

        //when
        doReturn(customer)
                .when(customerMapper)
                .toEntityFromCustomerPayload(payload);

        doReturn(customer)
                .when(customerRepository)
                .save(customer);

        //then
        customerService.saveOrUpdateFromMessage(payload);
        verify(customerRepository).save(customer);
    }

    @Test
    @DisplayName("should update customer with empty unp from RabbitMQ message")
    void updateCustomerWithEmptyUnpFromMessage() {
        //given
        CustomerMessagePayload payload = new CustomerMessagePayloadTestBuilder().build();
        payload.setUnp(null);
        Customer updatedCustomer = new CustomerTestBuilder().build();
        updatedCustomer.setEmail("newEmail@mail.ru");
        Customer customerToUpdate = new CustomerTestBuilder().build();

        //when
        doReturn(Optional.of(customerToUpdate))
                .when(customerRepository)
                .findByIdOrEmail(payload.getId(), payload.getEmail());

        doReturn(updatedCustomer)
                .when(customerMapper)
                .partialUpdate(payload, customerToUpdate);


        //then
        customerService.saveOrUpdateFromMessage(payload);
        verify(customerRepository).findByIdOrEmail(payload.getId(), payload.getEmail());
    }

    @Test
    @DisplayName("should update customer with unp from RabbitMQ message")
    void updateCustomerWithUnpFromMessage() {
        //given
        CustomerMessagePayload payload = new CustomerMessagePayloadTestBuilder().build();
        payload.setUnp("2146812");
        Customer updatedCustomer = new CustomerTestBuilder().build();
        updatedCustomer.setEmail("newEmail@mail.ru");
        Customer customerToUpdate = new CustomerTestBuilder().build();

        //when
        doReturn(Optional.of(customerToUpdate))
                .when(customerRepository)
                .findByIdOrEmailOrUnp(payload.getId(), payload.getEmail(), payload.getUnp());

        doReturn(updatedCustomer)
                .when(customerMapper)
                .partialUpdate(payload, customerToUpdate);


        //then
        customerService.saveOrUpdateFromMessage(payload);
        verify(customerRepository).findByIdOrEmailOrUnp(payload.getId(), payload.getEmail(), payload.getUnp());
    }

    @Test
    @DisplayName("should delete customer by id")
    void deleteCustomerById() {
        //given
        UUID id = UUID.randomUUID();
        //when
        customerService.deleteCustomer(id);
        //then
        verify(customerRepository).deleteById(id);
    }

    @Test
    @DisplayName("should return true if customer exists by id or email or unp")
    void existsByIdOrEmailOrUnp() {
        //given
        UUID id = UUID.randomUUID();
        String email = "email@mail.ru";
        String unp = "2146812";
        //when
        doReturn(true)
                .when(customerRepository)
                .existsByIdOrEmailOrUnp(id, email, unp);
        //then
        boolean result = customerService.isCustomerExist(id, email, unp);
        assertTrue(result);
    }

    @Test
    @DisplayName("should return false if customer not exists by id or email")
    void existsByIdOrEmail() {
        //given
        UUID id = UUID.randomUUID();
        String email = "email@mail.ru";
        //when
        doReturn(false)
                .when(customerRepository)
                .existsByIdOrEmail(id, email);
        //then
        boolean result = customerService.isCustomerExist(id, email);
        assertFalse(result);
    }
}
