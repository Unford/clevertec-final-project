package ru.clevertec.banking.deposit.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.clevertec.banking.deposit.model.dto.response.DepositResponse;
import ru.clevertec.banking.deposit.service.DepositService;
import ru.clevertec.banking.deposit.util.RandomDepositFactory;
import ru.clevertec.banking.deposit.util.SpringUnitCompositeTest;
import ru.clevertec.banking.security.model.Role;

import java.util.List;
import java.util.UUID;

@SpringUnitCompositeTest
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class CustomSecurityExpressionTest {

    @Autowired
    RandomDepositFactory randomDepositFactory;

    @Spy
    DepositService depositService = new DepositService(null, null , null);

    @InjectMocks
    CustomSecurityExpression customSecurityExpression;
    @Mock
    Authentication authentication;


    @Test
    void shouldReturnTrueForUserRoleAndEqualId() {
        List<SimpleGrantedAuthority> authorities = List.of(Role.ADMIN.toAuthority(), Role.USER.toAuthority());
        UUID uuid = UUID.randomUUID();

        Mockito.when(authentication.getAuthorities()).thenAnswer(a -> authorities);
        Mockito.when(authentication.getPrincipal()).thenReturn(uuid);

        boolean actual = customSecurityExpression.hasUserRoleAndIdEquals(uuid, authentication);

        Assertions.assertThat(actual).isTrue();
        Mockito.verify(authentication).getAuthorities();
        Mockito.verify(authentication).getPrincipal();
    }

    @Test
    void shouldReturnFalseForUserRoleAndNotEqualId() {
        List<SimpleGrantedAuthority> authorities = List.of(Role.USER.toAuthority());

        Mockito.when(authentication.getAuthorities()).thenAnswer(a -> authorities);
        Mockito.when(authentication.getPrincipal()).thenReturn(UUID.randomUUID());

        boolean actual = customSecurityExpression.hasUserRoleAndIdEquals(UUID.randomUUID(), authentication);

        Assertions.assertThat(actual).isFalse();
        Mockito.verify(authentication).getAuthorities();
        Mockito.verify(authentication).getPrincipal();
    }

    @Test
    void shouldReturnFalseForNotUserRole() {
        List<SimpleGrantedAuthority> authorities = List.of(Role.ADMIN.toAuthority());

        Mockito.when(authentication.getAuthorities()).thenAnswer(a -> authorities);

        boolean actual = customSecurityExpression.hasUserRoleAndIdEquals(UUID.randomUUID(), authentication);

        Assertions.assertThat(actual).isFalse();
        Mockito.verify(authentication).getAuthorities();
        Mockito.verify(authentication, Mockito.never()).getPrincipal();
    }


    @Test
    void shouldReturnTrueForUserRoleAndCustomerOwnDeposit() {
        List<SimpleGrantedAuthority> authorities = List.of(Role.USER.toAuthority());

        DepositResponse depositResponse = randomDepositFactory.createDepositResponse();

        Mockito.when(authentication.getAuthorities()).thenAnswer(a -> authorities);
        Mockito.when(authentication.getPrincipal()).thenReturn(depositResponse.getCustomerId());
        Mockito.doReturn(depositResponse).when(depositService).findByAccountIban(Mockito.anyString());

        boolean actual = customSecurityExpression.hasUserRoleAndOwnDeposit(depositResponse.getAccInfo().getAccIban(),
                authentication);

        Assertions.assertThat(actual).isTrue();
        Mockito.verify(authentication).getAuthorities();
        Mockito.verify(authentication).getPrincipal();
        Mockito.verify(depositService).findByAccountIban(Mockito.anyString());
    }

    @Test
    void shouldReturnFalseForUserRoleAndCustomerNotOwnDeposit() {
        List<SimpleGrantedAuthority> authorities = List.of(Role.USER.toAuthority());

        DepositResponse depositResponse = randomDepositFactory.createDepositResponse();

        Mockito.when(authentication.getAuthorities()).thenAnswer(a -> authorities);
        Mockito.when(authentication.getPrincipal()).thenReturn(UUID.randomUUID());
        Mockito.doReturn(depositResponse).when(depositService).findByAccountIban(Mockito.anyString());

        boolean actual = customSecurityExpression.hasUserRoleAndOwnDeposit(depositResponse.getAccInfo().getAccIban(),
                authentication);

        Assertions.assertThat(actual).isFalse();
        Mockito.verify(authentication).getAuthorities();
        Mockito.verify(authentication).getPrincipal();
        Mockito.verify(depositService).findByAccountIban(Mockito.anyString());
    }


}
