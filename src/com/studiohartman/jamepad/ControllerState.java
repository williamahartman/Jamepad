package com.studiohartman.jamepad;

/**
 * Created by will on 3/8/16.
 */
public class ControllerState {
    public final boolean isConnected;
    public final String controllerType;

    public final float leftStickX;
    public final float leftStickY;
    public final float rightStickX;
    public final float rightStickY;

    public final float leftStickAngle;
    public final float leftStickMagnitude;
    public final float rightStickAngle;
    public final float rightStickMagnitude;

    public final boolean leftStickClick;
    public final boolean rightStickClick;

    public final float leftTrigger;
    public final float rightTrigger;

    public final boolean a;
    public final boolean b;
    public final boolean x;
    public final boolean y;
    public final boolean leftBumper;
    public final boolean rightBumper;
    public final boolean start;
    public final boolean back;
    public final boolean guide;

    public final boolean dpadUp;
    public final boolean dpadDown;
    public final boolean dpadLeft;
    public final boolean dpadRight;

    /**
     * Return a controller state based on the current state of the passed controller.
     *
     * @param c The Controller object whose state should be read.
     */
    public ControllerState(Controller c) {
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
