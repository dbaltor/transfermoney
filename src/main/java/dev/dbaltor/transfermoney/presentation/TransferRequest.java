package dev.dbaltor.transfermoney.presentation;

import dev.dbaltor.transfermoney.domain.TransferVO;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.NonNull;
import lombok.Data;

@Data
public class TransferRequest {
    @NotBlank(message = "Account number to withdraw is mandatory")
    @Pattern(regexp = "^[0-9]{1,10}$", message = "Account number must be a number with the format 1234567890")
    private  @NonNull String sourceAccountNo;
    @NotBlank(message = "Account number to deposit is mandatory")
    @Pattern(regexp = "^[0-9]{1,10}$", message = "Account number must be a number with the format 1234567890")
    private @NonNull String targetAccountNo;
    @NotBlank(message = "Amount is mandatory")
    @Pattern(regexp = "^[0-9]{1,10}\\.?[0-9]{1,2}$", message = "Amount must be a number with the format 1234567890.12")
    private @NonNull String amount;
    @NotBlank(message = "Currency is mandatory")
    @Pattern(regexp = "^[a-z,A-Z]{3}$", message = "Currency must be a string with three letters")
    private @NonNull String currency;

    public TransferVO transfer() {
        return TransferVO.of(
                Long.valueOf(sourceAccountNo),
                Long.valueOf(targetAccountNo),
                new BigDecimal(amount),
                currency);
    }

    public static TransferRequest of(TransferVO transfer) {
        return new TransferRequest(
                transfer.fromAccountNo().toString(),
                transfer.toAccountNo().toString(),
                transfer.amount().toString(),
                transfer.currency()
        );
    }
}
