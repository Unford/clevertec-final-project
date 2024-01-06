package ru.clevertec.banking.service;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.clevertec.banking.config.TokenConfig;
import ru.clevertec.banking.dto.response.SmallUserCredentialsReponse;
import ru.clevertec.banking.entity.UserCredentials;

import java.util.Date;
import java.util.HashMap;

@Service
public class JwtTokenService extends AbstractTokenService {
    private final TokenConfig tokenConfig;
//    private final UserService userService;

    @Autowired
    public JwtTokenService(TokenConfig tokenConfig, UserService userService) {
        super(tokenConfig);
        this.tokenConfig = tokenConfig;
//        this.userService = userService;
    }

    public String generateToken(Long userId) {
        return generateToken(new HashMap<>(), userId, tokenConfig.getTokenExpirationMs());
    }


    // Если мы решим сюда не ходить проверять, коменты удалить
//    public void isTokenValid(String token) {
//        SmallUserCredentialsReponse user = userService.getSmallUserFromContext();
//        final Long id = extractId(token);
//        if (!isTokenExpired(token) && id.equals(user.getId())) {
//            return;
//        }
//        throw new IllegalArgumentException("Invalid token");
//    }
//
//    private boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
}
