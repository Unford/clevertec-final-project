package ru.clevertec.banking.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.clevertec.banking.customer.entity.UserCredentials;

@Repository
public interface RefreshTokenRepository extends JpaRepository<UserCredentials, Long> {

    @Modifying
    @Query("UPDATE UserCredentials u SET u.refreshToken = :refreshToken WHERE u.id = :userId")
    void updateRefreshToken(@Param("userId") Long userId, @Param("refreshToken") String refreshToken);

    boolean existsByIdAndRefreshToken(Long userId, String providedRefreshToken);


}
