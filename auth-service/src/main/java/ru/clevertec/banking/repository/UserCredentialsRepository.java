package ru.clevertec.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import ru.clevertec.banking.entity.SecurityUserDetails;
import ru.clevertec.banking.entity.UserCredentials;

import java.util.Optional;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long> {

    Optional<UserCredentials> findByEmail(String email);

    // Опять же если мы не идем сюда проверять токен, удалить
//    default UserCredentials getUserFromContext() {
//        Authentication authentication = SecurityContextHolder.getContext()
//                                                             .getAuthentication();
//        SecurityUserDetails user = (SecurityUserDetails) authentication.getPrincipal();
//        return user.getUser();
//    }
}
