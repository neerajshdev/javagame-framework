package javagames.util;

public class NotARectangleException extends RuntimeException {
    public NotARectangleException (String message) {
        super(message);
    }
    public NotARectangleException(String message, Throwable cause) {
        super(message, cause);
    }
}
