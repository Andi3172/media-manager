package project.exceptions;

/**
 * Exception thrown when a provided filesystem location is invalid for monitoring
 * (for example, when the path does not point to a directory).
 * <p>
 * This is a checked exception to force callers to handle invalid configuration
 * or user-provided paths explicitly.
 */
public class InvalidLocationException extends Exception{
    
    /**
     * Constructs a new {@code InvalidLocationException} with the specified detail message.
     *
     * @param message a human-readable message describing the reason for the exception
     */
    public InvalidLocationException(String message) {
        super(message);
    }
}
