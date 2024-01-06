package ru.clevertec.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.banking.config.TokenConfig;
import ru.clevertec.banking.repository.RefreshTokenRepository;

import java.util.Date;
import java.util.HashMap;

@Service
@Transactional(readOnly = true)
public class RefreshTokenService extends AbstractTokenService{
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenConfig tokenConfig;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, TokenConfig tokenConfig) {
        super(tokenConfig);
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenConfig = tokenConfig;
    }

    @Transactional
    public void updateRefreshToken(String refreshToken) {
        final Long id = extractId(refreshToken);
        refreshTokenRepository.updateRefreshToken(id, refreshToken);
    }

    public boolean isRefreshTokenValid(String providedRefreshToken) {
        final Long id = extractId(providedRefreshToken);
        return refreshTokenRepository.isRefreshTokenValid(id, providedRefreshToken);
    }

    public boolean isRefreshTokenNotExpired(String refreshToken) {
        return extractExpiration(refreshToken).before(new Date());
    }

    public String generateRefreshToken(Long userId) {
        return generateToken(new HashMap<>(), userId, tokenConfig.getRefreshTokenExpirationMs());
    }
}
