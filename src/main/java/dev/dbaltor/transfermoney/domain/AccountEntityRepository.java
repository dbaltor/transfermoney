package dev.dbaltor.transfermoney.domain;

import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface AccountEntityRepository {
    Mono<AccountEntity> findById(@NonNull Long sourceAccountId);

    Mono<AccountEntity> save(AccountEntity source);
}
