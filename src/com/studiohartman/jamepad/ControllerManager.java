package com.studiohartman.jamepad;

import com.badlogic.gdx.jnigen.JniGenSharedLibraryLoader;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

/**
 * This class handles basic stuff with initializing SDL and getting you the controllers to play with.
 */
public class ControllerManager {
    /*JNI

    #include "SDL.h"

    SDL_Event event;
    */

    private String mappingsPath;
    private boolean isInitialized;
    private Controller[] controllers;

    /**
     * Constructor. Uses built-in mappings from here: https://github.com/gabomdq/SDL_GameControllerDB
     */
    public ControllerManager() {
        this("gamecontrollerdb.txt");
    }

    /**
     * Constructor.
     *
     * @param mappingsPath The path to a file containing SDL controller mappings.
     */
    public ControllerManager(String mappingsPath) {
        this.mappingsPath = mappingsPath;
        isInitialized = false;
        controllers = new Controller[0];

        new JniGenSharedLibraryLoader().load("jamepad");
    }

    /**
     * Initialize the Controller library. This initialized loads the native library and initializes SDL
     * in the native code.
     *
     * @throws JamepadRuntimeException
     */
    public void initSDLGamepad() throws JamepadRuntimeException {
        //Initialize SDL
        if (!nativeInitSDLGamepad()) {
            throw new JamepadRuntimeException("Failed to initialize SDL in native method!");
        } else {
            isInitialized = true;
        }

        //Set controller mappings. The possible exception is caught, since stuff will still work ok
        //for most people if mapping aren't set.
        try {
            addMappingsFromFile(mappingsPath);
        } catch (IOException e) {
            System.err.println("Failed to load mapping with original location \"" + mappingsPath + "\"");
            e.printStackTrace();
        }

        //Connect and keep track of the controllers
        controllers = new Controller[getNumControllers()];
        for(int i = 0; i < controllers.length; i++) {
            controllers[i] = new Controller(i);
        }
    }
    private native boolean nativeInitSDLGamepad(); /*
        if (SDL_Init(SDL_INIT_EVENTS | SDL_INIT_JOYSTICK | SDL_INIT_GAMECONTROLLER | SDL_INIT_HAPTIC) != 0) {
            printf("NATIVE METHOD: SDL_Init failed: %s\n", SDL_GetError());
            return JNI_FALSE;
        }

        //We don't want any controller connections events (which are automatically generated at init)
        //since they interfere with us detecting new controllers, so we go through all events and clear them.
        while (SDL_PollEvent(&event));

        return JNI_TRUE;
    */

    /**
     * This method quits all the native stuff. Call it when you're done with Jamepad.
     */
    public void quitSDLGamepad() {
        for(Controller c: controllers) {
            c.close();
        }
        nativeCloseSDLGamepad();
        controllers = new Controller[0];
        isInitialized = false;
    }
    private native void nativeCloseSDLGamepad(); /*
        SDL_Quit();
    */

    /**
     * Return the state of a controller at the passed index. This is nice if you don't want to deal with
     * Controller objects and button codes and stuff.
     *
     * @param index The index of the controller to be checked
     * @return The state of the controller at the passed index.
     */
    public ControllerState getState(int index) {
        if(index < controllers.length) {
            return new ControllerState(controllers[index]);
        }
        return new ControllerState();
    }

    /**
     * Returns a the Controller object with the passed index (0 for p1, 1 for p2, etc.)
     *
     * @param index The index of the desired controller
     * @return The list of connected Jamepads
     * @throws JamepadRuntimeException
     */
    public Controller get(int index) {
        verifyInitialized();
        return controllers[index];
    }

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
     * Automatically refresh the controller list if a controller was connected or disconnected
     * since the last call to this method.
     */
    public void update() {
        try {
            if (nativeControllerConnectedOrDisconnected()) {
                int numControllers = getNumControllers();

                Controller[] newControllerArr = new Controller[numControllers];
                for (int i = 0; i < newControllerArr.length; i++) {
                    if(i < controllers.length) {
                        newControllerArr[i] = controllers[i];
                    } else {
                        newControllerArr[i] = new Controller(i);
                    }
                }
                controllers = newControllerArr;

                for (int i = 0; i < controllers.length; i++) {
                    controllers[i].reconnectController();
                }
            }
        } catch (JamepadRuntimeException e) {
            e.printStackTrace();
        }
    }
    private native boolean nativeControllerConnectedOrDisconnected(); /*
        SDL_JoystickUpdate();
        while (SDL_PollEvent(&event)) {
            if (event.type == SDL_JOYDEVICEADDED || event.type == SDL_JOYDEVICEREMOVED) {
                return JNI_TRUE;
            }
        }
        return JNI_FALSE;
    */

    /**
     * This method adds mappings held in the specified file. The file is copied to the temp folder so
     * that it can be read by the native code (if running from a .jar for instance)
     *
     * @param path The path to the file containing controller mappings.
     * @throws IOException
     * @throws JamepadRuntimeException
     */
    public void addMappingsFromFile(String path) throws IOException, JamepadRuntimeException {
        mappingsPath = path;

        /*
        Copy the file to a temp folder. SDL can't read files held in .jars, and that's probably how
        most people would use this library.
         */
        Path extractedLoc = FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"), path);
        Files.copy(ClassLoader.getSystemResourceAsStream(path), extractedLoc,
                StandardCopyOption.REPLACE_EXISTING);

        if(!nativeAddMappingsFromFile(extractedLoc.toString())) {
            throw new JamepadRuntimeException("Failed to set SDL controller mappings!");
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

    private boolean verifyInitialized() {
        if(!isInitialized) {
            throw new JamepadRuntimeException("SDL_GameController is not initialized!");
        }
        return true;
    }
}
