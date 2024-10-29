package dev.dbaltor.transfermoney.fixture;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration
public class RxTxTestConfiguration {

    @Bean
    public RxTransaction rxTransaction(TransactionalOperator transactionalOperator) {
        return new RxTransaction(transactionalOperator);
    }
}


