package dev.dbaltor.transfermoney.domain;

import org.javamoney.moneta.Money;

public interface CurrencyConversionService {

    public Money convertTo(Money amount, String currency);
}
