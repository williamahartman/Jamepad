package com.studiohartman.jamepad;

/**
 * Signals that some kind of runtime-y error with gamepads has occurred.
 *
 * @author  William Hartman
 */
public class JamepadRuntimeException extends RuntimeException {
    /**
     * Constructs a {@code JamepadRuntimeException} with {@code null}
     * as its error detail message.
     */
    public JamepadRuntimeException() {
        super();
    }

    /**
     * Constructs a {@code JamepadRuntimeException} with the specified detail message.
     *
     * @param message
     *        The detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method)
     */
    public JamepadRuntimeException(String message) {
        super(message);
    }
}
