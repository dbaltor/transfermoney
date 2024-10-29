package dev.dbaltor.transfermoney.fixture;

import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Rolling back transactions in R2DBC integration tests
// https://github.com/spring-projects/spring-framework/issues/24226

@Component
public class RxTransaction {

    private static TransactionalOperator rxtx;

    public RxTransaction(TransactionalOperator rxtx) {
        RxTransaction.rxtx = rxtx;
    }

    public static <T> Mono<T> withRollback(Mono<T> publisher) {
        return rxtx.execute(tx -> {
                    tx.setRollbackOnly();
                    return publisher;
                })
                .next();
    }

    public static <T> Flux<T> withRollback(Flux<T> publisher) {
        return rxtx.execute(tx -> {
            tx.setRollbackOnly();
            return publisher;
        });
    }
}
