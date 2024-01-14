package ru.clevertec.banking.customer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_credentials")
public class UserCredentials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // TODO make uuid if needed
    private Long id; // TODO uuid in migrations and UserCredentialsRepository

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCredentials that = (UserCredentials) o;
        return Objects.equals(id, that.id) && Objects.equals(email, that.email) &&
               Objects.equals(password, that.password) && role == that.role &&
               Objects.equals(refreshToken, that.refreshToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, role, refreshToken);
    }
}
