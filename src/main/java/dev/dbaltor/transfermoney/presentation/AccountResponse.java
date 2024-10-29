package dev.dbaltor.transfermoney.presentation;

import dev.dbaltor.transfermoney.domain.AccountVO;

import java.time.format.DateTimeFormatter;

public record AccountResponse(String number, String balance, String currency, String createdAt) {

    public static AccountResponse of(AccountVO accountVO){
        return new AccountResponse(
                String.valueOf(accountVO.number()),
                accountVO.balance().toString(),
                accountVO.currency(),
                accountVO.createdAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
