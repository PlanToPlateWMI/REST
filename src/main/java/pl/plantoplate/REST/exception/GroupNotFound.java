package pl.plantoplate.REST.exception;

public class GroupNotFound extends Exception{

    public GroupNotFound(String message) {
        super(message);
    }

    public GroupNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public GroupNotFound(Throwable cause) {
        super(cause);
    }
}
