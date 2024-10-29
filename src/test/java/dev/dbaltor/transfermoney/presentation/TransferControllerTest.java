package dev.dbaltor.transfermoney.presentation;

import dev.dbaltor.transfermoney.domain.TransferException;
import dev.dbaltor.transfermoney.domain.TransferService;
import dev.dbaltor.transfermoney.domain.TransferVO;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static dev.dbaltor.transfermoney.domain.TransferException.ErrorType.INSUFFICIENT_BALANCE;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;


@WebFluxTest(TransferController.class)
class TransferControllerTest {

    public static final String BASE_URL = "/api";
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private TransferService transferService;

    @Test
    @DisplayName("Should return 200 when money is transferred")
    public void shouldReturn200WhenMoneyIsTransferred() throws Exception {
        // Given
        val transferRequest = new TransferRequest("1", "2", "1000.00", "USD");
        val transferVO = TransferVO.of(
                Long.valueOf(transferRequest.getSourceAccountNo()),
                Long.valueOf(transferRequest.getTargetAccountNo()),
                new BigDecimal(transferRequest.getAmount()),
                transferRequest.getCurrency());
        given(transferService.requestTransfer(eq(transferVO)))
                .willReturn(Mono.just(transferVO));
        // When
        webTestClient.post()
                .uri(BASE_URL + "/transfer")
                .contentType(APPLICATION_JSON)
                .bodyValue(transferRequest)
                .exchange()
                // Then
                .expectStatus().isNoContent();
    }


    @Test
    @DisplayName("Should return 409 when when balance is insufficient")
    public void shouldReturn409WhenBalanceIsInsufficient() throws Exception {
        // Given
        val transferRequest = new TransferRequest("1", "2", "1000.00", "USD");
        val transferVO = TransferVO.of(
                Long.valueOf(transferRequest.getSourceAccountNo()),
                Long.valueOf(transferRequest.getTargetAccountNo()),
                new BigDecimal(transferRequest.getAmount()),
                transferRequest.getCurrency());
        given(transferService.requestTransfer(eq(transferVO)))
                .willReturn(Mono.error(TransferException.of(INSUFFICIENT_BALANCE)));
        // When
        webTestClient.post()
                .uri(BASE_URL + "/transfer")
                .contentType(APPLICATION_JSON)
                .bodyValue(transferRequest)
                .exchange()
                // Then
                .expectStatus().isEqualTo(409)
                .expectHeader().contentType(APPLICATION_JSON_VALUE)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Insufficient funds");
    }
}