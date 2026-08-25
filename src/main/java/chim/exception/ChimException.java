package chim.exception;

/**
 * Represents an exception specific to chim.Chim, thrown when user input cannot
 * be understood or does not meet chim.Chim's expected command format.
 */
public class ChimException extends Exception {

    /**
     * Creates a chim.exception.ChimException with the given message.
     *
     * @param message Explanation of what went wrong, shown to the user.
     */
    public ChimException(String message) {
        super(message);
    }
}