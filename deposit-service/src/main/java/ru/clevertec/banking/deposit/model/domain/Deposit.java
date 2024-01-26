package ru.clevertec.banking.deposit.model.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import ru.clevertec.banking.deposit.model.CustomerType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "deposits")
@SQLRestriction("deleted = false")
@SQLDelete(sql = "UPDATE {h-schema}deposits SET deleted = true WHERE id=?")
public class Deposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerType customerType;

    @Embedded
    private AccountInfo accInfo;

    @Embedded
    private DepositInfo depInfo;

    private Boolean deleted;

    @PrePersist
    public void onPrePersist() {
        this.deleted = false;
        if (Objects.isNull(accInfo.getAccOpenDate())) {
            accInfo.setAccOpenDate(LocalDate.now());
        }
        if (Objects.isNull(accInfo.getCurrAmount())){
            accInfo.setCurrAmount(BigDecimal.ZERO);
        }
        if (Objects.isNull(depInfo.getExpDate())){
            LocalDate openDate = accInfo.getAccOpenDate();
            depInfo.setExpDate(openDate.plus(depInfo.getTermVal(),
                    depInfo.getTermScale().getTemporalUnit()));
        }
    }

}
