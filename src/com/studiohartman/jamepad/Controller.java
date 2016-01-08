package com.studiohartman.jamepad;

/**
 * This class is the main thing you're gonna need to deal with. This is where
 * you get the states of buttons and axes for a single gamepad.
 *
 * The gamepads are defined by their index (player number, but starting at 0).
 */
public class Controller {
    /*JNI

    #include "SDL.h"

    SDL_GameController* pad;
    */

    private static final float AXIS_MAX_VAL = 32767;
    private int index;

    /**
     * Constructor
     *
     * @param index The index of the controller (player number)
     */
    public Controller(int index) {
        this.index = index;
        nativeConnectController(index);
    }
    private native void nativeConnectController(int index); /*
        pad = SDL_GameControllerOpen(index);
    */

    /**
     * Returns the index of the current controller.
     * @return The index of the current controller.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns whether or not a given button has been pressed.
     *
     * @param toCheck The ControllerButton to check the state of
     * @return Whether or not the button is pressed.
     */
    public boolean getButtonState(ControllerButton toCheck) {
        return nativeCheckButton(toCheck.ordinal());
    }
    private native boolean nativeCheckButton(int index); /*
        SDL_GameControllerUpdate();
        return SDL_GameControllerGetButton(pad, (SDL_GameControllerButton) index);
    */

    /**
     * Returns the current state of a passed axis.
     *
     * @param toCheck The ControllerAxis to check the state of
     * @return The current state of the requested axis.
     */
    public float getAxisState(ControllerAxis toCheck) {
        return nativeCheckAxis(toCheck.ordinal()) / AXIS_MAX_VAL;
    }
    private native int nativeCheckAxis(int index); /*
        SDL_GameControllerUpdate();
        return SDL_GameControllerGetAxis(pad, (SDL_GameControllerAxis) index);
    */
}
