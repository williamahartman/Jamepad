# Jamepad
#### A better way to use gamepads in Java

Jamepad is a library for using gamepads in Java. It's based on SDL2 ([here](https://www.libsdl.org/)) and uses jnigen ([more info here](https://github.com/libgdx/libgdx/wiki/jnigen)). When this library sucks less, its gonna use [this](https://github.com/gabomdq/SDL_GameControllerDB) database of gamepad mappings.

#### Stuff You Should Know About Jamepad

- For now, when we build SDL, the  dynamic API stuff is disabled. This seems bad and should probably change. I just don't know how to get it to work right with JNI with it.

- Before you run the build method, run this in the directory with the right executable:

        ./configure --disable-audio --disable-video --disable-render --disable-power --disable-filesystem --disable-file --disable-loadso --disable-cpuinfo
I'm sure there's a nice way I could work this into the build scripts, but I haven't figured that out yet.
