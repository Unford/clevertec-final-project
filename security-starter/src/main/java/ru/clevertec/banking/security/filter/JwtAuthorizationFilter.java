package ru.clevertec.banking.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.clevertec.banking.security.model.AuthTokenProvider;
import ru.clevertec.banking.security.model.Role;
import ru.clevertec.banking.security.service.JwtTokenService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private static final String BEARER = "Bearer ";

    private final JwtTokenService jwtService;
    private final AuthTokenProvider authTokenProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Optional<String> authHeader = authTokenProvider.getAuthorizationHeader();
        Optional<UUID> optionalSub = Optional.empty();
        List<SimpleGrantedAuthority> authorities = List.of();

        if (authHeader.isPresent() && authHeader.get().startsWith(BEARER)) {
            String token = authTokenProvider.getToken().orElse("");
            optionalSub = jwtService.extractSub(token).map(UUID::fromString);
            authorities = jwtService.extractAuthorities(token)
                    .stream()
                    .map(Role::valueOf)
                    .map(Role::toAuthority)
                    .toList();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (optionalSub.isPresent() && Objects.isNull(authentication)) {
            setAuthenticationWithToken(optionalSub.get(), authorities, authHeader.get());
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthenticationWithToken(UUID sub, List<SimpleGrantedAuthority> authorities, String authHeader) {
        //todo refactor UUID principal to implement UserDetails or Principal
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                sub,
                null,
                authorities
        );
        authenticationToken.setDetails(authHeader);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
