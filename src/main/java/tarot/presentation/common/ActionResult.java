package tarot.presentation.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tarot.application.common.result.Error;
import tarot.application.common.result.Result;

import java.util.Map;

/**
 * Helper chuyển đổi từ Result<T> sang ResponseEntity<?> chuẩn REST API (Tương đương ToActionResult() bên C#)
 */
public final class ActionResult {

    private ActionResult() {}

    public static <T> ResponseEntity<?> from(Result<T> result) {
        return from(result, HttpStatus.OK);
    }

    public static <T> ResponseEntity<?> from(Result<T> result, HttpStatus successStatus) {
        if (result.isSuccess()) {
            return ResponseEntity.status(successStatus).body(result.getDataOrNull());
        }

        Error error = result.getErrorOrNone();
        String code = (error.code() != null) ? error.code().toUpperCase() : "";

        if (code.contains("NOT_FOUND")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        if (code.contains("UNAUTHORIZED") || code.contains("INVALID_CREDENTIALS")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        if (code.contains("FORBIDDEN") || code.contains("USER_DEACTIVATED")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
        if (code.contains("CONFLICT") || code.contains("ALREADY_EXISTS")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}