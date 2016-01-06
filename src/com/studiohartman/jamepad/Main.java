package com.studiohartman.jamepad;

import com.badlogic.gdx.jnigen.JniGenSharedLibraryLoader;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Main {
    /*JNI

    #include "SDL.h"
    */

    public static void initSDLGamepad(String path) throws IOException, Exception {
        new JniGenSharedLibraryLoader().load("jamepad");

        Path extractedLoc = FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"), path);
        Files.copy(ClassLoader.getSystemResourceAsStream(path), extractedLoc,
                StandardCopyOption.REPLACE_EXISTING);
        
        if(!nativeInitSDLGamepad(extractedLoc.toString())) {
            throw new JamepadException("Failed to initialize Jamepad!");
        }
    }
    private static native boolean nativeInitSDLGamepad(String path); /*
        if (SDL_Init(SDL_INIT_EVENTS | SDL_INIT_JOYSTICK | SDL_INIT_GAMECONTROLLER | SDL_INIT_HAPTIC) != 0) {
            printf("Could not init SDL\n");
            return JNI_FALSE;
        }

        if(SDL_GameControllerAddMappingsFromFile(path) < 0) {
            printf("Failed to load mappings from \"%s\"\n", path);
            return JNI_FALSE;
        }

        return JNI_TRUE;
    */

    public static native int getNumRollers(); /*
        return SDL_NumJoysticks();
    */

    public static void main (String[] args) throws Exception {
        initSDLGamepad("gamecontrollerdb.txt");

        System.out.println("SDL JNI Test (Number of connected controllers): " + getNumRollers());
    }
}
