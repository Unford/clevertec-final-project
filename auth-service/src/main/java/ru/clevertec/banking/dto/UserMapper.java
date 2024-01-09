package ru.clevertec.banking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.clevertec.banking.entity.UserCredentials;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserCredentials toEntity(UserCredentialsDto userCredentialsDto);

    UserCredentialsDto toDto(UserCredentials userEntity);
}