package dev.dbaltor.transfermoney.domain;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.val;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static dev.dbaltor.transfermoney.domain.TransferException.ErrorType.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

public class TransferServiceTests {
    private static final LocalDateTime date = LocalDateTime.of(2021, 5, 26, 15, 0);
    private static final long sourceAccId = 8888L;
    private static final long targetAccId = 9999L;
    private static final String usd = "USD";
    private static final String eur = "EUR";
    private static final BigDecimal thousand = new BigDecimal(1000);
    private static final TransferVO transfer1000USD = TransferVO.of(
            sourceAccId,
            targetAccId,
            thousand,
            usd);

    private AccountEntity sourceAccount;
    private AccountEntity targetAccount;
    private TransferService transferService;

    private AccountEntityRepository mockAccountEntityRepository;
    private TransferVORepository mockTransferVORepository;
    private CurrencyConversionService mockCurrencyConversionService;

    @BeforeEach
    void setup() {
        mockCurrencyConversionService = mock(CurrencyConversionService.class);
        sourceAccount = AccountEntity.of(
                        thousand,
                        usd,
                        date
                )
                .setNumber(sourceAccId);
        targetAccount = AccountEntity.of(
                        thousand,
                        usd,
                        date
                )
                .setNumber(targetAccId);
        mockAccountEntityRepository = mock(AccountEntityRepository.class);
        mockTransferVORepository = mock(TransferVORepository.class);
        transferService = new TransferService(mockAccountEntityRepository, mockTransferVORepository, mockCurrencyConversionService);
    }

    @Test
    void shouldTransferMoneyOfSameCurrency() {
        // Given
        given(mockAccountEntityRepository.findById(sourceAccId))
                .willReturn(Mono.just(sourceAccount));
        given(mockAccountEntityRepository.findById(targetAccId))
                .willReturn(Mono.just(targetAccount));
        given(mockAccountEntityRepository.save(sourceAccount))
                .willReturn(Mono.just(sourceAccount));
        given(mockAccountEntityRepository.save(targetAccount))
                .willReturn(Mono.just(targetAccount));
        given(mockTransferVORepository.save(any(), any(), any()))
                .willReturn(Mono.just(transfer1000USD));
        // When
        transferService.requestTransfer(transfer1000USD)

                // Then
                .as(StepVerifier::create)
                .expectNextMatches(transfer -> transfer.amount().getNumberStripped().compareTo(thousand) == 0)
                .verifyComplete();
        then(mockAccountEntityRepository)
                .should()
                .save(sourceAccount);
        then(mockAccountEntityRepository)
                .should()
                .save(targetAccount);
    }

    @Test
    void shouldTransferMoneyOfDifferentCurrencies() {
        // Given
        given(mockAccountEntityRepository.findById(sourceAccId))
                .willReturn(Mono.just(sourceAccount));
        given(mockAccountEntityRepository.findById(targetAccId))
                .willReturn(Mono.just(targetAccount));
        given(mockAccountEntityRepository.save(sourceAccount))
                .willReturn(Mono.just(sourceAccount));
        given(mockAccountEntityRepository.save(targetAccount))
                .willReturn(Mono.just(targetAccount));
        given(mockCurrencyConversionService.convertTo(eq(Money.of(new BigDecimal("500"), eur)), eq(usd)))
                .willReturn(Money.of(new BigDecimal("800"), usd));
        val transfer500EUR = TransferVO.of(
                sourceAccId,
                targetAccId,
                new BigDecimal("500"),
                eur);
        given(mockTransferVORepository.save(any(), any(), any()))
                .willReturn(Mono.just(transfer500EUR));

        // When
        transferService.requestTransfer(transfer500EUR)

                // Then
                .as(StepVerifier::create)
                .expectNextMatches(transfer -> transfer.amount().getNumberStripped().compareTo(new BigDecimal("500")) == 0)
                .verifyComplete();
        then(mockAccountEntityRepository)
                .should()
                .save(sourceAccount);
        then(mockAccountEntityRepository)
                .should()
                .save(targetAccount);
    }

    @Test
    void shouldNotTransferMoneyWhenSourceAccountNotFound() {
        // Given
        given(mockAccountEntityRepository.findById(sourceAccId))
                .willReturn(Mono.empty());
        given(mockAccountEntityRepository.findById(targetAccId))
                .willReturn(Mono.just(targetAccount));
        // When
        transferService.requestTransfer(transfer1000USD)

                // Then
                .as(StepVerifier::create)
                .expectErrorMatches(e ->
                        (e instanceof TransferException transferException) &&
                                transferException.getError().equals(FROM_ACCOUNT_NOT_FOUND))
                .verify();
        then(mockAccountEntityRepository)
                .should(never())
                .save(sourceAccount);
    }

    @Test
    void shouldNotTransferMoneyWhenTargetAccountNotFound() {
        // Given
        given(mockAccountEntityRepository.findById(sourceAccId))
                .willReturn(Mono.just(sourceAccount));
        given(mockAccountEntityRepository.findById(targetAccId))
                .willReturn(Mono.empty());
        // When
        transferService.requestTransfer(transfer1000USD)

                // Then
                .as(StepVerifier::create)
                .expectErrorMatches(e ->
                        (e instanceof TransferException transferException) &&
                                transferException.getError().equals(TO_ACCOUNT_NOT_FOUND))
                .verify();
        then(mockAccountEntityRepository)
                .should(never())
                .save(sourceAccount);
    }

    @Test
    void shouldNotTransferMoneyWhenAccountsAreTheSame() {
        // Given
        given(mockAccountEntityRepository.findById(sourceAccId))
                .willReturn(Mono.just(sourceAccount));
        val transferBtwSameAcc = TransferVO.of(
                sourceAccId,
                sourceAccId,
                thousand,
                usd);
        // When
        transferService.requestTransfer(transferBtwSameAcc)

                // Then
                .as(StepVerifier::create)
                .expectErrorMatches(e ->
                        (e instanceof TransferException transferException) &&
                                transferException.getError().equals(FROM_AND_TO_ACCOUNTS_ARE_THE_SAME))
                .verify();
        then(mockAccountEntityRepository)
                .should(never())
                .save(sourceAccount);
    }

    @Test
    void shouldNotTransferMoneyWhenBalanceIsInsufficient() {
        // Given
        given(mockAccountEntityRepository.findById(sourceAccId))
                .willReturn(Mono.just(sourceAccount));
        given(mockAccountEntityRepository.findById(targetAccId))
                .willReturn(Mono.just(targetAccount));
        val insufficientTransferBalance = TransferVO.of(
                sourceAccId,
                targetAccId,
                new BigDecimal("2000"),
                usd);
        // When
        transferService.requestTransfer(insufficientTransferBalance)

                // Then
                .as(StepVerifier::create)
                .expectErrorMatches(e ->
                        (e instanceof TransferException transferException) &&
                                transferException.getError().equals(INSUFFICIENT_BALANCE))
                .verify();
        then(mockAccountEntityRepository)
                .should(never())
                .save(sourceAccount);
    }
}