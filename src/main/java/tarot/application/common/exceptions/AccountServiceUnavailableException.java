package tarot.application.common.exceptions;

public class AccountServiceUnavailableException extends AccountServiceException {

    public AccountServiceUnavailableException(String message) {
        super(message, 503);
    }

    public AccountServiceUnavailableException(String message, Throwable cause) {
        super(message, cause, 503);
    }
}
