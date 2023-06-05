package pl.plantoplate.REST.exception;

public class WrongRequestData extends RuntimeException{
    public WrongRequestData(String message) {
        super(message);
    }
}
