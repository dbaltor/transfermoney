package dev.dbaltor.transfermoney.presentation;

import dev.dbaltor.transfermoney.domain.AccountVO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
public class AccountRequest {
        @NotBlank(message = "Initial balance is mandatory")
        @Pattern(regexp = AMOUNT_FORMAT, message = AMOUNT_FORMAT_ERROR)
        private @NonNull String balance;
        @NotBlank(message = "Currency is mandatory")
        @Pattern(regexp = CURRENCY_FORMAT, message = CURRENCY_FORMAT_ERROR)
        private @NonNull String currency;

    public static final String ACCOUNT_NUMBER_FORMAT = "^[0-9]{1,10}$";
    public static final String ACCOUNT_NUMBER_FORMAT_ERROR = "Account number must be a number with the format 1234567890";
    public static final String AMOUNT_FORMAT = "^[0-9]{1,10}\\.?[0-9]{1,2}$";
    public static final String AMOUNT_FORMAT_ERROR = "Amount must be a number with format 1234567890.12";
    public static final String CURRENCY_FORMAT = "^[a-z,A-Z]{3}$";
    public static final String CURRENCY_FORMAT_ERROR = "Currency must be a string with three letters";

    public AccountVO account() {
        return new AccountVO(null, new BigDecimal(balance), currency, null);
    }
}
