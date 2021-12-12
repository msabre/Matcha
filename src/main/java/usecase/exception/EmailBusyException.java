package usecase.exception;

public class EmailBusyException extends Exception {

    public EmailBusyException() {
        super("This email busy already");
    }
}
