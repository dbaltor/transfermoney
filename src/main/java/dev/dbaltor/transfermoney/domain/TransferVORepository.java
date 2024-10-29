package dev.dbaltor.transfermoney.domain;

import reactor.core.publisher.Mono;

public interface TransferVORepository {
    Mono<TransferVO> save(AccountEntity source, AccountEntity target, TransferVO transfer);
}
