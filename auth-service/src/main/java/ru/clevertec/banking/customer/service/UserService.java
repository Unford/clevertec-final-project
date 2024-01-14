package ru.clevertec.banking.customer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.banking.customer.dto.UserCredentialsDto;
import ru.clevertec.banking.customer.dto.UserMapper;
import ru.clevertec.banking.customer.exception.UserOperationException;
import ru.clevertec.banking.customer.repository.UserCredentialsRepository;

import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserCredentialsRepository userRepository;
    private final UserMapper userMapper;

    public Optional<UserCredentialsDto> getOptionalByEmail(String email) {
        return userRepository.findByEmail(email)
                             .map(userMapper::toDto);
    }
    @Transactional
    public UserCredentialsDto save(UserCredentialsDto user) {
        return Stream.of(user)
                     .map(userMapper::toEntity)
                     .map(userRepository::save)
                     .map(userMapper::toDto)
                     .findFirst()
                     .orElseThrow(() -> new UserOperationException(
                             String.format("User with email = %s not saved", user.getEmail())));
    }
}
