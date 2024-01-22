package ru.clevertec.banking.mapper;

import org.mapstruct.*;
import ru.clevertec.banking.dto.CreditRequest;
import ru.clevertec.banking.dto.CreditRequestForUpdate;
import ru.clevertec.banking.dto.CreditResponse;
import ru.clevertec.banking.entity.Credit;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreditMapper {

    @Mapping(target = "customer_id", source = "credit.customerId")
    @Mapping(target = "customer_type", source = "credit.customerType")
    @Mapping(target = "isClosed", source = "credit.closed")
    CreditResponse toResponse(Credit credit);


    @Mapping(target = "customerId", source = "request.customer_id")
    @Mapping(target = "customerType", source = "request.customer_type")
    @Mapping(target = "closed", source = "request.isClosed")
    Credit fromRequest(CreditRequest request);


    @Mapping(target = "credit.repaymentDate", source = "request.repaymentDate",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "credit.rate", source = "request.rate",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "credit.possibleRepayment", source = "request.possibleRepayment",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "credit.closed", source = "request.isClosed",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "credit.customerType", source = "request.customer_type",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Credit updateFromRequest(CreditRequestForUpdate request, @MappingTarget Credit credit);

    @Mapping(target = "customerId", source = "response.customer_id")
    @Mapping(target = "customerType", source = "response.customer_type")
    @Mapping(target = "closed", source = "response.isClosed")
    Credit fromResponse(CreditResponse response);

    Credit updateFromMessage(Credit message, @MappingTarget() Credit credit);
}
