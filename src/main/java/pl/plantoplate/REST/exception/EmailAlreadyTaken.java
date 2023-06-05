package pl.plantoplate.REST.exception;

public class EmailAlreadyTaken extends RuntimeException{

    public EmailAlreadyTaken(String message) {
        super(message);
    }

    public EmailAlreadyTaken(String message, Throwable cause) {
        super(message, cause);
    }
}
