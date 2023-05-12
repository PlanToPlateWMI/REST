package pl.plantoplate.REST.exception;

public class WrongProductInShoppingList extends Exception{
    public WrongProductInShoppingList(String message) {
        super(message);
    }

    public WrongProductInShoppingList(String message, Throwable cause) {
        super(message, cause);
    }
}
