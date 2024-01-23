package ru.clevertec.banking.deposit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
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
import ru.clevertec.banking.deposit.repository.DepositSpecifications;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "deposit")
public class DepositService {
    private final DepositRepository depositRepository;
    private final DepositMapper depositMapper;
    private final CustomerClient customerClient;


    @Transactional
    @CachePut(key = "#result.accInfo.accIban")
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
    @CachePut(key = "#result.accInfo.accIban")
    public DepositResponse saveFromMessage(DepositMessagePayload payload) {
        Deposit deposit = depositRepository.findByAccInfoAccIbanWithDeleted(payload.getAccInfo().getAccIban())
                .map(d -> depositMapper.updateDeposit(payload, d))
                .orElseGet(() -> depositMapper.toDeposit(payload));
        return depositMapper.toDepositResponse(depositRepository.save(deposit));
    }

    @Cacheable(key = "#iban")
    public DepositResponse findByAccountIban(String iban) {
        return depositRepository.findByAccInfoAccIban(iban)
                .map(depositMapper::toDepositResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Deposit with iban '%s' is not found".formatted(iban)));
    }

    @Transactional
    @CacheEvict(key = "#iban")
    public void deleteByAccountIban(String iban) {
        depositRepository.deleteByAccInfoAccIban(iban);
    }


    public List<DepositResponse> findAllByCustomerId(UUID customerId) {
        return depositRepository.findAllByCustomerId(customerId)
                .stream()
                .map(depositMapper::toDepositResponse)
                .toList();
    }

    public Page<DepositResponse> findPageByRole(Pageable pageable, Authentication authentication) {
        return depositRepository.findAll(DepositSpecifications.filterByUserId(authentication), pageable)
                .map(depositMapper::toDepositResponse);
    }


    public boolean isDepositExistByIban(String iban) {
        return depositRepository.existsByAccInfoAccIban(iban);
    }


    @Transactional
    @CachePut(key = "#iban")
    public DepositResponse update(String iban, UpdateDepositRequest updateDepositRequest) {
        return depositRepository.findByAccInfoAccIban(iban)
                .map(d -> depositMapper.updateDeposit(updateDepositRequest, d))
                .map(depositRepository::save)
                .map(depositMapper::toDepositResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Deposit with iban '%s' is not found".formatted(iban)));

    }
}
