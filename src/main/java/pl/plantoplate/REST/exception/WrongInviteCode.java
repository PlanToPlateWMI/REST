package pl.plantoplate.REST.exception;

public class WrongInviteCode extends Exception{

    public WrongInviteCode() {
    }

    public WrongInviteCode(String message) {
        super(message);
    }
}
