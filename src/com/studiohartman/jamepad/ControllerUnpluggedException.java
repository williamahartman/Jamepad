package com.studiohartman.jamepad;

/**
 * Signals that you are trying to access a gamepad that is not plugged in.
 *
 * @author  William Hartman
 */
public class ControllerUnpluggedException extends Exception {
    /**
     * Constructs a {@code ControllerUnpluggedException} with {@code null}
     * as its error detail message.
     */
    public ControllerUnpluggedException() {
        super();
    }

    /**
     * Constructs a {@code ControllerUnpluggedException} with the specified detail message.
     *
     * @param message
     *        The detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method)
     */
    public ControllerUnpluggedException(String message) {
        super(message);
    }
}
