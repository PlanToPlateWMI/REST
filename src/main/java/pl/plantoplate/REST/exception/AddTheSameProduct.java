package pl.plantoplate.REST.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class AddTheSameProduct extends RuntimeException{

    public AddTheSameProduct(String message) {
        super(message);
    }

    public AddTheSameProduct(String message, Throwable cause) {
        super(message, cause);
    }
}
