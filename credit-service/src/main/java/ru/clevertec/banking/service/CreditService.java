package ru.clevertec.banking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.clevertec.banking.dto.CreditRequest;
import ru.clevertec.banking.dto.CreditRequestForUpdate;
import ru.clevertec.banking.dto.CreditResponse;

import java.util.List;
import java.util.UUID;

public interface CreditService {
    CreditResponse save(CreditRequest request);

    List<CreditResponse> findByCustomer(UUID customerId);

    CreditResponse findByContractNumber(String contractNumber);

    Page<CreditResponse> getAll(Pageable pageable);

    CreditResponse update(CreditRequestForUpdate request);

    void delete(String contractNumber);

    void saveOrUpdate(CreditRequest request);
}
