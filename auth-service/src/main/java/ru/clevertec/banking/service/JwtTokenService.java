package ru.clevertec.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.clevertec.banking.config.TokenConfig;
import ru.clevertec.banking.entity.Role;

import java.util.Collections;
import java.util.List;

@Service
public class JwtTokenService extends AbstractTokenService {
    private final TokenConfig jwtConfig;

    @Autowired
    public JwtTokenService(TokenConfig jwtConfig) {
        super(jwtConfig);
        this.jwtConfig = jwtConfig;
    }

    public String generateToken(Long userId, List<Role> authorities) {
        return generateToken(Collections.singletonMap("authorities", authorities),
                             userId, jwtConfig.getTokenExpirationMs());
    }
}
