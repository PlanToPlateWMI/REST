package pl.plantoplate.REST.exception;

public class WrongQueryParam extends RuntimeException{
    public WrongQueryParam(String message) {
        super(message);
    }
}
