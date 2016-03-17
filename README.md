# Jamepad
#### A better way to use gamepads in Java

### This is very much a work in progress! The library currently works on Linux and Windows, but not on OSX.

Jamepad is a library for using gamepads in Java. It's based on SDL2 ([here](https://www.libsdl.org/)) and uses jnigen ([more info here](https://github.com/libgdx/libgdx/wiki/jnigen)). We also use [this](https://github.com/gabomdq/SDL_GameControllerDB) really nice database of gamepad mappings.

Other gamepad libraries are missing stuff developers need. For most libraries, Xbox 360 controllers on windows are not properly supported. The libraries that do support Xbox 360 controllers are not cross platform. On some, hotplugging controllers is not supported.

Jamepad has:
  - One library that supports all platforms (Windows, OSX, and Linux), and is easy to port to others.
  - XInput support on Windows for full Xbox 360 controller support.
  - Support for plugging/unplugging controllers at runtime.
  - Button/Axis mappings for popular controllers.

#### Stuff You Should Know About Jamepad

- Jamepad is based on SDL. Since SDL is a bit overkill for just gamepad stuff, we build a smaller version that only contains the SDL_GameController subsystem and its dependencies.
- On Linux, runtime dependencies are:
  - libevdev
  - libudev
  
#### Current Limitations

- Jamepad does not work on Mac OSX yet
- There are some problems Jamepad just can't fix. Xbox controller support on OSX and Linux are still kind of iffy. The 360 Wireless Adapter is a mess on Linux without weird kernel modules or userspace drivers. The Xbox One Wireless adapter isn't currently supported at all on Linux and OSX. 
- The order of gamepads in Windows is not necessarily the order they were plugged in. XInput controllers are always moved to the front of the list. This means that the player numbers associated with each controller can change unexpectedly if controllers are plugged in or disconnected.
- If using getState() in ControllerManager, a new ControllerState is instantiated on each call. For some games, this could pose a problem.
- For now, when we build SDL, the  dynamic API stuff is disabled. This seems bad and should probably change. I just don't know how to get it to work through JNI with that stuff enabled.
  
## Using Jamepad

There are two main ways to use Jamepad. Both rely on a ControllerManager Object.

```java
ControllerManager controllers = new ControllerManager();
controllers.initSDLGamepad();
```

For most applications, using the getState() method in ControllerManager is best. This method returns an immutable ControllerState object that describes the state of the controller at the instant the method is called. Using this method, you don't need to litter code with a bunch of exceoption handling or handle the possiblity of controller disconnections at weird times. 

If a controller is disconnected, the returned ControllerState object has the isConnected field set to false. All other fields are either false (for buttons) or 0 (for axes).

Here's a simple example:

```java
//Print a message when the "A" button is pressed. Exit if the "B" button is pressed or the controller disconnects.
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

For a select few applications, getState() might not be the best decision. Since ControllerState is immutable, a new one is instantiated on each call to getState(). This should be fine for normal desktop JVMs; both Oracle's JVM and the OpenJDK one should absolutely be able to handle this. What problems do come up could probably be solved with some GC tuning.

If these allocations do end up being an actual problem, you can access the internal representation of the controllers. This is more complicated to use, and you might need to deal with some exceptions.

Here's a pretty barebones example:

```java
//Print a message when the "A" button is pressed. Exit if the "B" button is pressed or the controller disconnects.
ControllerIndex currController = controllers.getControllerIndex(0);

while(true) {
  controllers.update(); //If using ControllerIndex, you should call update() to check if a new controller
                        //was plugged in or unplugged at this index.

  try {
    if(currController.isButtonPressed(ControllerButton.A)) {
      System.out.println("\"A\" on \"" + currController.getName() + "\" is pressed");
    }

    if(currController.isButtonPressed(ControllerButton.B)) {
      break;
    }
  } catch (JamepadRuntimeException e) {   //Deal with exceptions thrown if currController isn't connnected
    break;
  }
}
```

When you're finished with your gamepad stuff, you should call quitSDLGamepad() to free the native library.

```java
controllers.quitSDLGamepad();
```

## Building Jamepad
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
