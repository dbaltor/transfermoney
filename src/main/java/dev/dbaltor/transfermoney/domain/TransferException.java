package dev.dbaltor.transfermoney.domain;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class TransferException extends Exception{

    private @NonNull ErrorType error;

    public enum ErrorType {
        ACCOUNT_NOT_FOUND,
        FROM_ACCOUNT_NOT_FOUND,
        TO_ACCOUNT_NOT_FOUND,
        FROM_AND_TO_ACCOUNTS_ARE_THE_SAME,
        INSUFFICIENT_BALANCE
    }
}


