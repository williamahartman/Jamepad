#include <com.studiohartman.jamepad.Main.h>

//@line:12


    #include "SDL.h"
    static inline jboolean wrapped_Java_com_studiohartman_jamepad_Main_nativeInitSDLGamepad
(JNIEnv* env, jclass clazz, jstring obj_path, char* path) {

//@line:28

        if (SDL_Init(SDL_INIT_EVENTS | SDL_INIT_JOYSTICK | SDL_INIT_GAMECONTROLLER | SDL_INIT_HAPTIC) != 0) {
            printf("Could not init SDL\n");
            return JNI_FALSE;
        }

        if(SDL_GameControllerAddMappingsFromFile(path) < 0) {
            printf("Failed to load mappings from \"%s\"\n", path);
            return JNI_FALSE;
        }

        return JNI_TRUE;
    
}

JNIEXPORT jboolean JNICALL Java_com_studiohartman_jamepad_Main_nativeInitSDLGamepad(JNIEnv* env, jclass clazz, jstring obj_path) {
	char* path = (char*)env->GetStringUTFChars(obj_path, 0);

	jboolean JNI_returnValue = wrapped_Java_com_studiohartman_jamepad_Main_nativeInitSDLGamepad(env, clazz, obj_path, path);

	env->ReleaseStringUTFChars(obj_path, path);

	return JNI_returnValue;
}

JNIEXPORT jint JNICALL Java_com_studiohartman_jamepad_Main_getNumRollers(JNIEnv* env, jclass clazz) {


//@line:42

        return SDL_NumJoysticks();
    

}

