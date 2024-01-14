package ru.clevertec.banking.deposit.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.clevertec.banking.deposit.model.domain.Deposit;
import ru.clevertec.banking.deposit.model.dto.message.DepositMessagePayload;

@Mapper
public interface DepositMapper {
    Deposit toDeposit(DepositMessagePayload messagePayload);

    Deposit updateDeposit(DepositMessagePayload messagePayload, @MappingTarget Deposit deposit);

}
