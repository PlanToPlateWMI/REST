package pl.plantoplate.REST.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class WrongInviteCode extends RuntimeException{

    public WrongInviteCode() {
    }

    public WrongInviteCode(String message) {
        super(message);
    }
}
