package reclaim.exceptions;

/** Thrown when user input or saved data is not valid. */
public class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}
