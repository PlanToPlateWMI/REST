package pl.plantoplate.REST.exception;

public class UserChangeHisRole extends RuntimeException{
    public UserChangeHisRole(String message) {
        super(message);
    }

    public UserChangeHisRole(String message, Throwable cause) {
        super(message, cause);
    }
}
