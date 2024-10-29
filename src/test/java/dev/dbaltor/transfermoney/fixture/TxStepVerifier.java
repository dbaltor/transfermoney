package dev.dbaltor.transfermoney.fixture;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public interface TxStepVerifier extends StepVerifier {

    static <T> FirstStep<T> withRollback(final Mono<T> publisher) {
        return StepVerifier.create(publisher.as(RxTransaction::withRollback));
    }

    static <T> FirstStep<T> withRollback(final Flux<T> publisher) {
        return StepVerifier.create(publisher.as(RxTransaction::withRollback));
    }
}
