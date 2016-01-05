package com.studiohartman.jamepad;

import com.badlogic.gdx.jnigen.JniGenSharedLibraryLoader;

public class Main {
    /*JNI

    #include "SDL.h"
    */

    static public native int add (int a, int b); /*
        return a + b;
    */

    static public native int getNumRollers(); /*
        if (SDL_Init(SDL_INIT_EVENTS | SDL_INIT_JOYSTICK | SDL_INIT_GAMECONTROLLER | SDL_INIT_HAPTIC) != 0) {
            printf("Could not init SDL\n");
            return 1;
        }

        return SDL_NumJoysticks();
    */

    public static void main (String[] args) throws Exception {
        new JniGenSharedLibraryLoader().load("jamepad");

        System.out.println("Basic test JNI Test: " + add(1, 2));
        System.out.println("SDL JNI Test:        " + getNumRollers());
    }
}
