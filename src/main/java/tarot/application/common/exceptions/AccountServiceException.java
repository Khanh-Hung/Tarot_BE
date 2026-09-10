package tarot.application.common.exceptions;

public class AccountServiceException extends RuntimeException {

    private final int statusCode;

    public AccountServiceException(String message) {
        this(message, 500);
    }

    public AccountServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public AccountServiceException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
