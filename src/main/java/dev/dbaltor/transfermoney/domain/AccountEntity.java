package dev.dbaltor.transfermoney.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;
import org.javamoney.moneta.Money;

import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

import javax.money.Monetary;

@Component
@Getter @Accessors(chain = true)
@NoArgsConstructor @RequiredArgsConstructor(staticName = "of")
public class AccountEntity {
    private @Setter Long number;
    private @NonNull Money balance;
    private @NonNull String currency;
    private @NonNull LocalDateTime createdAt;

    private CurrencyConversionService currencyConversionService;

    public static AccountEntity of(BigDecimal balance, String currency, LocalDateTime createdAt) {
        return new AccountEntity(Money.of(balance, currency), currency, createdAt);
    }

    public AccountEntity calculateBalance(TransferVO transfer, CurrencyConversionService currencyConversionService) {
        if (transfer.fromAccountNo().equals(getNumber()))
            balance = balance.subtract(getTransferAmount(transfer, currencyConversionService));
        else if (transfer.toAccountNo().equals(getNumber()))
            balance = balance.add(getTransferAmount(transfer, currencyConversionService));
        return this;
    }

    private Money getTransferAmount(TransferVO transfer, CurrencyConversionService currencyConversionService) {
        if (transfer.currency().equals(Monetary.getCurrency(currency).getCurrencyCode()))
            return transfer.amount();
        return currencyConversionService.convertTo(transfer.amount(), currency);
    }
}
