package usecase.exception;

public class SamePasswordException extends Exception {

    public SamePasswordException() {
        super("New and old passwords are the same");
    }
}
