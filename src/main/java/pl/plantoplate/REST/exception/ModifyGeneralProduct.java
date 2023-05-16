package pl.plantoplate.REST.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ModifyGeneralProduct extends RuntimeException{

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
