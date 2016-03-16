package com.studiohartman.jamepad;

/**
 * This class represents the state of a gamepad at a given moment. This includes
 * the state of the axes and the buttons.
 *
 * This is probably how most people should deal with gamepads.
 *
 * The isConnected field is pretty important. This is how you determine if the controller
 * you asked for is plugged in. If isConnected is false, all other fields will be 0 or false. For
 * some applications, you might not need to even bother checking isConnected.
 *
 * All fields are public, but immutable.
 */
public class ControllerState {
    /**
     * Whether or not the controller is currently connected.
     *
     * If the controller is disconnected, all other fields will be 0 or false.
     */
    public final boolean isConnected;

    /**
     * A string describing the type of controller (i.e. "PS4 Controller" or "XInput Controller")
     */
    public final String controllerType;

    /**
     * The x position of the left stick between -1 and 1
     */
    public final float leftStickX;

    /**
     * The y position of the left stick between -1 and 1
     */
    public final float leftStickY;

    /**
     * The x position of the right stick between -1 and 1
     */
    public final float rightStickX;

    /**
     * The y position of the right stick between -1 and 1
     */
    public final float rightStickY;

    /**
     * The angle of the left stick (for reference, 0 is right, 90 is up, 180 is left, 270 is down)
     */
    public final float leftStickAngle;

    /**
     * The amount the left stick is pushed in the current direction. This probably between 0 and 1,
     * but this can't be guaranteed due to weird gamepads (like the square holes on a Logitech Dual Action)
     */
    public final float leftStickMagnitude;

    /**
     * The angle of the right stick (for reference, 0 is right, 90 is up, 180 is left, 270 is down)
     */
    public final float rightStickAngle;

    /**
     * The amount the right stick is pushed in the current direction. This probably between 0 and 1,
     * but this can't be guaranteed due to weird gamepads (like the square holes on a Logitech Dual Action)
     */
    public final float rightStickMagnitude;

    /**
     * Whether or not the left stick is clicked in
     */
    public final boolean leftStickClick;

    /**
     * Whether or not the right stick is clicked in
     */
    public final boolean rightStickClick;

    /**
     * The position of the left trigger between 0 and 1
     */
    public final float leftTrigger;

    /**
     * The position of the right trigger between 0 and 1
     */
    public final float rightTrigger;

    /**
     * Whether or not the a button is pressed
     */
    public final boolean a;

    /**
     * Whether or not the b button is pressed
     */
    public final boolean b;

    /**
     * Whether or not the x button is pressed
     */
    public final boolean x;

    /**
     * Whether or not the y button is pressed
     */
    public final boolean y;

    /**
     * Whether or not the left bumper is pressed
     */
    public final boolean leftBumper;

    /**
     * Whether or not the right bumper is pressed
     */
    public final boolean rightBumper;

    /**
     * Whether or not the start button is pressed
     */
    public final boolean start;

    /**
     * Whether or not the back button is pressed
     */
    public final boolean back;

    /**
     * Whether or not the guide button is pressed. For some controller/platform combinations this
     * doesn't work. You probably shouldn't use this.
     */
    public final boolean guide;

    /**
     * Whether or not the up button on the dpad is pushed
     */
    public final boolean dpadUp;

    /**
     * Whether or not the down button on the dpad is pushed
     */
    public final boolean dpadDown;

    /**
     * Whether or not the left button on the dpad is pushed
     */
    public final boolean dpadLeft;

    /**
     * Whether or not the right button on the dpad is pushed
     */
    public final boolean dpadRight;

    /**
     * Return a controller state based on the current state of the passed controller.
     *
     * @param c The ControllerIndex object whose state should be read.
     */
    public ControllerState(ControllerIndex c) {
        isConnected = true;
        controllerType = c.getName();
        leftStickX = c.getAxisState(ControllerAxis.LEFTX);
        leftStickY = c.getAxisState(ControllerAxis.LEFTY);
        rightStickX = c.getAxisState(ControllerAxis.RIGHTX);
        rightStickY = c.getAxisState(ControllerAxis.RIGHTY);
        leftStickAngle = (float) Math.toDegrees(Math.atan2(leftStickY, leftStickX));
        leftStickMagnitude = (float) Math.sqrt((leftStickX * leftStickX) + (leftStickY * leftStickY));
        rightStickAngle = (float) Math.toDegrees(Math.atan2(rightStickY, rightStickX));
        rightStickMagnitude = (float) Math.sqrt((rightStickX * rightStickX) + (rightStickY * rightStickY));
        leftStickClick = c.isButtonPressed(ControllerButton.LEFTSTICK);
        rightStickClick = c.isButtonPressed(ControllerButton.RIGHTSTICK);
        leftTrigger = c.getAxisState(ControllerAxis.TRIGGERLEFT);
        rightTrigger = c.getAxisState(ControllerAxis.TRIGGERRIGHT);
        a = c.isButtonPressed(ControllerButton.A);
        b = c.isButtonPressed(ControllerButton.B);
        x = c.isButtonPressed(ControllerButton.X);
        y = c.isButtonPressed(ControllerButton.Y);
        leftBumper = c.isButtonPressed(ControllerButton.LEFTBUMPER);
        rightBumper = c.isButtonPressed(ControllerButton.RIGHTBUMPER);
        start = c.isButtonPressed(ControllerButton.START);
        back = c.isButtonPressed(ControllerButton.BACK);
        guide = c.isButtonPressed(ControllerButton.GUIDE);
        dpadUp = c.isButtonPressed(ControllerButton.DPAD_UP);
        dpadDown = c.isButtonPressed(ControllerButton.DPAD_DOWN);
        dpadLeft = c.isButtonPressed(ControllerButton.DPAD_LEFT);
        dpadRight = c.isButtonPressed(ControllerButton.DPAD_RIGHT);
    }

    /**
     * Return an empty controller state for disconnected controllers.
     */
    public ControllerState() {
        isConnected = false;
        controllerType = "Not Connected";
        leftStickX = 0;
        leftStickY = 0;
        rightStickX = 0;
        rightStickY = 0;
        leftStickAngle = 0;
        leftStickMagnitude = 0;
        rightStickAngle = 0;
        rightStickMagnitude = 0;
        leftStickClick = false;
        rightStickClick = false;
        leftTrigger = 0;
        rightTrigger = 0;
        a = false;
        b = false;
        x = false;
        y = false;
        leftBumper = false;
        rightBumper = false;
        start = false;
        back = false;
        guide = false;
        dpadUp = false;
        dpadDown = false;
        dpadLeft = false;
        dpadRight = false;
    }
}
