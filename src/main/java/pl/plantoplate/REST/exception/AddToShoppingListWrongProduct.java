package pl.plantoplate.REST.exception;

public class AddToShoppingListWrongProduct extends Exception{
    public AddToShoppingListWrongProduct(String message) {
        super(message);
    }

    public AddToShoppingListWrongProduct(String message, Throwable cause) {
        super(message, cause);
    }
}
