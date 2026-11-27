package exceptions;

public class ItemAlreadyClaimedException extends Exception {
    public ItemAlreadyClaimedException(String message) {
        super(message);
    }
}
