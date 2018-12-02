## Building Jamepad

1.  Clone the repo on Linux.  Run `./gradlew linuxNatives`
2.  The binaries for Windows are cross-compiled and so also need to be built on Linux (or MacOS).  Run `./gradlew windowsNatives`
3.  Clone the repo on a mac. Copy the files you just built (from the `libs` folder) to the mac.
4.  On the mac, run `./gradlew OSXNatives`
5.  Run `./gradlew dist` to generate a .jar file with all the dependencies bundled.

####  Linux build dependencies

The following packages (or equivalents) are needed:

```
ant
build-essential
sdl2-dev
```

If you've built C stuff for different platforms and bitnesses, you probably have all this stuff. If not, use your package manager to get them all. It should be something like this if you're on Ubuntu or Debian or whatever: 

```
sudo apt-get install ant build-essential sdl2-dev
```

sdl2-config must be in the path.

If your distro doesn't have an up to date version of SDL or you get errors, you can build it yourself from source:

```
./configure CFLAGS=-fPIC CPPFLAGS=-fPIC ; make ; sudo make install
```

If you want to make the binaries smaller you can disable parts of SDL you don't need with configure flags.  (We only make use of Joystick, GameController and Events systems).  However this is not tested.

#### Windows (cross compiled on Linux on MacOS) build dependencies

Linux:
```
sudo apt-get install mingw-w64
```
MacOS:
```
brew install mingw-w64  
```

You  need to install cross compiled Windows 32 and 64 bit versions of SDL, e.g.

```
./configure --host=i686-w64-mingw32 ; make ; sudo make install
./configure --host=x86_64-w64-mingw32 ; make ; sudo make install
```

sdl2-config is assumed to be in /usr/local/cross-tools/ if it is not found there you will need to edit JamepadNativesBuild.java with the correct path.

#### MacOS build dependencies
The OS X binaries currently must be built on OS X. You can build the Windows ones too with cross compiler.  It is probably possible to build the Linux binaries here too, but I haven't tried that out.

The dependencies are pretty much the same (ant, g++). These packages can be installed from homebrew.
