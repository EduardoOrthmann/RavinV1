package exceptions;

public class UnauthorizedRequestException extends Exception {
    public UnauthorizedRequestException() {
        super("Usuário não autorizado!");
    }

    public UnauthorizedRequestException(String message) {
        super(message);
    }
}

