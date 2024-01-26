package ru.clevertec.banking.auth.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import ru.clevertec.banking.auth.entity.Role;
import ru.clevertec.banking.auth.service.AuthenticationService;
import ru.clevertec.banking.auth.service.UserService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminsRegistrationInitializerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserService userService;

    @Mock
    private ApplicationContext eventPublisher;

    @InjectMocks
    private AdminsRegistrationInitializer initializer;

    @Test
    void onApplicationEventWhenNoUsersExistShouldRegisterAdmins() {
        when(userService.count()).thenReturn(0L);

        initializer.onApplicationEvent(new ContextRefreshedEvent(eventPublisher));

        verify(authenticationService, times(1)).registerAdmins(
                argThat(userDto -> "clevertec@ccc.ru".equals(userDto.getEmail()) && userDto.getRole() == Role.ADMIN)
        );

        verify(authenticationService, times(1)).registerAdmins(
                argThat(userDto -> "clevertec-banking@ccc.ru".equals(userDto.getEmail()) && userDto.getRole() == Role.SUPER_USER)
        );
    }

    @Test
    void onApplicationEventWhenUsersExistShouldNotRegisterAdmins() {
        when(userService.count()).thenReturn(2L);

        initializer.onApplicationEvent(new ContextRefreshedEvent(eventPublisher));

        verify(authenticationService, never()).registerAdmins(any());
    }
}
