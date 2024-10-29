package dev.dbaltor.transfermoney.presentation;

import org.springframework.http.HttpStatus;

public record ErrorResponse(HttpStatus status, String message) {
}
