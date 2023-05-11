package pl.plantoplate.REST.exception;

public class ModifyGeneralProduct extends Exception{

    public ModifyGeneralProduct() {
        super();
    }

    public ModifyGeneralProduct(String message) {
        super(message);
    }

    public ModifyGeneralProduct(String message, Throwable cause) {
        super(message, cause);
    }
}
