package ru.clevertec.banking.customer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.banking.advice.exception.ResourceNotFoundException;
import ru.clevertec.banking.advice.exception.ResourceUniqueException;
import ru.clevertec.banking.customer.dto.CustomerMapper;
import ru.clevertec.banking.customer.dto.message.CustomerMessagePayload;
import ru.clevertec.banking.customer.dto.request.CreateCustomerRequest;
import ru.clevertec.banking.customer.dto.request.GetCustomersPageableRequest;
import ru.clevertec.banking.customer.dto.response.CustomerResponse;
import ru.clevertec.banking.customer.entity.Customer;
import ru.clevertec.banking.customer.producer.CustomerProducer;
import ru.clevertec.banking.customer.repository.CustomerRepository;
import ru.clevertec.banking.customer.repository.CustomerSpecification;
import ru.clevertec.banking.logging.annotation.Loggable;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerProducer customerProducer;
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

    public CustomerResponse getCustomersByUnp(String unp) {
        return customerRepository.findByUnpAndUnpNotNull(unp)
                                 .map(customerMapper::toCustomerResponse)
                                 .orElseThrow(() -> new ResourceNotFoundException(
                                         String.format("Customer with unp %s not found", unp)));
    }

    @Transactional
    @Loggable
    public CustomerResponse saveCustomer(CreateCustomerRequest customerRequest) {
        Optional<CustomerResponse> customerResponse = Optional.of(customerRequest)
                                                              .map(customerMapper::toEntity)
                                                              .map(customerRepository::save)
                                                              .map(customerMapper::toCustomerResponse);

        customerResponse.ifPresent(customerProducer::prepareAndProduceForward);

        return customerResponse
                .orElseThrow(() -> new ResourceUniqueException("Failed to create customer"));
    }

    @Transactional
    @Loggable
    public void saveOrUpdateFromMessage(CustomerMessagePayload payload) {
        Optional.ofNullable(payload)
                .map(p -> p.getUnp() == null ?
                          customerRepository.findByIdOrEmail(p.getId(), p.getEmail()) :
                          customerRepository.findByIdOrEmailOrUnp(p.getId(), p.getEmail(), p.getUnp())
                )
                .filter(Optional::isPresent)
                .ifPresentOrElse(
                        existingCustomer -> existingCustomer.map(c -> customerMapper.partialUpdate(payload, c)),
                        () -> customerRepository.save(customerMapper.toEntityFromCustomerPayload(payload))
                );
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