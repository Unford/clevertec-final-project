package ru.clevertec.banking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.banking.advice.exception.ResourceNotFoundException;
import ru.clevertec.banking.dto.CreditRequest;
import ru.clevertec.banking.dto.CreditRequestForUpdate;
import ru.clevertec.banking.dto.CreditResponse;
import ru.clevertec.banking.exception.CreditOperationException;
import ru.clevertec.banking.mapper.CreditMapper;
import ru.clevertec.banking.repository.CreditRepository;
import ru.clevertec.banking.service.CreditService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {
    private final CreditRepository repository;
    private final CreditMapper mapper;

    @Transactional
    @Override
    @CachePut(key = "#result.contractNumber()")
    public CreditResponse save(CreditRequest request) {
        return Optional.of(request)
                .map(mapper::fromRequest)
                .map(repository::save)
                .map(mapper::toResponse)
                .orElseThrow(() -> new CreditOperationException("Failed to create credit"));
    }

    @Override
    public List<CreditResponse> findByCustomer(UUID customerId) {
        return repository.findCreditsByCustomerId(customerId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @CachePut(key = "#contractNumber")
    public CreditResponse findByContractNumber(String contractNumber) {
        return Optional.of(contractNumber)
                .map(repository::findCreditByContractNumber)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Credit with contractNumber: %s not found"
                        .formatted(contractNumber)));
    }

    @Override
    public Page<CreditResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Transactional
    @Override
    @CachePut(key = "#request.contractNumber()")
    public CreditResponse update(CreditRequestForUpdate request) {
        return Optional.of(request)
                .map(CreditRequestForUpdate::contractNumber)
                .map(this::findByContractNumber)
                .map(mapper::fromResponse)
                .map(credit -> mapper.updateFromRequest(request, credit))
                .map(repository::save)
                .map(mapper::toResponse)
                .orElseThrow(() -> new CreditOperationException("Failed to update credit with contractNumber: %s"
                        .formatted(request.contractNumber())));
    }

    @Override
    @Transactional
    public void saveOrUpdate(CreditRequest request) {
        Optional.of(request)
                .map(CreditRequest::contractNumber)
                .map(repository::findCreditByContractNumberWithDeleted)
                .flatMap(o -> o)
                .ifPresentOrElse(cred -> repository.save(mapper.updateFromMessage(mapper.fromRequest(request), cred)),
                        () -> repository.save(mapper.fromRequest(request)));
    }

    @Transactional
    @Override
    @CacheEvict(key = "#contractNumber")
    public void delete(String contractNumber) {
        repository.deleteByContractNumber(contractNumber);
    }
}
