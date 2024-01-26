package ru.clevertec.banking.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.banking.advice.exception.ResourceNotFoundException;
import ru.clevertec.banking.auth.dto.UserCredentialsDto;
import ru.clevertec.banking.auth.mapper.UserMapper;
import ru.clevertec.banking.auth.exception.UserOperationException;
import ru.clevertec.banking.auth.repository.UserCredentialsRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "users")
public class UserService {

    private final UserCredentialsRepository userRepository;
    private final UserMapper userMapper;

    @Cacheable(key = "#email")
    public UserCredentialsDto getByEmail(String email) {
        return userRepository.findByEmail(email)
                             .map(userMapper::toDto)
                             .orElseThrow(() -> new ResourceNotFoundException(
                                     String.format("User with email: %s not found", email)));
    }

    @Transactional
    @CachePut(key = "#result.email")
    public UserCredentialsDto save(UserCredentialsDto user) {
        return Optional.of(user)
                       .map(userMapper::toEntity)
                       .map(userRepository::save)
                       .map(userMapper::toDto)
                       .orElseThrow(() -> new UserOperationException(
                               String.format("Failed to save user with email: %s", user.getEmail())));
    }

    public long count() {
        return userRepository.count();
    }
}