package dev.dbaltor.transfermoney.application;

import dev.dbaltor.transfermoney.domain.AccountEntity;
import dev.dbaltor.transfermoney.domain.AccountEntityRepository;
import dev.dbaltor.transfermoney.domain.AccountVO;
import dev.dbaltor.transfermoney.domain.TransferException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static dev.dbaltor.transfermoney.domain.TransferException.ErrorType.ACCOUNT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AccountService {

    private @NonNull AccountEntityRepository accountEntityRepository;

    public Mono<AccountVO> createAccount(AccountVO accountVO) {
        val newAccount = AccountEntity.of(accountVO.balance(), accountVO.currency(), LocalDateTime.now());
        return accountEntityRepository.save(newAccount)
                .map(AccountVO::of);
    }

    public Mono<AccountVO> getAccount(Long accountNo) {
        return accountEntityRepository.findById(accountNo)
                .switchIfEmpty(Mono.error(TransferException.of(ACCOUNT_NOT_FOUND)))
                .map(AccountVO::of);
    }
}
