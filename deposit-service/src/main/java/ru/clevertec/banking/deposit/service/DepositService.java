package ru.clevertec.banking.deposit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.banking.advice.exception.ResourceNotFoundException;
import ru.clevertec.banking.deposit.client.CustomerClient;
import ru.clevertec.banking.deposit.mapper.DepositMapper;
import ru.clevertec.banking.deposit.model.domain.Deposit;
import ru.clevertec.banking.deposit.model.dto.message.DepositMessagePayload;
import ru.clevertec.banking.deposit.model.dto.request.CreateDepositRequest;
import ru.clevertec.banking.deposit.model.dto.request.UpdateDepositRequest;
import ru.clevertec.banking.deposit.model.dto.response.DepositResponse;
import ru.clevertec.banking.deposit.repository.DepositRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepositService {
    private final DepositRepository depositRepository;
    private final DepositMapper depositMapper;
    private final CustomerClient customerClient;


    @Transactional
    public DepositResponse save(CreateDepositRequest createDepositRequest) {
        return Optional.ofNullable(customerClient.findByCustomerId(createDepositRequest.getCustomerId()))
                .map(c -> createDepositRequest)
                .map(depositMapper::toDeposit)
                .map(depositRepository::save)
                .map(depositMapper::toDepositResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id %s is not found"
                        .formatted(createDepositRequest.getCustomerId().toString())));

    }

    @Transactional
    public void saveFromMessage(DepositMessagePayload payload) {
        Deposit deposit = depositRepository.findByAccInfoAccIban(payload.getAccInfo().getAccIban())
                .map(d -> depositMapper.updateDeposit(payload, d))
                .orElseGet(() -> depositMapper.toDeposit(payload));
        depositRepository.save(deposit);
    }

    public DepositResponse findByAccountIban(String iban) {
        return depositRepository.findByAccInfoAccIban(iban)
                .map(depositMapper::toDepositResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Deposit with iban '%s' is not found".formatted(iban)));
    }

    @Transactional
    public void deleteByAccountIban(String iban) {
        depositRepository.deleteByAccInfoAccIban(iban);
    }

    public List<DepositResponse> findAllByCustomerId(UUID customerId) {
        return depositRepository.findAllByCustomerId(customerId)
                .stream()
                .map(depositMapper::toDepositResponse)
                .toList();
    }

    public Page<DepositResponse> findPage(Pageable pageable) {
        return depositRepository.findAll(pageable)
                .map(depositMapper::toDepositResponse);
    }


    public boolean isDepositExistByIban(String iban) {
        return depositRepository.existsByAccInfoAccIban(iban);
    }


    @Transactional
    public DepositResponse update(String iban, UpdateDepositRequest updateDepositRequest) {
        return depositRepository.findByAccInfoAccIban(iban)
                .map(d -> depositMapper.updateDeposit(updateDepositRequest, d))
                .map(depositRepository::save)
                .map(depositMapper::toDepositResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Deposit with iban '%s' is not found".formatted(iban)));

    }
}
