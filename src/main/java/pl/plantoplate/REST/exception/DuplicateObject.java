package pl.plantoplate.REST.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class DuplicateObject extends RuntimeException{

    public DuplicateObject(String message) {
        super(message);
    }

}
