package com.studiohartman.jamepad;

/**
 * This is an enumerated type for controller buttons.
 *
 * Things are a bit gross here because it needs to correspond with the
 * enum SDL_GameControllerButton in SDL_gamecontroller.h.
 *
 * We skip the invalid button one and the max one (Who would ever want to check those anyway?).
 * This means that we start with an index 0 instead of -1, so that's nice at least.
 *
 * Make sure that the indices of the included buttons matches with the values in the
 * enum in native code. (i.e. that A is 0 in both, B is 1 in both, etc.).
 *
 * @author William Hartman
 */
public enum ControllerButton {
    A,
    B,
    X,
    Y,
    BACK,
    GUIDE,
    START,
    LEFTSTICK,
    RIGHTSTICK,
    LEFTBUMPER,
    RIGHTBUMPER,
    DPAD_UP,
    DPAD_DOWN,
    DPAD_LEFT,
    DPAD_RIGHT,
}
