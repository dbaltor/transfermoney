package dev.dbaltor.transfermoney.infrastructure;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import dev.dbaltor.transfermoney.domain.AccountEntity;
import lombok.*;
import org.springframework.data.annotation.Id;
import lombok.experimental.Accessors;

import static java.math.RoundingMode.*;
import static lombok.AccessLevel.*;

@Data
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of", access = PRIVATE)
public class Account {
    private @Id Long Number;
    private @NonNull BigDecimal balance;
    private @NonNull String currency;
    private @NonNull LocalDateTime createdAt;

    public AccountEntity account() {
        return AccountEntity.of(
                        balance,
                        currency,
                        createdAt)
                .setNumber(Number);
    }

    public static Account of(AccountEntity acc) {
        return Account.of(
                acc.getBalance().getNumberStripped().setScale(2, HALF_DOWN),
                acc.getCurrency(),
                acc.getCreatedAt()
        ).setNumber(acc.getNumber());
    }
}
