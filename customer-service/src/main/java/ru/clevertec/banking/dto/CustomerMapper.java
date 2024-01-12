package ru.clevertec.banking.dto;

import org.mapstruct.*;
import ru.clevertec.banking.dto.request.CreateCustomerRequest;
import ru.clevertec.banking.dto.response.CustomerResponse;
import ru.clevertec.banking.entity.Customer;
import ru.clevertec.banking.entity.CustomerType;
import ru.clevertec.banking.exception.InvalidCustomerTypeException;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {

    @Mapping(target = "id", source = "id", defaultExpression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "customerType", qualifiedByName = "stringToCustomerType")
    Customer toEntity(CreateCustomerRequest createCustomerRequest);

    CustomerResponse toCustomerResponse(Customer customer);

    @Named("stringToCustomerType")
    default CustomerType stringToCustomerType(String customerType) {
        return switch (customerType) {
            case "LEGAL" -> CustomerType.LEGAL;
            case "PHYSIC" -> CustomerType.PHYSIC;
            default -> throw new InvalidCustomerTypeException(String.format("Invalid customer type: %s", customerType));
        };
    }
}