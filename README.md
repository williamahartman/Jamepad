# Jamepad
#### A better way to use gamepads in Java

Jamepad is a library for using gamepads in Java. It's based on SDL2 ([here](https://www.libsdl.org/)) and uses jnigen ([more info here](https://github.com/libgdx/libgdx/wiki/jnigen)). When this library sucks less, its gonna use [this](https://github.com/gabomdq/SDL_GameControllerDB) database of gamepad mappings.

#### Stuff You Should Know About Jamepad

- For now, when we build SDL, the  dynamic API stuff is disabled. This seems bad and should probably change. I just don't know how to get it to work right with JNI with it.

- Before you run the build method, run this in the directory with the right executable:

        ./configure --disable-audio --disable-video --disable-render --disable-power --disable-filesystem --disable-file --disable-loadso --disable-cpuinfo
I'm sure there's a nice way I could work this into the build scripts, but I haven't figured that out yet.

- On Linux, dependencies are:
  - libdbus
  - libibus
  - libevdev
  - libudev

#### Building Jamepad on Ubuntu
The following packages are needed:

        build-essentials 
        libc6-i386 
        libc6-dev-i386 
        g++-multilib
        g++-mingw-w64-i686 
        g++-mingw-w64-x86-64
        ant
       
If you've build C stuff for different platforms and bitnesses, you probably have all this stuff. If not, use your package manager to get them all: 
        
        sudo apt-get isntall build-essentials libc6-i386 libc6-dev-i386 g++-multilib g++-mingw-w64-i686 g++-mingw-w64-x86-64 ant
