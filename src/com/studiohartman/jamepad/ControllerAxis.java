package com.studiohartman.jamepad;

/**
 * This is an enumerated type for controller axes.
 *
 * Things are a bit gross here because it needs to correspond with the
 * enum SDL_GameControllerAxis in SDL_gamecontroller.h.
 *
 * We skip the invalid axis one and the max one (Who would ever want to check those anyway?).
 * This means that we start with an index 0 instead of -1, so that's nice at least.
 *
 * Make sure that the indices of the included axis matches with the values in the
 * enum in native code. (i.e. that A is 0 in both, B is 1 in both, etc.).
 *
 * @author William Hartman
 */
public enum ControllerAxis {
    LEFTX,
    LEFTY,
    RIGHTX,
    RIGHTY,
    TRIGGERLEFT,
    TRIGGERRIGHT
}
