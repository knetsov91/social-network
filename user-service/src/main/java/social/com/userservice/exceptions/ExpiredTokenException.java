package social.com.userservice.exceptions;

public class ExpiredTokenException extends RuntimeException {

    public ExpiredTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}