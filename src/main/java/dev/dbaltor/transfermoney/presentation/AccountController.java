package dev.dbaltor.transfermoney.presentation;

import dev.dbaltor.transfermoney.application.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

import static dev.dbaltor.transfermoney.presentation.AccountRequest.ACCOUNT_NUMBER_FORMAT;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping(value = "api")
@RequiredArgsConstructor
public class AccountController {
    private static final String ACCOUNT_NUMBER_FORMAT_ERROR = "Account number must be a number in the format 1234567890";

    private @NonNull AccountService accountService;

    @PostMapping(value = "account", consumes = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> createAccount(@Valid @RequestBody AccountRequest accountRequest) {
        return accountService.createAccount(accountRequest.account())
                .map(account -> ResponseEntity.created(URI.create("/api/account/" + account.number())).build());
    }


    @GetMapping("account/{accountNo}")
    public Mono<AccountResponse> getAccount(@PathVariable("accountNo") @Pattern(regexp = ACCOUNT_NUMBER_FORMAT, message = ACCOUNT_NUMBER_FORMAT_ERROR) String accountNo) {
        return accountService.getAccount(Long.valueOf(accountNo))
                .map(AccountResponse::of);
    }
}

