package pl.plantoplate.REST.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class NotValidGroup extends RuntimeException{

    public NotValidGroup(String message) {
        super(message);
    }
}
