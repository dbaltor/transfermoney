package dev.dbaltor.transfermoney.infrastructure;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;
import org.springframework.data.annotation.Id;

import dev.dbaltor.transfermoney.domain.TransferVO;
import lombok.experimental.Accessors;

import static java.math.RoundingMode.HALF_DOWN;
import static lombok.AccessLevel.PRIVATE;

@Data
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of", access = PRIVATE)
public class Transfer {
    private @Id Long id;
    private @NonNull Long sourceAccountNo;
    private @NonNull Long targetAccountNo;
    private @NonNull BigDecimal amount;
    private @NonNull String currency;
    // Extra info
    private String sourceCurrency;
    private String targetCurrency;
    private LocalDateTime time;

    public TransferVO transfer() {
        return TransferVO.of(
                sourceAccountNo,
                targetAccountNo,
                amount,
                currency);
    }

    public static Transfer of(TransferVO transfer) {
        return Transfer.of(
                transfer.fromAccountNo(),
                transfer.toAccountNo(),
                transfer.amount().getNumberStripped().setScale(2, HALF_DOWN),
                transfer.currency()
        );
    }
}
