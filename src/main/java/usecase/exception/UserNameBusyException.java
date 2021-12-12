package usecase.exception;

public class UserNameBusyException extends Exception {

    public UserNameBusyException() {
        super("This username busy already");
    }
}
