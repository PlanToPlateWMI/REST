package pl.plantoplate.REST.exception;

public class UserNotFromGroup extends  RuntimeException{
    public UserNotFromGroup(String message) {
        super(message);
    }

    public UserNotFromGroup(String message, Throwable cause) {
        super(message, cause);
    }
}
