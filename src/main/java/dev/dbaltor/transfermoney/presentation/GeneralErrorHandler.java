package dev.dbaltor.transfermoney.presentation;

import dev.dbaltor.transfermoney.domain.TransferException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import static java.util.stream.Collectors.*;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GeneralErrorHandler {

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorMessage> handleInputValidation(HandlerMethodValidationException e) {
        return e.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .peek(log::error)
                .collect(collectingAndThen(
                        joining("; "),
                        message -> ResponseEntity.badRequest().body(new ErrorMessage(message))));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorMessage> handleInputValidation(WebExchangeBindException e) {
        return e.getBindingResult()
                .getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .peek(log::error)
                .collect(collectingAndThen(
                        joining("; "),
                        message -> ResponseEntity.badRequest().body(new ErrorMessage(message))));
    }

    @ExceptionHandler(TransferException.class)
    public ResponseEntity<ErrorMessage> handleBusinessException(TransferException ex) {
        log.error(ex.getError().name());
        val errorResponse = getError(ex);
        return ResponseEntity.status(errorResponse.status())
                .body(new ErrorMessage(errorResponse.message()));
    }

    private ErrorResponse getError(TransferException ex) {
        return switch (ex.getError()) {
            case ACCOUNT_NOT_FOUND -> new ErrorResponse(NOT_FOUND, "Account does not exist");
            case FROM_ACCOUNT_NOT_FOUND -> new ErrorResponse(NOT_FOUND, "Account to debit from does not exist");
            case TO_ACCOUNT_NOT_FOUND -> new ErrorResponse(NOT_FOUND, "Account to credit to does not exist");
            case FROM_AND_TO_ACCOUNTS_ARE_THE_SAME -> new ErrorResponse(BAD_REQUEST, "Account to debit and credit are the same");
            case INSUFFICIENT_BALANCE -> new ErrorResponse(CONFLICT, "Insufficient funds");
        };
    }

}