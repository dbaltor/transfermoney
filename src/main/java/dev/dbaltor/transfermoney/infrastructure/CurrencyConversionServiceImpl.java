package dev.dbaltor.transfermoney.infrastructure;

import dev.dbaltor.transfermoney.domain.CurrencyConversionService;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Component;

import javax.money.convert.MonetaryConversions;

@Component
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    @Override
    public Money convertTo(Money amount, String currency) {
        return amount.with(MonetaryConversions.getConversion(currency));
    }
}
