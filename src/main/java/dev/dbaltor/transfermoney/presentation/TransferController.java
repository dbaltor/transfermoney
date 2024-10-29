package dev.dbaltor.transfermoney.presentation;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.dbaltor.transfermoney.domain.TransferService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "api")
@RequiredArgsConstructor
public class TransferController {

    private @NonNull TransferService transferService;

    @PostMapping(value = "transfer", consumes = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> requestTransfer(@Valid @RequestBody TransferRequest transferRequest) {

        return transferService.requestTransfer(transferRequest.transfer())
                .map(t -> ResponseEntity.noContent().build());
    }

/*
    // Local error handling implementation rather than ControllerAdvice
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        val errors = new HashMap<String, String>();
        ex.getAllErrors().forEach(error -> {
            errors.put(
                ((FieldError)error).getField(),
                error.getDefaultMessage()
            );
        });
        return errors;
    }
*/
}

