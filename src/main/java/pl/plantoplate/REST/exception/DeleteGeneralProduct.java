package pl.plantoplate.REST.exception;

public class DeleteGeneralProduct extends Exception{

    public DeleteGeneralProduct() {
        super();
    }

    public DeleteGeneralProduct(String message) {
        super(message);
    }

    public DeleteGeneralProduct(String message, Throwable cause) {
        super(message, cause);
    }
}
