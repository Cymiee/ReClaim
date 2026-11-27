package reclaim.exceptions;

/** Thrown when someone tries to claim an item that is already claimed. */
public class ItemAlreadyClaimedException extends Exception {
    public ItemAlreadyClaimedException(String message) {
        super(message);
    }
}
