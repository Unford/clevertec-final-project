package ru.clevertec.banking.dto;

import org.mapstruct.*;
import ru.clevertec.banking.dto.response.SmallUserCredentialsReponse;
import ru.clevertec.banking.entity.UserCredentials;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserCredentials toEntity(UserCredentialsDto userCredentialsDto);

    UserCredentialsDto toDto(UserCredentials userEntity); // TODO mb delete
    SmallUserCredentialsReponse toSmallDto(UserCredentials userEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserCredentials partialUpdate(
            UserCredentialsDto userCredentialsDto, @MappingTarget UserCredentials userEntity);
}