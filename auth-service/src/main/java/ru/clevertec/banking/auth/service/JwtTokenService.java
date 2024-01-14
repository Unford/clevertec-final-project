package ru.clevertec.banking.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.clevertec.banking.auth.config.TokenConfig;
import ru.clevertec.banking.auth.entity.Role;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class JwtTokenService extends AbstractTokenService {
    private final TokenConfig jwtConfig;

    @Autowired
    public JwtTokenService(TokenConfig jwtConfig) {
        super(jwtConfig);
        this.jwtConfig = jwtConfig;
    }

    public String generateToken(UUID userId, List<Role> authorities) {
        return generateToken(Collections.singletonMap("authorities", authorities),
                             userId, jwtConfig.getTokenExpirationMs());
    }
}
