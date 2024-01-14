package ru.clevertec.banking.auth.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import ru.clevertec.banking.auth.dto.UserCredentialsDto;
import ru.clevertec.banking.auth.entity.Role;
import ru.clevertec.banking.auth.service.AuthenticationService;
import ru.clevertec.banking.auth.service.UserService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AdminsRegistrationInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final AuthenticationService authenticationService;
    private final UserService userService;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (userService.count() == 0) {
            authenticationService.registerAdmins(
                    new UserCredentialsDto()
                            .setId(UUID.randomUUID())
                            .setEmail("clevertec@ccc.ru")
                            .setPassword("clevertec")
                            .setRole(Role.ADMIN)
            );

            authenticationService.registerAdmins(
                    new UserCredentialsDto()
                            .setId(UUID.randomUUID())
                            .setEmail("clevertec-banking@ccc.ru")
                            .setPassword("clevertec-banking")
                            .setRole(Role.SUPER_USER)
            );
        }
    }
}