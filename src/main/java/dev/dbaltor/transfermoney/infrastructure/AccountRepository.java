package dev.dbaltor.transfermoney.infrastructure;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AccountRepository extends ReactiveCrudRepository<Account, Long>{
}
