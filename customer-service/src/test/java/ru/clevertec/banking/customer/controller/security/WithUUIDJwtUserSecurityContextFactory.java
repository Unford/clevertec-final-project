package ru.clevertec.banking.customer.controller.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class WithUUIDJwtUserSecurityContextFactory implements WithSecurityContextFactory<WithMockUUIDJwtUser> {

    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();

    @Override
    public SecurityContext createSecurityContext(WithMockUUIDJwtUser withUser) {
        Assert.notNull(withUser.value(), () -> withUser + " cannot have null uuid on value");
        UUID id = withUser.value().isEmpty() ? UUID.randomUUID() : UUID.fromString(withUser.value());
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (String role : withUser.roles()) {
            Assert.isTrue(!role.startsWith("ROLE_"), () -> "roles cannot start with ROLE_ Got " + role);
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(id,
                null, grantedAuthorities);
        authentication.setDetails(withUser.details());
        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }

    @Autowired(required = false)
    void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        this.securityContextHolderStrategy = securityContextHolderStrategy;
    }


}
