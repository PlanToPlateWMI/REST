package pl.plantoplate.REST.exception;

public class CategoryNotFound extends Exception{

    public CategoryNotFound(String message) {
        super(message);
    }

    public CategoryNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
