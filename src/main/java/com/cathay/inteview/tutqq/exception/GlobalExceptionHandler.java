package com.cathay.inteview.tutqq.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    // =====================================================
    // ERROR RESPONSE DTO
    // =====================================================

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorResponse {
        private String error;
        private String message;
        private String details;
        private int status;
        private String path;
        private List<ValidationError> validationErrors;
        private String traceId;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        private LocalDateTime timestamp = LocalDateTime.now();

        public ErrorResponse(String error, String message, int status, String path) {
            this.error = error;
            this.message = message;
            this.status = status;
            this.path = path;
        }
    }

    @Getter @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ValidationError {
        private String field;
        private Object rejectedValue;
        private String message;

        public ValidationError(String field, Object rejectedValue, String message) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.message = message;
        }
    }

    // =====================================================
    // BUSINESS EXCEPTIONS
    // =====================================================

    @ExceptionHandler(ExchangeRateApiException.class)
    public ResponseEntity<ErrorResponse> handleExchangeRateApiException(
            ExchangeRateApiException ex, WebRequest request) {

        log.error("Exchange Rate API Exception: {}", ex.getMessage(), ex);

        String localizedMessage = getMessage("error.exchangeRate.api");

        ErrorResponse response = new ErrorResponse(
                "EXCHANGE_RATE_API_ERROR",
                localizedMessage,
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                getPath(request)
        );
        response.setDetails(ex.getMessage());
        response.setTraceId(generateTraceId());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @ExceptionHandler(DataSyncException.class)
    public ResponseEntity<ErrorResponse> handleDataSyncException(
            DataSyncException ex, WebRequest request) {

        log.error("Data Sync Exception: {}", ex.getMessage(), ex);

        String localizedMessage = getMessage("error.dataSync");

        ErrorResponse response = new ErrorResponse(
                "DATA_SYNC_ERROR",
                localizedMessage,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                getPath(request)
        );
        response.setDetails(ex.getMessage());
        response.setTraceId(generateTraceId());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // ... other exception handlers follow the same pattern ...

    // =====================================================
    // HELPER METHODS
    // =====================================================

    private String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    private String generateTraceId() {
        return "trace-" + System.currentTimeMillis() + "-" + Thread.currentThread().getId();
    }

    private String getMessage(String code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, locale);
    }

    private String extractConstraintViolationMessage(String message) {
        if (message == null) {
            return getMessage("error.constraint.default");
        }
        if (message.contains("unique constraint") || message.contains("UNIQUE constraint")) {
            return getMessage("error.constraint.unique");
        } else if (message.contains("foreign key constraint")) {
            return getMessage("error.constraint.foreignKey");
        } else if (message.contains("check constraint")) {
            return getMessage("error.constraint.check");
        } else if (message.contains("not-null constraint")) {
            return getMessage("error.constraint.notNull");
        }
        return getMessage("error.constraint.default");
    }
}
