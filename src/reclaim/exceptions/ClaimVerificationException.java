package reclaim.exceptions;

/** Thrown when the answer to a verification question is wrong. */
public class ClaimVerificationException extends Exception {
    public ClaimVerificationException(String message) {
        super(message);
    }
}
