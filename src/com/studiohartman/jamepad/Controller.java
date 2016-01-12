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
    private boolean connected;

    /**
     * Constructor
     *
     * @param index The index of the controller (player number)
     * @throws JamepadRuntimeException
     */
    public Controller(int index) {
        this.index = index;

        connected = nativeConnectController(index);
        if(!connected) {
            throw new JamepadRuntimeException("Controller at index " + index + " failed to connect!");
        }
    }
    private native boolean nativeConnectController(int index); /*
        pad = SDL_GameControllerOpen(index);

        if(pad) {
            return 1;
        } else {
            return 0;
        }
    */

    /**
     * Close the connection to this controller.
     */
    public void close() {
        nativeClose();
    }
    private native void nativeClose(); /*
        if(pad && SDL_GameControllerGetAttached(pad)) {
            SDL_GameControllerClose(pad);
        }
        pad = NULL;
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
     * @throws JamepadRuntimeException
     */
    public boolean isButtonPressed(ControllerButton toCheck) {
        ensureConnected();
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
     * @throws JamepadRuntimeException
     */
    public float getAxisState(ControllerAxis toCheck) {
        ensureConnected();
        return nativeCheckAxis(toCheck.ordinal()) / AXIS_MAX_VAL;
    }
    private native int nativeCheckAxis(int index); /*
        SDL_GameControllerUpdate();
        return SDL_GameControllerGetAxis(pad, (SDL_GameControllerAxis) index);
    */

    /**
     * Returns the implementation dependent name of this controller.
     *
     * @return The the name of this controller
     * @throws JamepadRuntimeException
     */
    public String getName() {
        ensureConnected();
        String controllerName = nativeGetName();

        //Return empty string instead of null if the attached controller does not have a name
        if(controllerName == null) {
            return "";
        }

        return controllerName;
    }
    private native String nativeGetName(); /*
        return env->NewStringUTF(SDL_GameControllerName(pad));
    */

    private void ensureConnected() {
        if(!connected || !nativeEnsureConnected()) {
            throw new JamepadRuntimeException("Controller at index " + index + " is not connected!");
        }
    }
    private native boolean nativeEnsureConnected(); /*
        if (pad &&
            SDL_GameControllerGetAttached(pad) &&
            SDL_WasInit(SDL_Init(SDL_INIT_EVENTS | SDL_INIT_JOYSTICK | SDL_INIT_GAMECONTROLLER | SDL_INIT_HAPTIC))) {

            return JNI_TRUE;
        }
        return JNI_FALSE;
    */
}
