package ru.clevertec.banking.customer.mapper;

import org.mapstruct.*;
import ru.clevertec.banking.customer.dto.message.CustomerMessagePayload;
import ru.clevertec.banking.customer.dto.request.CreateCustomerRequest;
import ru.clevertec.banking.customer.dto.response.CustomerResponse;
import ru.clevertec.banking.customer.entity.Customer;
import ru.clevertec.banking.customer.entity.CustomerType;
import ru.clevertec.banking.customer.exception.InvalidCustomerTypeException;

import java.util.Objects;

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
    @Mapping(target = "customerType", expression = "java(updateCustomerType(source, target))")
    @Mapping(target = "id", ignore = true)
    Customer partialUpdate(CustomerMessagePayload source, @MappingTarget Customer target);

    default CustomerType updateCustomerType(CustomerMessagePayload source, @MappingTarget Customer target) {
        if (Objects.equals(source.getCustomerType(), CustomerType.PHYSIC.toString())
            && target.getCustomerType() == CustomerType.LEGAL) {
            target.setUnp(null);
        }
        return source.getCustomerType() == null ? target.getCustomerType() : stringToCustomerType(source.getCustomerType());
    }
}