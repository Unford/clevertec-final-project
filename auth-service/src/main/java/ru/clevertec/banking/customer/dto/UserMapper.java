package ru.clevertec.banking.customer.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.clevertec.banking.customer.entity.UserCredentials;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserCredentials toEntity(UserCredentialsDto userCredentialsDto);

    UserCredentialsDto toDto(UserCredentials userEntity);
}