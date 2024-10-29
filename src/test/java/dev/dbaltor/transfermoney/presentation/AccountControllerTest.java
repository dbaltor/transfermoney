package dev.dbaltor.transfermoney.presentation;

import dev.dbaltor.transfermoney.application.AccountService;
import dev.dbaltor.transfermoney.domain.AccountVO;
import dev.dbaltor.transfermoney.domain.TransferException;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static dev.dbaltor.transfermoney.domain.TransferException.ErrorType.ACCOUNT_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;


@WebFluxTest(AccountController.class)
class AccountControllerTest {

    public static final String BASE_URL = "/api";
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private AccountService accountService;

    @Test
    @DisplayName("Should return 201 when account is created")
    public void shouldReturn201WhenAccountIsCreated() throws Exception {
        // Given
        val accountRequest = new AccountRequest("1000.00", "USD");
        val accountVO = new AccountVO(
                1L,
                new BigDecimal(accountRequest.getBalance()),
                accountRequest.getCurrency(),
                LocalDateTime.now());
        given(accountService.createAccount(any()))
                .willReturn(Mono.just(accountVO));
        // When
        webTestClient.post()
                .uri(BASE_URL + "/account")
                .contentType(APPLICATION_JSON)
                .bodyValue(accountRequest)
                .exchange()
                // Then
                .expectStatus().isCreated();
    }


    @Test
    @DisplayName("Should return 200 when account is found")
    public void shouldReturn200WhenAccountIsFound() throws Exception {
        // Given
        val accountVO = new AccountVO(
                1L,
                new BigDecimal("1000.00"),
                "USD",
                LocalDateTime.now());
        given(accountService.getAccount(any()))
                .willReturn(Mono.just(accountVO));
        // When
        webTestClient.get()
                .uri(BASE_URL + "/account/1")
                .exchange()
                // Then
                .expectStatus().isEqualTo(200)
                .expectHeader().contentType(APPLICATION_JSON_VALUE)
                .expectBody()
                .jsonPath("$.number").isEqualTo("1");
    }


    @Test
    @DisplayName("Should return 404 when account is not found")
    public void shouldReturn404WhenAccountIsNotFound() throws Exception {
        // Given
        given(accountService.getAccount(any()))
                .willReturn(Mono.error(TransferException.of(ACCOUNT_NOT_FOUND)));
        // When
        webTestClient.get()
                .uri(BASE_URL + "/account/1")
                .exchange()
                // Then
                .expectStatus().isEqualTo(404)
                .expectHeader().contentType(APPLICATION_JSON_VALUE)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Account does not exist");
    }
}