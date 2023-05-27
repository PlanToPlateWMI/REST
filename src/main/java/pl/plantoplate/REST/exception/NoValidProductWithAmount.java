package pl.plantoplate.REST.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class NoValidProductWithAmount extends RuntimeException{
    public NoValidProductWithAmount(String message) {
        super(message);
    }

    public NoValidProductWithAmount(String message, Throwable cause) {
        super(message, cause);
    }
}
