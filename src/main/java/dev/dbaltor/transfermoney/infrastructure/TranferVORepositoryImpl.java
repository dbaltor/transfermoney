package dev.dbaltor.transfermoney.infrastructure;

import dev.dbaltor.transfermoney.domain.AccountEntity;
import dev.dbaltor.transfermoney.domain.TransferVO;
import dev.dbaltor.transfermoney.domain.TransferVORepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class TranferVORepositoryImpl implements TransferVORepository {

    private final TransferRepository transferRepository;

    @Override
    public Mono<TransferVO> save(AccountEntity source, AccountEntity target, TransferVO transfer) {
        val transferAuditDB = Transfer.of(transfer);
        transferAuditDB.setSourceCurrency(source.getCurrency());
        transferAuditDB.setTargetCurrency(target.getCurrency());
        transferAuditDB.setTime(LocalDateTime.now());
        return transferRepository
                .save(transferAuditDB)
                .log()
                .map(Transfer::transfer);
    }
}
