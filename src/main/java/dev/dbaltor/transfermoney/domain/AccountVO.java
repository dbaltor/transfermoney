package dev.dbaltor.transfermoney.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.math.RoundingMode.HALF_DOWN;

public record AccountVO(Long number, BigDecimal balance, String currency, LocalDateTime createdAt) {
    public static AccountVO of(AccountEntity account) {
        return new AccountVO(
                account.getNumber(),
                account.getBalance().getNumberStripped().setScale(2, HALF_DOWN),
                account.getCurrency(),
                account.getCreatedAt());
    }
}
