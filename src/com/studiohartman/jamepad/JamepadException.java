package com.studiohartman.jamepad;

/**
 * Signals that some kind of error with gamepads has occurred.
 *
 * @author  William Hartman
 */
public class JamepadException extends Exception {
    /**
     * Constructs a {@code JamepadException} with {@code null}
     * as its error detail message.
     */
    public JamepadException() {
        super();
    }

    /**
     * Constructs a {@code JamepadException} with the specified detail message.
     *
     * @param message
     *        The detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method)
     */
    public JamepadException(String message) {
        super(message);
    }
}
