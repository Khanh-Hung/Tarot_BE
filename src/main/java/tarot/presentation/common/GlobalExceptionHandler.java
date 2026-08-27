package tarot.presentation.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tarot.application.common.result.Error;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleGenericException(Exception e) {
        log.error("Unhandled Exception caught in GlobalExceptionHandler: {}", e.getMessage(), e);
        Error error = new Error("INTERNAL_SERVER_ERROR", e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}