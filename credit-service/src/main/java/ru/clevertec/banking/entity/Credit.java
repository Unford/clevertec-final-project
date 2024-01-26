package ru.clevertec.banking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "credit")
@SQLDelete(sql = "UPDATE {h-schema}credit SET deleted = true WHERE contract_number=?")
@SQLRestriction(value = "deleted = false")
public class Credit {
    private UUID customerId;
    @Id
    private String contractNumber;
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private LocalDate contractStartDate;
    private BigDecimal totalDebt;
    private BigDecimal currentDebt;
    private String currency;
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private LocalDate repaymentDate;
    private BigDecimal rate;
    private String iban;
    private boolean possibleRepayment;
    private boolean closed;
    private String customerType;
    private boolean deleted = Boolean.FALSE;
}
