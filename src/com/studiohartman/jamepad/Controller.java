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
    */

    private static final float AXIS_MAX_VAL = 32767;
    private int index;
    private long controllerPtr;

    /**
     * Constructor
     *
     * @param index The index of the controller (player number)
     * @throws JamepadRuntimeException
     */
    public Controller(int index) {
        this.index = index;

        connectController();
    }
    private void connectController() {
        controllerPtr = nativeConnectController(index);
        if(controllerPtr == 0) {
            throw new JamepadRuntimeException("Controller at index " + index + " failed to connect!");
        }
    }
    private native long nativeConnectController(int index); /*
        return (jlong) SDL_GameControllerOpen(index);
    */

    /**
     * Close the connection to this controller.
     */
    public void close() {
        if(controllerPtr != 0) {
            nativeClose(controllerPtr);
        }
    }
    private native void nativeClose(long controllerPtr); /*
        SDL_GameController* pad = (SDL_GameController*) controllerPtr;
        if(pad && SDL_GameControllerGetAttached(pad)) {
            SDL_GameControllerClose(pad);
        }
        pad = NULL;
    */

    /**
     * Close and reconnect to the native gamepad at the index associated with this Controller object
     *
     * @throws JamepadRuntimeException
     */
    public void reconnectController() {
        close();
        connectController();
    }

    /**
     * Return whether or not the controller is currently connected. This first checks that the controller
     * was successfully connected to our SDL backend. Then we check if the controller is currently plugged
     * in.
     *
     * @return Whether or not the controller is plugged in.
     */
    public boolean isConnected() {
        return controllerPtr != 0 && nativeIsConnected(controllerPtr);
    }
    private native boolean nativeIsConnected(long controllerPtr); /*
        SDL_GameController* pad = (SDL_GameController*) controllerPtr;
        if (pad && SDL_GameControllerGetAttached(pad)) {
            return JNI_TRUE;
        }
        return JNI_FALSE;
    */

    /**
     * Returns the index of the current controller.
     * @return The index of the current controller.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns whether or not a given button has been pressed. Returns false if the gamepad is disconnected.
     * This is both to avoid extra try/catch blocks in your code and to hopefully have less annoying behavior
     * for the user on controller disconnections (hopefully nothing will happen, but that could depend on
     * your application).
     *
     * @param toCheck The ControllerButton to check the state of
     * @return Whether or not the button is pressed.
     */
    public boolean isButtonPressed(ControllerButton toCheck) {
        try {
            if(!isConnected()) {
                throw new JamepadRuntimeException("Controller at index " + index + " is not connected!");
            }
        } catch (JamepadRuntimeException e) {
            return false;
        }
        return nativeCheckButton(controllerPtr, toCheck.ordinal());
    }
    private native boolean nativeCheckButton(long controllerPtr, int buttonIndex); /*
        SDL_GameControllerUpdate();
        SDL_GameController* pad = (SDL_GameController*) controllerPtr;
        return SDL_GameControllerGetButton(pad, (SDL_GameControllerButton) buttonIndex);
    */

    /**
     * Returns the current state of a passed axis. Returns 0 if the gamepad is disconnected.
     * This is both to avoid extra try/catch blocks in your code and to hopefully have less
     * annoying behavior for the user on controller disconnections (hopefully nothing will
     * happen, but that could depend on your application).
     *
     * @param toCheck The ControllerAxis to check the state of
     * @return The current state of the requested axis.
     */
    public float getAxisState(ControllerAxis toCheck) {
        try {
            if(!isConnected()) {
                throw new JamepadRuntimeException("Controller at index " + index + " is not connected!");
            }
        } catch (JamepadRuntimeException e) {
            return 0;
        }

        float toReturn;

        //Note: we flip the Y values so up on the stick is positive. that makes more sense.
        if(toCheck == ControllerAxis.LEFTY || toCheck == ControllerAxis.RIGHTY) {
            toReturn = nativeCheckAxis(controllerPtr, toCheck.ordinal()) / -AXIS_MAX_VAL;
        } else {
            toReturn = nativeCheckAxis(controllerPtr, toCheck.ordinal()) / AXIS_MAX_VAL;
        }

        return toReturn;
    }
    private native int nativeCheckAxis(long controllerPtr, int axisIndex); /*
        SDL_GameControllerUpdate();
        SDL_GameController* pad = (SDL_GameController*) controllerPtr;
        return SDL_GameControllerGetAxis(pad, (SDL_GameControllerAxis) axisIndex);
    */

    /**
     * Returns the implementation dependent name of this controller.
     *
     * @return The the name of this controller
     * @throws JamepadRuntimeException
     */
    public String getName() {
        if(controllerPtr != 0) {
            String controllerName = nativeGetName(controllerPtr);

            //Return empty string instead of null if the attached controller does not have a name
            if(controllerName == null) {
                return "";
            }
            return controllerName;
        } else {
            return "Not Connected";
        }
    }
    private native String nativeGetName(long controllerPtr); /*
        SDL_GameController* pad = (SDL_GameController*) controllerPtr;
        return env->NewStringUTF(SDL_GameControllerName(pad));
    */

    @Override
    public String toString() {
        return "\\" + getName() + "\\" + "@" + index;
    }
}
