package dev.dbaltor.transfermoney.infrastructure;

import java.math.BigDecimal;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

public interface TransferRepository extends ReactiveCrudRepository<Transfer, Long>{
    @Query("SELECT * FROM transfer WHERE amount >= :min and amount <= :max")
    Flux<Transfer> findByAmountRange(BigDecimal min, BigDecimal max);
}
