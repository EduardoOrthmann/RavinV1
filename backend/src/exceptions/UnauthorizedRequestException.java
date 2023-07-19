package exceptions;

public class UnauthorizedRequestException extends Exception {
    public UnauthorizedRequestException() {
        super("Unauthorized Request!");
    }

    public UnauthorizedRequestException(String message) {
        super(message);
    }
}

