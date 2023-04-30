package pl.plantoplate.REST.exception;

import java.io.IOException;

public class UserNotFound extends IOException {

    public UserNotFound(String message) {
        super(message);
    }

    public UserNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotFound(Throwable cause) {
        super(cause);
    }
}
