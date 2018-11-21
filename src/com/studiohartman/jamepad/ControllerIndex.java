package com.studiohartman.jamepad;

/**
 * This class is the main thing you're gonna need to deal with if you want lots of
 * control over your gamepads or want to avoid lots of ControllerState allocations.
 *
 * A Controller index cannot be made from outside the Jamepad package. You're gonna need to go
 * through a ControllerManager to get your controllers.
 *
 * A ControllerIndex represents the controller at a given index. There may or may not actually
 * be a controller at that index. Exceptions are thrown if the controller is not connected.
 *
 * @author William Hartman
 */
public final class ControllerIndex {
    /*JNI
    #include "SDL.h"
    */

    private static final float AXIS_MAX_VAL = 32767;
    private int index;
    private long controllerPtr;

    private boolean[] heldDownButtons;
    private boolean[] justPressedButtons;

    /**
     * Constructor. Builds a controller at the given index and attempts to connect to it.
     * This is only accessible in the Jamepad package, so people can't go trying to make controllers
     * before the native library is loaded or initialized.
     *
     * @param index The index of the controller
     */
    ControllerIndex(int index) {
        this.index = index;

        heldDownButtons = new boolean[ControllerButton.values().length];
        justPressedButtons = new boolean[ControllerButton.values().length];
        for(int i = 0; i < heldDownButtons.length; i++) {
            heldDownButtons[i] = false;
            justPressedButtons[i] = false;
        }

        connectController();
    }
    private void connectController() {
        controllerPtr = nativeConnectController(index);
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
            controllerPtr = 0;
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
     * Close and reconnect to the native gamepad at the index associated with this ControllerIndex object.
     * This is will refresh the gamepad represented here. This should be called if something is plugged
     * in or unplugged.
     *
     * @return whether or not the controller could successfully reconnect.
     */
    public boolean reconnectController() {
        close();
        connectController();

        return isConnected();
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
     * Start vibrating the controller.
     *
     * @deprecated use doVibration instead
     * @param leftMagnitude The speed for the left motor to vibrate (this should be between 0 and 1)
     * @param rightMagnitude The speed for the right motor to vibrate (this should be between 0 and 1)
     * @return Whether or not the controller was able to be vibrated (i.e. if haptics are supported)
     * @throws ControllerUnpluggedException If the controller is not connected
     */
    @Deprecated
    public boolean startVibration(float leftMagnitude, float rightMagnitude) throws ControllerUnpluggedException {
        return doVibration(leftMagnitude,rightMagnitude , 1000);
    }

    private native boolean nativeDoVibration(long controllerPtr, int leftMagnitude, int rightMagnitude, int duration_ms); /*
        SDL_Joystick* joystick = SDL_GameControllerGetJoystick((SDL_GameController*) controllerPtr);
        return SDL_JoystickRumble(joystick, leftMagnitude, rightMagnitude,  duration_ms) == 0;
    */

    /**
     * Vibrate the controller using the new rumble API
     * This will return false if the controller doesn't support vibration or if SDL was unable to start
     * vibration (maybe the controller doesn't support left/right vibration, maybe it was unplugged in the
     * middle of trying, etc...)
     *
     * @param leftMagnitude The speed for the left motor to vibrate (this should be between 0 and 1)
     * @param rightMagnitude The speed for the right motor to vibrate (this should be between 0 and 1)
     * @return Whether or not the controller was able to be vibrated (i.e. if haptics are supported)
     * @throws ControllerUnpluggedException If the controller is not connected
     */
    public boolean doVibration(float leftMagnitude, float rightMagnitude, int duration_ms) throws ControllerUnpluggedException {
        ensureConnected();

        //Check the values are appropriate
        boolean leftInRange = leftMagnitude >= 0 && leftMagnitude <= 1;
        boolean rightInRange = rightMagnitude >= 0 && rightMagnitude <= 1;
        if(!(leftInRange && rightInRange)) {
            throw new IllegalArgumentException("The passed values are not in the range 0 to 1!");
        }

        return nativeDoVibration(controllerPtr, (int) (32767 * leftMagnitude), (int) (32767 * rightMagnitude), duration_ms);
    }

    /**
     * Does nothing
     * @deprecated not needed by new rumble API
     */
    @Deprecated
    public void stopVibration() {
    }

    /**
     * Returns whether or not a given button has been pressed.
     *
     * @param toCheck The ControllerButton to check the state of
     * @return Whether or not the button is pressed.
     * @throws ControllerUnpluggedException If the controller is not connected
     */
    public boolean isButtonPressed(ControllerButton toCheck) throws ControllerUnpluggedException {
        updateButton(toCheck.ordinal());
        return heldDownButtons[toCheck.ordinal()];
    }

    /**
     * Returns whether or not a given button has just been pressed since you last made a query
     * about that button (either through this method, isButtonPressed(), or through the ControllerState
     * side of things). If the button was not pressed the last time you checked but is now, this method
     * will return true.
     *
     * @param toCheck The ControllerButton to check the state of
     * @return Whether or not the button has just been pressed.
     * @throws ControllerUnpluggedException If the controller is not connected
     */
    public boolean isButtonJustPressed(ControllerButton toCheck) throws ControllerUnpluggedException {
        updateButton(toCheck.ordinal());
        return justPressedButtons[toCheck.ordinal()];
    }

    private void updateButton(int buttonIndex) throws ControllerUnpluggedException {
        ensureConnected();

        boolean currButtonIsPressed = nativeCheckButton(controllerPtr, buttonIndex);
        justPressedButtons[buttonIndex] = (currButtonIsPressed && !heldDownButtons[buttonIndex]);
        heldDownButtons[buttonIndex] = currButtonIsPressed;
    }
    private native boolean nativeCheckButton(long controllerPtr, int buttonIndex); /*
        SDL_GameControllerUpdate();
        SDL_GameController* pad = (SDL_GameController*) controllerPtr;
        return SDL_GameControllerGetButton(pad, (SDL_GameControllerButton) buttonIndex);
    */

    /**
     * Returns the current state of a passed axis.
     *
     * @param toCheck The ControllerAxis to check the state of
     * @return The current state of the requested axis.
     * @throws ControllerUnpluggedException If the controller is not connected
     */
    public float getAxisState(ControllerAxis toCheck) throws ControllerUnpluggedException {
        ensureConnected();

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
     * @throws ControllerUnpluggedException If the controller is not connected
     */
    public String getName() throws ControllerUnpluggedException {
        ensureConnected();

        String controllerName = nativeGetName(controllerPtr);

        //Return a descriptive string instead of null if the attached controller does not have a name
        if(controllerName == null) {
            return "Unnamed Controller";
        }
        return controllerName;
    }
    private native String nativeGetName(long controllerPtr); /*
        SDL_GameController* pad = (SDL_GameController*) controllerPtr;
        return env->NewStringUTF(SDL_GameControllerName(pad));
    */

    /**
     * Convenience method to throw an exception if the controller is not connected.
     */
    private void ensureConnected() throws ControllerUnpluggedException {
        if(!isConnected()) {
            throw new ControllerUnpluggedException("Controller at index " + index + " is not connected!");
        }
    }
}
