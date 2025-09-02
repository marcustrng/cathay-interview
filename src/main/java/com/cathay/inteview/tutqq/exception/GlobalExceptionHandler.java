package com.cathay.inteview.tutqq.exception;

import com.cathay.interview.tutqq.model.Error;
import com.cathay.interview.tutqq.model.ErrorItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    // =====================================================
    // HANDLERS FOR EACH EXCEPTION CLASS
    // =====================================================

    @ExceptionHandler(ExchangeRateApiException.class)
    public ResponseEntity<Error> handleExchangeRateApiException(
            ExchangeRateApiException ex, WebRequest request) {

        log.error("Exchange Rate API Exception: {}", ex.getMessage(), ex);
        return buildErrorResponse("EXCHANGE_RATE_API_ERROR", "error.exchangeRate.api",
                ex, request, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(DataSyncException.class)
    public ResponseEntity<Error> handleDataSyncException(
            DataSyncException ex, WebRequest request) {

        log.error("Data Sync Exception: {}", ex.getMessage(), ex);
        return buildErrorResponse("DATA_SYNC_ERROR", "error.dataSync",
                ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ProviderNotFoundException.class)
    public ResponseEntity<Error> handleProviderNotFoundException(
            ProviderNotFoundException ex, WebRequest request) {

        log.error("Provider not found: {}", ex.getProviderName(), ex);
        return buildErrorResponse(
                "PROVIDER_NOT_FOUND",
                "error.providerNotFound",
                ex,
                request,
                HttpStatus.NOT_FOUND,
                ex.getProviderName()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleUnexpectedException(
            Exception ex, WebRequest request) {

        log.error("Unexpected exception: {}", ex.getMessage(), ex);
        return buildErrorResponse(
                "UNEXPECTED_ERROR",
                "error.unexpected",
                ex,
                request,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    private ResponseEntity<Error> buildErrorResponse(
            String key,
            String messageCode,
            Exception ex,
            WebRequest request,
            HttpStatus status,
            Object... messageArgs) {

        String message = getMessage(messageCode, messageArgs);

        ErrorItem errorItem = new ErrorItem()
                .key(key)
                .message(ex.getMessage())
                .context(Map.of("path", getPath(request)));

        Error response = new Error()
                .key(key)
                .message(message)
                .errors(Collections.singletonList(errorItem));

        return ResponseEntity.status(status).body(response);
    }

    private String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    private String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
