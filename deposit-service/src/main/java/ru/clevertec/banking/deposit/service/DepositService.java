package ru.clevertec.banking.deposit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.banking.deposit.mapper.DepositMapper;
import ru.clevertec.banking.deposit.model.CustomerType;
import ru.clevertec.banking.deposit.model.DepositType;
import ru.clevertec.banking.deposit.model.TermScale;
import ru.clevertec.banking.deposit.model.domain.AccountInfo;
import ru.clevertec.banking.deposit.model.domain.Deposit;
import ru.clevertec.banking.deposit.model.domain.DepositInfo;
import ru.clevertec.banking.deposit.model.dto.message.DepositMessagePayload;
import ru.clevertec.banking.deposit.repository.DepositRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepositService {
    private final DepositRepository depositRepository;
    private final DepositMapper depositMapper;





    @Transactional
    public void saveFromMessage(DepositMessagePayload payload) {
        Deposit deposit = depositRepository.findByAccInfoAccIban(payload.getAccInfo().getAccIban())
                .map(d -> depositMapper.updateDeposit(payload, d))
                .orElseGet(() -> depositMapper.toDeposit(payload));
        depositRepository.save(deposit);
    }
}
