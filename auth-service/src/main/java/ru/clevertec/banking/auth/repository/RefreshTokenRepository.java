package ru.clevertec.banking.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.clevertec.banking.auth.entity.UserCredentials;

import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<UserCredentials, UUID> {

    @Modifying
    @Query("UPDATE UserCredentials u SET u.refreshToken = :refreshToken WHERE u.id = :userId")
    void updateRefreshToken(@Param("userId") UUID userId, @Param("refreshToken") String refreshToken);

    boolean existsByIdAndRefreshToken(UUID userId, String providedRefreshToken);
}