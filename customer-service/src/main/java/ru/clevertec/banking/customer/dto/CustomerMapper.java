package ru.clevertec.banking.customer.dto;

import org.mapstruct.*;
import ru.clevertec.banking.customer.dto.message.CustomerMessagePayload;
import ru.clevertec.banking.customer.dto.request.CreateCustomerRequest;
import ru.clevertec.banking.customer.dto.response.CustomerResponse;
import ru.clevertec.banking.customer.entity.Customer;
import ru.clevertec.banking.customer.entity.CustomerType;
import ru.clevertec.banking.customer.exception.InvalidCustomerTypeException;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {

    @Mapping(target = "id", source = "id", defaultExpression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "customerType", qualifiedByName = "stringToCustomerType")
    Customer toEntity(CreateCustomerRequest createCustomerRequest);

    @Mapping(target = "customerType", qualifiedByName = "stringToCustomerType")
    Customer toEntityFromCustomerPayload(CustomerMessagePayload payload);

    CustomerMessagePayload toCustomerPayloadFromResponse(CustomerResponse customerResponse);

    CustomerResponse toCustomerResponse(Customer customer);

    @Named("stringToCustomerType")
    default CustomerType stringToCustomerType(String customerType) {
        return switch (customerType) {
            case "LEGAL" -> CustomerType.LEGAL;
            case "PHYSIC" -> CustomerType.PHYSIC;
            default -> throw new InvalidCustomerTypeException(String.format("Invalid customer type: %s", customerType));
        };
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Customer partialUpdate(CustomerMessagePayload source, @MappingTarget Customer target);
}