# Jamepad
#### A better way to use gamepads in Java

### This is very much a work in progress! The library currently works on Linux, but not on Windows or OSX.

Jamepad is a library for using gamepads in Java. It's based on SDL2 ([here](https://www.libsdl.org/)) and uses jnigen ([more info here](https://github.com/libgdx/libgdx/wiki/jnigen)). We also use [this](https://github.com/gabomdq/SDL_GameControllerDB) really nice database of gamepad mappings.

Other gamepad libraries are missing stuff developers need. For most libraries, XBox 360 controllers on windows are not properly supported. The libraries that do support Xbox 360 controllers are not cross platform. Jamepad is a cross platform library with support for pretty much any controller. Jamepad also has a large database of controllers mapping so developers don't need to waste time (and potentially money) building profiles for every variety of controller a user might have.

#### Stuff You Should Know About Jamepad

- Jamepad is based on SDL. Since SDL is a bit overkill for just gamepad stuff, we build a smaller version that only contains the SDL_GameController subsystem and its dependencies.

- For now, when we build SDL, the  dynamic API stuff is disabled. This seems bad and should probably change. I just don't know how to get it to work through JNI with that stuff enabled.

- On Linux, runtime dependencies are:
  - libevdev
  - libudev

#### Building Jamepad
1.  Run `JamepadNativesBuild.java` with the desired arguments.
The possible arguments are:
  - system-SDL2 - Use the version of SDL2 supplied with your system instead of building our own minimal version of SDL2.
  - build-windows - Build natives for the Windows platform.
  - build-linux - Build natives for the Linux platform.
  - build-osx - Build natives for the Mac OS X platform.
2.  run `gradle dist` to generate a .jar file with all the dependencies bundled.

#### Building Jamepad on Linux
Right now, Jamepad needs to be built on Linux. The binaries for Windows are cross-compiled.

The following packages (or equivalents) are needed:

        gradle
        ant
        build-essentials 
        libc6-i386 
        libc6-dev-i386 
        g++-multilib
        g++-mingw-w64-i686 
        g++-mingw-w64-x86-64
        
       
If you've built C stuff for different platforms and bitnesses, you probably have all this stuff. If not, use your package manager to get them all. It should be something like this if you're on Ubuntu or Debian or whatever: 
        
        sudo apt-get install ant gradle build-essential libc6-i386 libc6-dev-i386 g++-multilib g++-mingw-w64-i686 g++-mingw-w64-x86-64
