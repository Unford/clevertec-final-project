package ru.clevertec.banking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.banking.advice.exception.ResourceNotFoundException;
import ru.clevertec.banking.dto.CustomerMapper;
import ru.clevertec.banking.dto.request.CreateCustomerRequest;
import ru.clevertec.banking.dto.request.GetCustomersPageableRequest;
import ru.clevertec.banking.dto.response.CustomerResponse;
import ru.clevertec.banking.entity.Customer;
import ru.clevertec.banking.exception.CustomerOperationException;
import ru.clevertec.banking.repository.CustomerRepository;
import ru.clevertec.banking.repository.CustomerSpecification;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;


    public Page<CustomerResponse> getCustomersPageable(GetCustomersPageableRequest request) {
        Specification<Customer> specification =
                CustomerSpecification.filterChannels(request.getRegisterDate(), request.getCustomerTypeEnum());
        return customerRepository.findAll(specification, request.toPageable())
                                 .map(customerMapper::toCustomerResponse);
    }

    public CustomerResponse getCustomersById(UUID id) {
        return customerRepository.findById(id)
                                 .map(customerMapper::toCustomerResponse)
                                 .orElseThrow(() -> new ResourceNotFoundException(
                                         String.format("Customer with id %s not found", id)));
    }

    public CustomerResponse getCustomersByEmail(String email) {
        return customerRepository.findByEmail(email)
                                 .map(customerMapper::toCustomerResponse)
                                 .orElseThrow(() -> new ResourceNotFoundException(
                                         String.format("Customer with email %s not found", email)));
    }


    public CustomerResponse getCustomersByUnp(String unp) {
        return customerRepository.findByUnpAndUnpNotNull(unp)
                                 .map(customerMapper::toCustomerResponse)
                                 .orElseThrow(() -> new ResourceNotFoundException(
                                         String.format("Customer with unp %s not found", unp)));
    }

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest customerRequest) {
        return Optional.of(customerRequest)
                       .map(customerMapper::toEntity)
                       .map(customerRepository::save)
                       .map(customerMapper::toCustomerResponse)
                       .orElseThrow(() -> new CustomerOperationException("Failed to create customer"));
    }

    @Transactional
    public void deleteCustomer(UUID id) {
        customerRepository.deleteById(id);
    }


    public boolean isCustomerExist(UUID id, String email, String unp) {
        return customerRepository.existsByIdOrEmailOrUnp(id, email, unp);
    }

    public boolean isCustomerExist(UUID id, String email) {
        return customerRepository.existsByIdOrEmail(id, email);
    }
}
