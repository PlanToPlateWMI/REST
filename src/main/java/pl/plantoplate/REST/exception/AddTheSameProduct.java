package pl.plantoplate.REST.exception;

public class AddTheSameProduct extends Exception{

    public AddTheSameProduct(String message) {
        super(message);
    }

    public AddTheSameProduct(String message, Throwable cause) {
        super(message, cause);
    }
}
