package tarot.presentation.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tarot.application.common.result.Error;

import tarot.application.common.exceptions.AccountServiceException;
import tarot.application.common.exceptions.AccountServiceUnavailableException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountServiceUnavailableException.class)
    public ResponseEntity<Error> handleAccountServiceUnavailable(AccountServiceUnavailableException e) {
        log.error("Account service unavailable: {}", e.getMessage(), e);
        Error error = new Error("ACCOUNT_SERVICE_UNAVAILABLE", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @ExceptionHandler(AccountServiceException.class)
    public ResponseEntity<Error> handleAccountServiceException(AccountServiceException e) {
        log.error("Account service error (status {}): {}", e.getStatusCode(), e.getMessage());
        HttpStatus status = HttpStatus.resolve(e.getStatusCode());
        if (status == null) status = HttpStatus.BAD_GATEWAY;
        Error error = new Error("ACCOUNT_SERVICE_ERROR", e.getMessage());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleGenericException(Exception e) {
        log.error("Unhandled Exception caught in GlobalExceptionHandler: {}", e.getMessage(), e);
        Error error = new Error("INTERNAL_SERVER_ERROR", e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}