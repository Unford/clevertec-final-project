package ru.clevertec.banking.service;


import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;
import ru.clevertec.banking.advice.exception.ResourceNotFoundException;
import ru.clevertec.banking.dto.account.AccountRequest;
import ru.clevertec.banking.dto.account.AccountRequestForUpdate;
import ru.clevertec.banking.dto.account.AccountResponse;
import ru.clevertec.banking.dto.account.AccountWithCardResponse;
import ru.clevertec.banking.entity.Account;
import ru.clevertec.banking.exception.ResourceCreateException;
import ru.clevertec.banking.exception.RestApiServerException;
import ru.clevertec.banking.mapper.AccountMapper;
import ru.clevertec.banking.mapper.CardMapper;
import ru.clevertec.banking.repository.AccountRepository;
import ru.clevertec.banking.repository.specifications.FilterSpecifications;
import ru.clevertec.banking.service.impl.AccountServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static ru.clevertec.banking.util.CardFactory.*;
import static ru.clevertec.banking.util.AccountFactory.*;


@ExtendWith(MockitoExtension.class)
@Tag("unit")
public class AccountServiceTest {
    @Mock
    private AccountRepository repository;

    @Spy
    private static AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    @Spy
    private static CardMapper cardMapper = Mappers.getMapper(CardMapper.class);

    @Spy
    private FilterSpecifications<Account> specifications;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeAll
    static void init() {
        ReflectionTestUtils.setField(
                accountMapper,
                "cardMapper",
                cardMapper
        );
    }

    @Test
    @DisplayName("test should return expected response")
    void saveAccountTest() {
        AccountRequest request = getAccountRequest();
        Account expected = getAccountWithCards(null, false);

        doReturn(expected)
                .when(repository).save(Mockito.any());

        Account actual = Optional.of(request)
                .map(accountService::save)
                .map(accountMapper::fromResponse)
                .orElseThrow();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("test should throw ResourceCreateException")
    void saveAccountExceptionTest() {
        AccountRequest request = getAccountRequest();

        doReturn(null)
                .when(repository).save(Mockito.any());

        assertThatThrownBy(() -> accountService.save(request))
                .isInstanceOf(ResourceCreateException.class);
    }

    @Test
    @DisplayName("test should return expected response")
    void findByIbanTest() {
        Account expected = getAccountWithCards(null, false);

        doReturn(Optional.of(expected))
                .when(repository).findAccountByIban(Mockito.any());

        Account actual = Optional.of(expected)
                .map(Account::getIban)
                .map(accountService::findByIban)
                .map(accountMapper::fromResponse)
                .orElseThrow();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("test should throw ResourceNotFountException")
    void findByIbanWithExceptionTest() {
        Account expected = getAccountWithCards(null, false);

        doReturn(Optional.empty())
                .when(repository).findAccountByIban(Mockito.any());

        assertThatThrownBy(() -> accountService.findByIban(expected.getIban()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("test should return expected list with responses")
    @SuppressWarnings("unchecked")
    void findByCustomerTest() {

        Account account = getAccountWithCards(List.of(getCard(null, false)), false);
        List<AccountWithCardResponse> expected = List.of(accountMapper.toResponseWithCards(account, account.getCards()));

        doReturn(List.of(account))
                .when(repository).findAll(Mockito.any(Specification.class));

        List<AccountWithCardResponse> actual = accountService.findByCustomer(account.getCustomerId());

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);

    }

    @Test
    @DisplayName("test should return expected list with responses")
    void getAllTest(){
        Account account = getAccountWithCards(List.of(getCard(null, false)), false);
        List<Account> accountList = List.of(account);
        Pageable pageable = PageRequest.of(0, 1);

        Page<Account> expected = new PageImpl<>(accountList,pageable,1L);

        doReturn(expected)
                .when(repository).findAll(pageable);

        Page<AccountWithCardResponse> actual = accountService.getAll(pageable);

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected
                .map(acc -> accountMapper.toResponseWithCards(acc,acc.getCards())));
    }

    @Test
    @DisplayName("test should return expected response after update")
    void updateTest(){
        Account account = getAccountWithCards(new ArrayList<>(),false);
        AccountRequestForUpdate requestForUpdate = new AccountRequestForUpdate(
                account.getIban(),
                "New Name",
                false,
                "PHYSIC");

        AccountResponse expected = Optional.of(account)
                .map(acc -> accountMapper.updateFromRequest(requestForUpdate,account))
                .map(accountMapper::toResponse)
                .orElseThrow();

        doReturn(Optional.of(account))
                .when(repository).findAccountByIban(Mockito.any());

        doAnswer(o -> (Account)o.getArguments()[0])
                .when(repository).save(Mockito.any(Account.class));

        AccountResponse actual = accountService.update(requestForUpdate);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("the test should check whether the method throws an error if the entity being updated is not found")
    void updateNotFoundTest(){
        AccountRequestForUpdate requestForUpdate = new AccountRequestForUpdate(
                "Iban Not Found",
                "New Name",
                false,
                "PHYSIC");

        doReturn(Optional.empty())
                .when(repository).findAccountByIban(Mockito.any());

        assertThatThrownBy(() -> accountService.update(requestForUpdate))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("test should throw RestApiServerException")
    void updateExceptionTest(){
        Account account = getAccountWithCards(new ArrayList<>(),false);
        AccountRequestForUpdate requestForUpdate = new AccountRequestForUpdate(
                "Iban Not Found",
                "New Name",
                false,
                "PHYSIC");

        doReturn(Optional.of(account))
                .when(repository).findAccountByIban(Mockito.any());

        doReturn(null)
                .when(repository).save(Mockito.any(Account.class));

        assertThatThrownBy(() -> accountService.update(requestForUpdate))
                .isInstanceOf(RestApiServerException.class);
    }

    @Test
    @DisplayName("the test should check whether the method is called")
    void deleteByIbanTest(){
        accountService.deleteByIban("IBAN");

        verify(repository,times(1)).deleteAccountByIban(Mockito.any());
    }
}
