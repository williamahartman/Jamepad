# Jamepad
#### A better way to use gamepads in Java

### This is very much a work in progress! The library currently works on Linux and Windows, but not on OSX.

Jamepad is a library for using gamepads in Java. It's based on SDL2 ([here](https://www.libsdl.org/)) and uses jnigen ([more info here](https://github.com/libgdx/libgdx/wiki/jnigen)). We also use [this](https://github.com/gabomdq/SDL_GameControllerDB) really nice database of gamepad mappings.

Other gamepad libraries are missing stuff developers need. For most libraries, Xbox 360 controllers on windows are not properly supported. The libraries that do support Xbox 360 controllers are not cross platform. On some, hotplugging controllers is not supported.

Jamepad has:
  - One library that supports all platforms (Windows, OSX, and Linux), and is easy to port to others.
  - XInput support on windows for full Xbox 360 controller support. If we're being honest that's the most important controller/platform combo.
  - Support for plugging/unplugging controllers at runtime.
  - Button/Axis mappings for popular controllers.

#### Stuff You Should Know About Jamepad

- Jamepad is based on SDL. Since SDL is a bit overkill for just gamepad stuff, we build a smaller version that only contains the SDL_GameController subsystem and its dependencies.
- On Linux, runtime dependencies are:
  - libevdev
  - libudev
  
#### Using Jamepad

There are two main ways to use Jamepad. Both rely on a ControllerManager Object.

```java
ControllerManager controllers = new ControllerManager();
controllers.initSDLGamepad();
```

For most applications, using the getState() method is simplest. This method returns an immutable ControllerState object that describes the state of the controller at the instant the method is called. Here's a simple example:

```java
while(true) {
  ControllerState currState = controllers.getState(0);

  if(currState.a) {
    System.out.println("\"A\" on \"" + currState.controllerType + "\" is pressed");
  }
  
  if(currState.b || !currState.isConnected) {
    break;
  }
}
```

For some applications, getState() might not be the best decision since all the object allocations can certainly add up. If this is a problem, you can access the internal representation of the controllers. This is more complicated to use, and you might need to deal with some exceptions. Here's a pretty barebones example:

```java
ControllerIndex currController = controllers.getControllerIndex(0);

while(true) {
  controllers.update();

  try {
    if(currController.isButtonPressed(ControllerButton.A)) {
      System.out.println("\"A\" on \"" + currController.getName() + "\" is pressed");
    }

    if(currController.isButtonPressed(ControllerButton.B)) {
      break;
    }
  } catch (JamepadRuntimeException e) {
    break;
  }
}
```
        
When you're finished with your gamepad stuff, you should call quitSDLGamepad() to free the native library.
    
        controllers.quitSDLGamepad();

#### Current Limitations

- Jamepad does not work on Mac OSX yet
- If using getState() in ControllerManager, a new ControllerState is instantiated on each call. For some games, this could pose a problem.
- The order of gamepads in windows is not necessarily the order they were plugged in. XInput controllers are always moved to the front of the list. This means that the player numbers associated with each controller can change unexpectedly if controllers are plugged in or disconnected.
- For now, when we build SDL, the  dynamic API stuff is disabled. This seems bad and should probably change. I just don't know how to get it to work through JNI with that stuff enabled.

#### Building Jamepad
1.  run `gradle windowsNatives`
2.  run `gradle linuxNatives`
2.  run `gradle dist` to generate a .jar file with all the dependencies bundled.

#### Building Jamepad on Linux
Right now, Jamepad needs to be built on Linux. The binaries for Windows are cross-compiled.

The following packages (or equivalents) are needed:

```
gradle
ant
build-essentials 
libc6-i386 
libc6-dev-i386 
g++-multilib
g++-mingw-w64-i686 
g++-mingw-w64-x86-64
```

If you've built C stuff for different platforms and bitnesses, you probably have all this stuff. If not, use your package manager to get them all. It should be something like this if you're on Ubuntu or Debian or whatever: 

```
sudo apt-get install ant gradle build-essential libc6-i386 libc6-dev-i386 g++-multilib g++-mingw-w64-i686 g++-mingw-w64-x86-64
```
