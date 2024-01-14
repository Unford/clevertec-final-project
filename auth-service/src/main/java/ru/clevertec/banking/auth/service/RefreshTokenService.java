package ru.clevertec.banking.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.banking.auth.config.TokenConfig;
import ru.clevertec.banking.auth.entity.Role;
import ru.clevertec.banking.auth.repository.RefreshTokenRepository;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class RefreshTokenService extends AbstractTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenConfig refreshConfig;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, TokenConfig refreshConfig) {
        super(refreshConfig);
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshConfig = refreshConfig;
    }

    @Transactional
    public void updateRefreshToken(String refreshToken) {
        final UUID id = extractId(refreshToken);
        refreshTokenRepository.updateRefreshToken(id, refreshToken);
    }

    public boolean isRefreshTokenValid(String providedRefreshToken) {
        final UUID id = extractId(providedRefreshToken);
        return refreshTokenRepository.existsByIdAndRefreshToken(id, providedRefreshToken);
    }

    public boolean isRefreshTokenNotExpired(String refreshToken) {
        return extractExpiration(refreshToken).after(new Date());
    }

    public String generateRefreshToken(UUID userId, List<Role> authorities) {
        return generateToken(Collections.singletonMap("authorities", authorities),
                             userId, refreshConfig.getRefreshTokenExpirationMs());
    }
}
