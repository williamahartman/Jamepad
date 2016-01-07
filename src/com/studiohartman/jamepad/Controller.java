package com.studiohartman.jamepad;

/**
 * Created by will on 1/7/16.
 */
public class Controller {
    /*JNI

    #include "SDL.h"

    SDL_GameController* pad;
    */

    private native void nativeConnectController(int index); /*
        pad = SDL_GameControllerOpen(index);
    */

    private native boolean nativeCheckButton(int index); /*
        SDL_GameControllerUpdate();
        return SDL_GameControllerGetButton(pad, (SDL_GameControllerButton) index);
    */

    private int index;

    public Controller(int index) {
        this.index = index;
        nativeConnectController(index);
    }

    public int getIndex() {
        return index;
    }

    public boolean checkButton(ControllerButton toCheck) {
        return nativeCheckButton(toCheck.ordinal() - 1);
    }
}
