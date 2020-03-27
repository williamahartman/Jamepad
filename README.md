# News

[sdl2gdx](https://github.com/electronstudio/sdl2gdx) is a successor project by the maintainer of Jamepad.

Jamepad still exists as an API but it now depends on [sdl2gdx](https://github.com/electronstudio/sdl2gdx).  The downside of this is that you have
to add one additional dependency.  The upside is that you can upgrade to the latest version of SDL without waiting
for a new release of Jamepad just by updating the dependency.

# Jamepad

#### A better way to use gamepads in Java

Jamepad is a library for using gamepads in Java. It's based on SDL via [sdl2gdx](https://github.com/electronstudio/sdl2gdx).

Other gamepad libraries are missing stuff developers need. For most libraries, Xbox 360 controllers on windows are not properly supported. The libraries that do support Xbox 360 controllers are not cross platform. On some, hotplugging controllers is not supported.

Jamepad has:
  - One library that supports all platforms (Windows, OSX, and Linux)
  - XInput support on Windows for full Xbox 360 controller support.
  - Support for plugging/unplugging controllers at runtime.
  - Support for rumble
  - Button/Axis mappings for popular controllers.
  - A permissive license. You can use this library in proprietary projects without sharing source.

#### Stuff You Should Know About Jamepad

- On Windows (only 7 and up were tested), no special dependencies are needed.
- On Linux, runtime dependencies are:
  - libevdev
  - libudev
- On OS X, no special dependencies are needed
  - If you want to use Xbox controllers, you need separate drivers for them. The ones [here](https://github.com/360Controller/360Controller) have been tested with Jamepad and work properly.
  
#### Current Limitations
- The order of gamepads on Windows is not necessarily the order they were plugged in. XInput controllers will always appear before DirectInput controllers, regardless of when they were plugged in. This means that the player numbers associated with each controller can change unexpectedly if XInput controllers are plugged in or disconnected while DirectInput controllers are present.
- If using getState() in ControllerManager, a new ControllerState is instantiated on each call. For some games, this could pose a problem.
- If using ControllerIndex have to check for exceptions when controllers disconnected.
- Joysticks that are not controllers not supported.
- Note: you can use [sdl2gdx](https://github.com/electronstudio/sdl2gdx) directly without Jamepad to overcome these limitations.


## Using Jamepad

### Getting Jamepad

##### gradle
If you use gradle, you can pull this package in from jitpack.  First, add to your repositories section:
````
repositories {
  ...
  jcenter()
  maven { url "https://jitpack.io" }
}
````
Next, add to your dependencies section. Update the version number to whatever the latest release is.
````
dependencies {
  ...
  compile 'com.github.WilliamAHartman:Jamepad:1.4.0'
  compile 'uk.co.electronstudio.sdl2gdx:sdl2gdx:1.0.4-beta3'
}
````
##### maven
If you use maven, you can pull this package in from jitpack.  First, add jitpack to your repositories section:
````
<repositories>
    ...
    <repository>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <id>central</id>
        <name>bintray</name>
        <url>http://jcenter.bintray.com</url>
    </repository>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
````
Next, add this line to your dependencies section. Update the version number to whatever the latest release is.
````
<dependencies>
    ...
    <dependency>
        <groupId>com.github.WilliamAHartman</groupId>
        <artifactId>Jamepad</artifactId>
        <version>1.4.0</version>
    </dependency>
    <dependency>
        <groupId>uk.co.electronstudio.sdl2gdx/groupId>
        <artifactId>sdl2gdx</artifactId>
        <version>1.0.4-beta3/version>
    </dependency>
</dependencies>
````
##### jar
If you aren't using gradle, just download the .jar file from the releases section and add it to your project as usual.

#### Using Jamepad
There are two main ways to use Jamepad. Both rely on a ControllerManager Object.

```java
ControllerManager controllers = new ControllerManager();
controllers.initSDLGamepad();
```

For most applications, using the getState() method in ControllerManager is best. This method returns an immutable ControllerState object that describes the state of the controller at the instant the method is called. Using this method, you don't need to litter code with a bunch of exceoption handling or handle the possiblity of controller disconnections at weird times. 

If a controller is disconnected, the returned ControllerState object has the isConnected field set to false. All other fields are either false (for buttons) or 0 (for axes).

Here's a simple example:

```java
//Print a message when the "A" button is pressed. Exit if the "B" button is pressed 
//or the controller disconnects.
while(true) {
  ControllerState currState = controllers.getState(0);
  
  if(!currState.isConnected || currState.b) {
    break;
  }
  if(currState.a) {
    System.out.println("\"A\" on \"" + currState.controllerType + "\" is pressed");
  }
}
```

For a select few applications, getState() might not be the best decision. Since ControllerState is immutable, a new one is instantiated on each call to getState(). This should be fine for normal desktop JVMs; both Oracle's JVM and the OpenJDK one should absolutely be able to handle this. What problems do come up could probably be solved with some GC tuning.

If these allocations do end up being an actual problem, you can access the internal representation of the controllers. This is more complicated to use, and you might need to deal with some exceptions.

Here's a pretty barebones example:

```java
//Print a message when the "A" button is pressed. Exit if the "B" button is pressed 
//or the controller disconnects.
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
  } catch (ControllerUnpluggedException e) {   
    break;
  }
}
```

When you're finished with your gamepad stuff, you should call quitSDLGamepad() to free the native library.

```java
controllers.quitSDLGamepad();
```

## Building Jamepad

See [BUILDING](BUILDING.md)
