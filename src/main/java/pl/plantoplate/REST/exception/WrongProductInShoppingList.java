package pl.plantoplate.REST.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class WrongProductInShoppingList extends RuntimeException{
    public WrongProductInShoppingList(String message) {
        super(message);
    }

    public WrongProductInShoppingList(String message, Throwable cause) {
        super(message, cause);
    }
}
