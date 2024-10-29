package dev.dbaltor.transfermoney.domain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import static dev.dbaltor.transfermoney.domain.TransferException.ErrorType.*;

@Service
@RequiredArgsConstructor
public class TransferService {

    private @NonNull AccountEntityRepository accountEntityRepository;
    private @NonNull TransferVORepository transferVORepository;
    private @NonNull CurrencyConversionService currencyConversionService;

    @Transactional
    public Mono<TransferVO> requestTransfer(TransferVO transfer) {

        return accountEntityRepository.findById(transfer.fromAccountNo())
                .switchIfEmpty(Mono.error(TransferException.of(FROM_ACCOUNT_NOT_FOUND)))
                .flatMap(sourceAcc -> checkIfDifferentAccounts(sourceAcc, transfer))
                .map(sourceAcc -> sourceAcc.calculateBalance(transfer, currencyConversionService))
                .zipWith(
                        accountEntityRepository.findById(transfer.toAccountNo())
                                .switchIfEmpty(Mono.error(TransferException.of(TO_ACCOUNT_NOT_FOUND)))
                                .map(targetAcc -> targetAcc.calculateBalance(transfer, currencyConversionService)),
                        (fromAcc, toAcc) -> {
                            validateTransfer(fromAcc, toAcc);
                            // Transfer the money
                            saveAccount(fromAcc)
                                    .then(saveAccount(toAcc))
                                    .then(transferVORepository.save(fromAcc, toAcc, transfer))
                                    .subscribe();
                            return transfer;
                        });
    }

    private void validateTransfer(AccountEntity sourceAcc, AccountEntity targetAcc) {
        if (sourceAcc.getBalance().signum() == -1) {
            throw Exceptions.propagate(TransferException.of(INSUFFICIENT_BALANCE));
        }
    }

    private Mono<AccountEntity> checkIfDifferentAccounts(AccountEntity sourceAcc, TransferVO transfer) {
        if (transfer.fromAccountNo().equals(transfer.toAccountNo())) {
            return Mono.error(TransferException.of(FROM_AND_TO_ACCOUNTS_ARE_THE_SAME));
        }
        return Mono.just(sourceAcc);
    }


    private Mono<AccountEntity> saveAccount(AccountEntity account) {
        return accountEntityRepository
                .save(account)
                .log();
    }

}
