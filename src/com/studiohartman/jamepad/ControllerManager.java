package com.studiohartman.jamepad;

import com.badlogic.gdx.jnigen.JniGenSharedLibraryLoader;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * This class handles basic stuff with initializing SDL and getting you the controllers to play with.
 */
public class ControllerManager {
    /*JNI

    #include "SDL.h"
    */

    private boolean isInitialized;

    /**
     * Constructor.
     */
    public ControllerManager() {
        isInitialized = false;
    }

    /**
     * Initialize the Controller library. This initialized loads the native library and initializes SDL
     * in the native code.
     *
     * @throws JamepadException
     */
    public void initSDLGamepad() throws JamepadException {
        new JniGenSharedLibraryLoader().load("jamepad");

        if (!nativeInitSDLGamepad()) {
            throw new JamepadException("Failed to initialize SDL in native method!");
        } else {
            isInitialized = true;
        }
    }
    private native boolean nativeInitSDLGamepad(); /*
        if (SDL_Init(SDL_INIT_EVENTS | SDL_INIT_JOYSTICK | SDL_INIT_GAMECONTROLLER | SDL_INIT_HAPTIC) != 0) {
            printf("NATIVE METHOD: SDL_Init failed: %s\n", SDL_GetError());
            return JNI_FALSE;
        }

        return JNI_TRUE;
    */

    /**
     * Initialize the Controller library. This initialized loads the native library and initializes SDL
     * in the native code.
     *
     * This method also sets controller mappings.
     *
     * @param path The path to the file containing controller mappings.
     * @throws IOException
     * @throws JamepadException
     */
    public void initSDLGamepad(String path) throws IOException, JamepadException {
        initSDLGamepad();
        addMappingsFromFile(path);
    }

    /**
     * This method adds mappings held in the specified file.
     *
     * @param path The path to the file containing controller mappings.
     * @throws IOException
     * @throws JamepadException
     */
    public void addMappingsFromFile(String path) throws IOException, JamepadException {
        /*
        Copy the file to a temp folder. SDL can't read files held in .jars, and that's probably how
        most people would use this library.
         */
        Path extractedLoc = FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"), path);
        Files.copy(ClassLoader.getSystemResourceAsStream(path), extractedLoc,
                StandardCopyOption.REPLACE_EXISTING);

        if(!nativeAddMappingsFromFile(extractedLoc.toString())) {
            throw new JamepadException("Failed to set SDL controller mappings!");
        }
    }
    private native boolean nativeAddMappingsFromFile(String path); /*
        if(SDL_GameControllerAddMappingsFromFile(path) < 0) {
            printf("NATIVE METHOD: Failed to load mappings from \"%s\"\n", path);
            printf("               %s\n", SDL_GetError());
            return JNI_FALSE;
        }

        return JNI_TRUE;
    */

    /**
     * Return the number of controllers that are connected.
     *
     * @return the number of connected controllers.
     * @throws JamepadRuntimeException
     */
    public int getNumControllers() {
        verifyInitialized();

        return nativeGetNumRollers();
    }
    private native int nativeGetNumRollers(); /*
        int numJoysticks = SDL_NumJoysticks();

        int numGamepads = 0;

        for(int i = 0; i < numJoysticks; i++) {
            if(SDL_IsGameController(i)) {
                numGamepads++;
            }
        }

        return numGamepads;
    */

    /**
     * Returns a Controller object for each currently connected SDL Gamepad
     *
     * @return The list of connected Jamepads
     * @throws JamepadRuntimeException
     */
    public Controller[] getControllers() {
        verifyInitialized();

        Controller[] result = new Controller[getNumControllers()];
        for(int i = 0; i < result.length; i++) {
            result[i] = new Controller(i);
        }

        return result;
    }

    private boolean verifyInitialized() {
        if(!isInitialized) {
            throw new JamepadRuntimeException("Controller has not been successfully initialized!");
        }
        return true;
    }
}
