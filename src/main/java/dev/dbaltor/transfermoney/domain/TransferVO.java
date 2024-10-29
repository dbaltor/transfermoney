package dev.dbaltor.transfermoney.domain;

import org.javamoney.moneta.Money;

import lombok.NonNull;

import java.math.BigDecimal;

public record TransferVO(
        @NonNull Long fromAccountNo,
        @NonNull Long toAccountNo,
        @NonNull Money amount,
        @NonNull String currency) {

    public static TransferVO of(Long sourceAccountNo, Long targetAccountNo, BigDecimal amount, String currency) {
        return new TransferVO(sourceAccountNo, targetAccountNo, Money.of(amount, currency), currency);
    }
}