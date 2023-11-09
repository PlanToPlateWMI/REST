package pl.plantoplate.REST.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class DeleteNotSelected extends RuntimeException{

    public DeleteNotSelected(String message) {
        super(message);
    }
}
