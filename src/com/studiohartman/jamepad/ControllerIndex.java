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
    private long hapticPtr;
    private int hapticEffectID;

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
        hapticEffectID = -1;

        connectController();
    }
    private void connectController() {
        controllerPtr = nativeConnectController(index);
        hapticPtr = nativeConnectHaptic(controllerPtr);
        if(hapticPtr != 0) {
            hapticEffectID = nativeBuildHapticEffect(hapticPtr);
        }
    }
    private native long nativeConnectController(int index); /*
        return (jlong) SDL_GameControllerOpen(index);
    */
    private native long nativeConnectHaptic(long controllerPtr); /*
        SDL_GameController* pad = (SDL_GameController*) controllerPtr;
        return (jlong) SDL_HapticOpenFromJoystick(SDL_GameControllerGetJoystick(pad));
    */
    private native int nativeBuildHapticEffect(long hapticPtr); /*
        SDL_Haptic* haptic = (SDL_Haptic*) hapticPtr;

        //Check that left/right vibration is supported
        if((SDL_HapticQuery(haptic) & SDL_HAPTIC_LEFTRIGHT) == 0) {
            return -1;
        }

        SDL_HapticEffect effect;
        memset(&effect, 0, sizeof(SDL_HapticEffect));
        effect.type = SDL_HAPTIC_LEFTRIGHT;
        effect.leftright.length = SDL_HAPTIC_INFINITY;

        return SDL_HapticNewEffect(haptic, &effect);
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
     * Returns whether or not the controller is currently vibrating.
     * @return whether or not the controller is currently vibrating.
     */
    public boolean isVibrating() {
        return hapticPtr != 0;
    }

    /**
     * Start vibrating the controller.
     * This will return false if the controller doesn't support vibration or if SDL was unable to start
     * vibration (maybe the controller doesn't support left/right vibration, maybe it was unplugged in the
     * middle of trying, etc...)
     *
     * @param leftMagnitude The speed for the left motor to vibrate (this should be between 0 and 1)
     * @param rightMagnitude The speed for the right motor to vibrate (this should be between 0 and 1)
     * @return Whether or not the controller was able to be vibrated (i.e. if haptics are supported)
     * @throws ControllerUnpluggedException If the controller is not connected
     */
    public boolean startVibration(float leftMagnitude, float rightMagnitude) throws ControllerUnpluggedException {
        ensureConnected();

        //Check the values are appropriate
        boolean leftInRange = leftMagnitude >= 0 && leftMagnitude <= 1;
        boolean rightInRange = rightMagnitude >= 0 && rightMagnitude <= 1;
        if(!(leftInRange && rightInRange)) {
            throw new IllegalArgumentException("The passed values are not in the range 0 to 1!");
        }

        //Don't bother calling native code if the controller doesn't support vibration
        if(hapticPtr == 0) {
            return false;
        }

        return nativeStartVibration(hapticPtr, hapticEffectID,
                (int) (32767 * leftMagnitude), (int) (32767 * rightMagnitude));
    }
    private native boolean nativeStartVibration(long hapticPtr, int effectID, int leftMagnitude, int rightMagnitude); /*
        SDL_Haptic* haptic = (SDL_Haptic*) hapticPtr;

        //Update the effect
        SDL_HapticEffect effect;
        memset(&effect, 0, sizeof(SDL_HapticEffect));
        effect.type = SDL_HAPTIC_LEFTRIGHT;
        effect.leftright.length = SDL_HAPTIC_INFINITY;
        effect.leftright.large_magnitude = leftMagnitude;
        effect.leftright.small_magnitude = rightMagnitude;
        SDL_HapticUpdateEffect(haptic, effectID, &effect);

        //Stop any previously running effects before starting a new one
        SDL_HapticStopAll(haptic);

        return SDL_HapticRunEffect(haptic, effectID, 1) == 0;
    */

    /**
     * Stops any currently running vibration effects.
     */
    public void stopVibration() {
        nativeStopVibration(hapticPtr);
    }
    private native void nativeStopVibration(long hapticPtr); /*
        SDL_Haptic* haptic = (SDL_Haptic*) hapticPtr;
        SDL_HapticStopAll(haptic);
    */

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
