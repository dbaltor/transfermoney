package dev.dbaltor.transfermoney.infrastructure;

import dev.dbaltor.transfermoney.domain.AccountEntity;
import dev.dbaltor.transfermoney.domain.AccountEntityRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class AccountEntityRepositoryImpl implements AccountEntityRepository {

    private final AccountRepository accountRepository;

    @Override
    public Mono<AccountEntity> findById(@NonNull Long id) {
        return accountRepository.findById(id)
                .map(Account::account);
    }

    @Override
    public Mono<AccountEntity> save(AccountEntity account) {
        return accountRepository.save(Account.of(account))
                .map(Account::account);
    }
}
