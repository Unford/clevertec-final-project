package ru.clevertec.banking.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.clevertec.banking.advice.exception.ResourceNotFoundException;
import ru.clevertec.banking.dto.CreditRequest;
import ru.clevertec.banking.dto.CreditRequestForUpdate;
import ru.clevertec.banking.dto.CreditResponse;
import ru.clevertec.banking.entity.Credit;
import ru.clevertec.banking.exception.CreditOperationException;
import ru.clevertec.banking.mapper.CreditMapper;
import ru.clevertec.banking.repository.CreditRepository;
import ru.clevertec.banking.service.impl.CreditServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static ru.clevertec.banking.util.CreditFactory.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
public class CreditServiceTest {
    @Mock
    private CreditRepository repository;
    @Spy
    private CreditMapper mapper = Mappers.getMapper(CreditMapper.class);
    @InjectMocks
    private CreditServiceImpl service;

    @Test
    @DisplayName("test should return expected response")
    void saveTest(){
        CreditRequest request = getRequest();
        CreditResponse expected = getResponse();

        doReturn(getCredit())
                .when(repository).save(Mockito.any(Credit.class));

        CreditResponse actual = service.save(request);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("test should throw CreditOperationException")
    void saveExceptionTest(){
        doReturn(null)
                .when(repository).save(Mockito.any(Credit.class));

        assertThatThrownBy(()->service.save(getRequest())).isInstanceOf(CreditOperationException.class);
    }

    @Test
    @DisplayName("test should return expected list with responses")
    void findByCustomerTest(){
        List<Credit> credits = List.of(getCredit(),getCredit());
        UUID customer_id = getCredit().getCustomerId();

        doReturn(credits)
                .when(repository).findCreditsByCustomerId(Mockito.any(UUID.class));

        List<CreditResponse> actual = service.findByCustomer(customer_id);
        List<CreditResponse> expected = credits.stream().map(mapper::toResponse).toList();

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("test should return expected response")
    void findByContractNumberTest(){
        Credit credit = getCredit();
        CreditResponse expected = getResponse();

        doReturn(credit)
                .when(repository).findCreditByContractNumber(Mockito.anyString());

        CreditResponse actual = service.findByContractNumber(credit.getContractNumber());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("test should throw ResourceNotFoundException")
    void findByContractNumberNotFoundTest(){
        doReturn(null)
                .when(repository).findCreditByContractNumber(Mockito.anyString());

        assertThatThrownBy(()-> service.findByContractNumber("NOT FOUND")).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("test should return Page of credits")
    @SuppressWarnings("unchecked")
    void getAll(){
        List<Credit> credits = List.of(getCredit(),getCredit());
        Pageable pageable = Pageable.ofSize(20);
        Page<Credit> page = new PageImpl(credits,pageable,2);

        doReturn(page)
                .when(repository).findAll(Mockito.any(Pageable.class));

        Page<CreditResponse> actual = service.getAll(pageable);

        assertThat(actual).isNotEmpty().contains(mapper.toResponse(getCredit()));
    }

    @Test
    @DisplayName("test should return expected response after update")
    void updateTest(){
        CreditRequestForUpdate request = getRequestForUpdate();
        Credit credit = getCredit();

        doReturn(credit)
                .when(repository).findCreditByContractNumber(Mockito.anyString());
        doAnswer(o -> o.getArguments()[0])
                .when(repository).save(Mockito.any(Credit.class));

        CreditResponse actual = service.update(request);
        CreditResponse expected = Optional.of(request)
                .map(c -> mapper.updateFromRequest(c,credit))
                .map(mapper::toResponse)
                .orElseThrow();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("test should throw ResourceNotFoundException")
    void updateNotFoundTest(){
        doReturn(null)
                .when(repository).findCreditByContractNumber(Mockito.anyString());

        assertThatThrownBy(()->service.update(getRequestForUpdate())).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("test should throw CreditOperationException")
    void updateSaveExceptionTest(){
        doReturn(getCredit())
                .when(repository).findCreditByContractNumber(Mockito.anyString());
        doReturn(null)
                .when(repository).save(Mockito.any(Credit.class));

        assertThatThrownBy(()->service.update(getRequestForUpdate())).isInstanceOf(CreditOperationException.class);
    }

    @Test
    @DisplayName("the test should check whether the method is called")
    void delete(){
        service.delete("12491925");

        verify(repository,times(1)).deleteByContractNumber(Mockito.anyString());
    }

}
