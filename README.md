# Jamepad
#### A better way to use gamepads in Java

Jamepad is a library for using gamepads in Java. It's based on SDL2 ([here](https://www.libsdl.org/)) and uses jnigen ([more info here](https://github.com/libgdx/libgdx/wiki/jnigen)). We also use [this](https://github.com/gabomdq/SDL_GameControllerDB) really nice database of gamepad mappings.

#### Stuff You Should Know About Jamepad

- For now, when we build SDL, the  dynamic API stuff is disabled. This seems bad and should probably change. I just don't know how to get it to work right with JNI with it.

- Before you run the build method, run this in the directory with the right executable (jni/SDL2-2.0.4):

        ./configure --disable-audio --disable-video --disable-render --disable-power --disable-filesystem --disable-loadso --disable-cpuinfo
I'm sure there's a nice way I could work this into the build scripts, but I haven't figured that out yet.

- On Linux, dependencies are:
  - libdbus
  - libibus
  - libevdev
  - libudev

#### Building Jamepad on Linux
The following packages (or equivalents) are needed:

        gradle
        ant
        build-essentials 
        libc6-i386 
        libc6-dev-i386 
        g++-multilib
        g++-mingw-w64-i686 
        g++-mingw-w64-x86-64
        
       
If you've built C stuff for different platforms and bitnesses, you probably have all this stuff. If not, use your package manager to get them all. Something like this if you're on Ubuntu or Debian or whatever: 
        
        sudo apt-get isntall build-essentials libc6-i386 libc6-dev-i386 g++-multilib g++-mingw-w64-i686 g++-mingw-w64-x86-64 ant
