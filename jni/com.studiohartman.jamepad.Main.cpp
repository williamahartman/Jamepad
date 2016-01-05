#include <com.studiohartman.jamepad.Main.h>

//@line:6


    #include "SDL.h"
    JNIEXPORT jint JNICALL Java_com_studiohartman_jamepad_Main_add(JNIEnv* env, jclass clazz, jint a, jint b) {


//@line:11

        return a + b;
    

}

JNIEXPORT jint JNICALL Java_com_studiohartman_jamepad_Main_getNumRollers(JNIEnv* env, jclass clazz) {


//@line:15

        if (SDL_Init(SDL_INIT_EVENTS | SDL_INIT_JOYSTICK | SDL_INIT_GAMECONTROLLER | SDL_INIT_HAPTIC) != 0) {
            printf("Could not init SDL\n");
            return 1;
        }

        return SDL_NumJoysticks();
    

}

