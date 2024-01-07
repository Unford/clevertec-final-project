package ru.clevertec.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.clevertec.banking.config.TokenConfig;
import ru.clevertec.banking.dto.response.SmallUserCredentialsReponse;
import ru.clevertec.banking.entity.Role;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenService extends AbstractTokenService {
    private final TokenConfig jwtConfig;
//    private final UserService userService;

    @Autowired
    public JwtTokenService(TokenConfig jwtConfig, UserService userService) {
        super(jwtConfig);
        this.jwtConfig = jwtConfig;
//        this.userService = userService;
    }

    public String generateToken(Long userId, List<Role> authorities) {
        return generateToken(Collections.singletonMap("authorities", authorities),
                             userId, jwtConfig.getTokenExpirationMs());
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
