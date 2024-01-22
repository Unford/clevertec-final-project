package ru.clevertec.banking.auth.service;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.banking.advice.exception.ResourceNotFoundException;
import ru.clevertec.banking.auth.dto.UserCredentialsDto;
import ru.clevertec.banking.auth.entity.UserCredentials;
import ru.clevertec.banking.auth.exception.UserOperationException;
import ru.clevertec.banking.auth.mapper.UserMapper;
import ru.clevertec.banking.auth.repository.UserCredentialsRepository;
import ru.clevertec.banking.auth.testutil.builders.UserCredentialsDtoTestBuilder;
import ru.clevertec.banking.auth.testutil.builders.UserCredentialsTestBuilder;

import java.util.Optional;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserCredentialsRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @Test
    @DisplayName("should return user by email")
    void shouldReturnUserByEmail() {
        //given
        String email = "email@mail.ru";
        UserCredentials user = new UserCredentialsTestBuilder().build();
        UserCredentialsDto expectedResponse = new UserCredentialsDtoTestBuilder().build();
        //when
        doReturn(Optional.of(user))
                .when(userRepository)
                        .findByEmail(email);

        doReturn(expectedResponse)
                .when(userMapper)
                        .toDto(user);
        //then
        UserCredentialsDto actualResponse = userService.getByEmail(email);
        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("should save user")
    void shouldSaveUser() {
        //given
        UserCredentialsDto userDto = new UserCredentialsDtoTestBuilder().build();
        UserCredentials user = new UserCredentialsTestBuilder().build();
        //when
        doReturn(user)
                .when(userMapper)
                        .toEntity(userDto);

        doReturn(user)
                .when(userRepository)
                .save(user);

        doReturn(userDto)
                .when(userMapper)
                .toDto(user);
        //then
        UserCredentialsDto actualResponse = userService.save(userDto);
        Assertions.assertThat(actualResponse).isEqualTo(userDto);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException by email")
    void shouldThrowResourceNotFoundExceptionByEmail() {
        //given
        String email = "email@mail.ru";
        String errorMessage = String.format("User with email: %s not found", email);
        //when
        doReturn(Optional.empty())
                .when(userRepository)
                .findByEmail(email);
        //then
        Assertions.assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> userService.getByEmail(email)).withMessage(errorMessage);
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("should throw UserOperationException when saving user")
    void shouldThrowUserOperationException() {
        //given
        UserCredentialsDto userDto = new UserCredentialsDtoTestBuilder().build();
        UserCredentials user = new UserCredentialsTestBuilder().build();
        String errorMessage = String.format("Failed to save user with email: %s", userDto.getEmail());
        //when
        doReturn(null)
                .when(userRepository)
                .save(user);

        doReturn(user)
                .when(userMapper)
                .toEntity(userDto);
        //then
        Assertions.assertThatExceptionOfType(UserOperationException.class)
                .isThrownBy(() -> userService.save(userDto)).withMessage(errorMessage);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("should count users")
    void shouldCountUsers() {
        //given
        Long expectedCount = 1L;
        //when
        doReturn(expectedCount)
                .when(userRepository)
                .count();
        //then
        Long actualCount = userService.count();
        Assertions.assertThat(actualCount).isEqualTo(expectedCount);
        verify(userRepository).count();
    }

}
