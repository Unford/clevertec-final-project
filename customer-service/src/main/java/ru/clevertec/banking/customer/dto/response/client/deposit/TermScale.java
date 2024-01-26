package ru.clevertec.banking.customer.dto.response.client.deposit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

@Getter
@RequiredArgsConstructor
public enum TermScale {
    M(ChronoUnit.MONTHS),
    D(ChronoUnit.DAYS);

    private final TemporalUnit temporalUnit;
}
