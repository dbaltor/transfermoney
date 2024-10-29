package dev.dbaltor.transfermoney.infrastructure;

import dev.dbaltor.transfermoney.fixture.RxTxTestConfiguration;
import dev.dbaltor.transfermoney.fixture.TxStepVerifier;
import dev.dbaltor.transfermoney.domain.AccountEntity;
import dev.dbaltor.transfermoney.domain.TransferVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@DataR2dbcTest
@Import(RxTxTestConfiguration.class)
class TransferRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransferRepository transferRepository;

    @Test
    public void findById() {
        // Given
        accountRepository.save(accountWith("1000.00", "USD"))
                .zipWith(accountRepository.save(accountWith("5000.00", "USD")),
                        (acc1, acc2) -> transferRepository.save(transfer(acc1, acc2, "1000.00", "USD"))
                                .then(transferRepository.save(transfer(acc2, acc1, "5000.00", "EUR")))
                                // When
                                .then(transferRepository.findById(1L))
                                // Then
                                .as(TxStepVerifier::withRollback)
                                .expectNextCount(1)
                                .verifyComplete());
    }

    @Test
    public void findByAmountRange() {
        // Given
        accountRepository.save(accountWith("1000.00", "USD"))
                .zipWith(accountRepository.save(accountWith("5000.00", "USD")),
                        (acc1, acc2) -> transferRepository.save(transfer(acc1, acc2, "1000.00", "USD"))
                                .then(transferRepository.save(transfer(acc2, acc1, "5000.00", "EUR")))
                                // When
                                .thenMany(transferRepository.findByAmountRange(new BigDecimal("1000.01"), new BigDecimal("15000.00")))
                                // Then
                                .as(TxStepVerifier::withRollback)
                                .expectNextCount(1)
                                .verifyComplete());
    }

    private Account accountWith(String balance, String currency) {
        return Account.of(
                AccountEntity.of(
                        new BigDecimal(balance),
                        currency,
                        LocalDateTime.now()));
    }

    private Transfer transfer(Account from, Account to, String amount, String currency) {
        return Transfer.of(
                        TransferVO.of(
                                from.getNumber(),
                                to.getNumber(),
                                new BigDecimal(amount),
                                currency))
                .setTime(LocalDateTime.now())
                .setSourceCurrency(from.getCurrency())
                .setTargetCurrency(to.getCurrency());
    }
}