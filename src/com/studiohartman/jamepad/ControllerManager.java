package com.studiohartman.jamepad;

import com.badlogic.gdx.jnigen.JniGenSharedLibraryLoader;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * This class handles initializing the native library, connecting to controllers, and managing the
 * list of controllers.
 *
 * Generally, after creating a ControllerManager object and calling initSDLGamepad() on it, you
 * would access the states of the attached gamepads by calling getState().
 *
 * For some applications (but probably very few), getState may have a performance impact. In this
 * case, it may make sense to use the getControllerIndex() method to access the objects used
 * internally by  ControllerManager.
 *
 * @author William Hartman
 */
public class ControllerManager {
    /*JNI

    #include "SDL.h"

    SDL_Event event;
    */

    private String mappingsPath;
    private boolean isInitialized;
    private ControllerIndex[] controllers;

    /**
     * Default constructor. Makes a manager for 4 controllers with the built in mappings from here:
     * https://github.com/gabomdq/SDL_GameControllerDB
     */
    public ControllerManager() {
        this(4, "gamecontrollerdb.txt");
    }

    /**
     * Constructor. Uses built-in mappings from here: https://github.com/gabomdq/SDL_GameControllerDB
     *
     * @param maxNumControllers The number of controllers this ControllerManager can deal with
     */
    public ControllerManager(int maxNumControllers) {
        this(maxNumControllers, "gamecontrollerdb.txt");
    }

    /**
     * Constructor.
     *
     * @param mappingsPath The path to a file containing SDL controller mappings.
     * @param maxNumControllers The number of controller this ControllerManager can deal with
     */
    public ControllerManager(int maxNumControllers, String mappingsPath) {
        this.mappingsPath = mappingsPath;
        isInitialized = false;
        controllers = new ControllerIndex[maxNumControllers];

        new JniGenSharedLibraryLoader().load("jamepad");
    }

    /**
     * Initialize the ControllerIndex library. This loads the native library and initializes SDL
     * in the native code.
     *
     * @throws IllegalStateException If the native code fails to initialize or if SDL is already initialized
     */
    public void initSDLGamepad() throws IllegalStateException {
        if(isInitialized) {
            throw new IllegalStateException("SDL is already initialized!");
        }

        //Initialize SDL
        if (!nativeInitSDLGamepad()) {
            throw new IllegalStateException("Failed to initialize SDL in native method!");
        } else {
            isInitialized = true;
        }

        //Set controller mappings. The possible exception is caught, since stuff will still work ok
        //for most people if mapping aren't set.
        try {
            addMappingsFromFile(mappingsPath);
        } catch (IOException e) {
            System.err.println("Failed to load mapping with original location \"" + mappingsPath + "\", " +
                    "Falling back of SDL's built in mappings");
            e.printStackTrace();
        }

        //Connect and keep track of the controllers
        for(int i = 0; i < controllers.length; i++) {
            controllers[i] = new ControllerIndex(i);
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
        for(ControllerIndex c: controllers) {
            c.close();
        }
        nativeCloseSDLGamepad();
        controllers = new ControllerIndex[0];
        isInitialized = false;
    }
    private native void nativeCloseSDLGamepad(); /*
        SDL_Quit();
    */

    /**
     * Return the state of a controller at the passed index. This is probably the way most people
     * should use this library. It's simpler and less verbose, and controller connections and
     * disconnections are automatically handled.
     *
     * Also, no exceptions are thrown here (unless Jamepad isn't initialized), so you don't need
     * to have a million try/catches or anything.
     *
     * The returned state is immutable. This means an object is allocated every time you call this
     * (unless the controller is disconnected). This shouldn't be a big deal (even for games) if your
     * GC is tuned well, but if this is a problem for you, you can go directly through the internal
     * ControllerIndex objects using getControllerIndex().
     *
     * update() is called each time this method is called. Buttons are also queried, so values
     * returned from isButtonJustPressed() in ControllerIndex may not be what you expect. Calling
     * this method will have side effects if you are using the ControllerIndex objects yourself.
     * This should be fine unless you are mixing and matching this method with ControllerIndex
     * objects, which you probably shouldn't do anyway.
     *
     * @param index The index of the controller to be checked
     * @return The state of the controller at the passed index.
     * @throws IllegalStateException if Jamepad was not initialized
     */
    public ControllerState getState(int index) throws IllegalStateException {
        verifyInitialized();

        if(index < controllers.length && index >= 0) {
            update();
            return ControllerState.getInstanceFromController(controllers[index]);
        } else {
            return ControllerState.getDisconnectedControllerInstance();
        }
    }

    /**
     * Starts vibrating the controller at this given index. If this fails for one reason or another (e.g.
     * the controller at that index doesn't support haptics, or if there is no controller at that index),
     * this method will return false.
     *
     * @param index The index of the controller that will be vibrated
     * @param leftMagnitude The magnitude the left vibration motor will be set to
     * @param rightMagnitude The magnitude the right vibration motor will be set to
     * @return Whether or not vibration was successfully started
     * @throws IllegalStateException if Jamepad was not initialized
     */
    public boolean startVibration(int index, float leftMagnitude, float rightMagnitude) throws IllegalStateException {
        verifyInitialized();

        if(index < controllers.length && index < 0) {
            try {
                return controllers[index].startVibration(leftMagnitude, rightMagnitude);
            } catch (ControllerUnpluggedException e) {
                return false;
            }
        }

        return false;
    }

    /**
     * Stops any running vibration effects on the controller at the given index. If there is no
     * controller or there is another problem, this method will do nothing.
     *
     * @param index The index of the controller whose vibration effects will be stopped
     */
    public void stopVibration(int index) {
        verifyInitialized();

        if(index < controllers.length && index < 0) {
            controllers[index].stopVibration();
        }
    }

    /**
     * Returns a the ControllerIndex object with the passed index (0 for p1, 1 for p2, etc.).
     *
     * You should only use this method if you're worried about the object allocations from getState().
     * If you decide to do things this way, your code will be a good bit more verbose and you'll
     * need to deal with potential exceptions.
     *
     * It is generally safe to store objects returned from this method. They will only change internally
     * if you call quitSDLGamepad() followed by a call to initSDLGamepad().
     *
     * Calling update() will run through all the controllers to check for newly plugged in or unplugged
     * controllers. You could do this from your code, but keep that in mind.
     *
     * @param index the index of the ControllerIndex that will be returned
     * @return The internal ControllerIndex object for the passed index.
     * @throws IllegalStateException if Jamepad was not initialized
     */
    public ControllerIndex getControllerIndex(int index) {
        verifyInitialized();
        return controllers[index];
    }

    /**
     * Return the number of controllers that are actually connected. This may disagree with
     * the ControllerIndex objects held in here if something has been plugged in or unplugged
     * since update() was last called.
     *
     * @return the number of connected controllers.
     * @throws IllegalStateException if Jamepad was not initialized
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
     * Refresh the connected controllers in the controller list if something has been connected or
     * unplugged.
     *
     * If there hasn't been a change in whether controller are connected or not, nothing will happen.
     *
     * @throws IllegalStateException if Jamepad was not initialized
     */
    public void update() {
        verifyInitialized();
        if (nativeControllerConnectedOrDisconnected()) {
            for (int i = 0; i < controllers.length; i++) {
                controllers[i].reconnectController();
            }
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
     * @throws IOException if the file cannot be read, copied to a temp folder, or deleted.
     * @throws IllegalStateException if the mappings cannot be applied to SDL
     */
    public void addMappingsFromFile(String path) throws IOException, IllegalStateException {
        mappingsPath = path;

        /*
        Copy the file to a temp folder. SDL can't read files held in .jars, and that's probably how
        most people would use this library.
         */
        Path extractedLoc = FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"), path);
        Files.copy(ClassLoader.getSystemResourceAsStream(path), extractedLoc,
                StandardCopyOption.REPLACE_EXISTING);

        if(!nativeAddMappingsFromFile(extractedLoc.toString())) {
            throw new IllegalStateException("Failed to set SDL controller mappings! Falling back to build in SDL mappings.");
        }

        Files.delete(extractedLoc);
    }
    private native boolean nativeAddMappingsFromFile(String path); /*
        if(SDL_GameControllerAddMappingsFromFile(path) < 0) {
            printf("NATIVE METHOD: Failed to load mappings from \"%s\"\n", path);
            printf("               %s\n", SDL_GetError());
            return JNI_FALSE;
        }

        return JNI_TRUE;
    */

    private boolean verifyInitialized() throws IllegalStateException {
        if(!isInitialized) {
            throw new IllegalStateException("SDL_GameController is not initialized!");
        }
        return true;
    }
}
