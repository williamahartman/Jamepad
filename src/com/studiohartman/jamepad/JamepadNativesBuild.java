package com.studiohartman.jamepad;

import com.badlogic.gdx.jnigen.*;
import com.badlogic.gdx.jnigen.BuildTarget.TargetOs;

import java.io.File;

class JamepadNativesBuild {
    //Listing needed source files for each platform
    private static String[] COMMON_SRC = {
            "*.*",
            "SDL2-2.0.4/src/*.*",
            "SDL2-2.0.4/src/stdlib/*.*",
            "SDL2-2.0.4/src/atomic/*.*",
            "SDL2-2.0.4/src/events/*.*",
            "SDL2-2.0.4/src/file/*.*",
            "SDL2-2.0.4/src/haptic/*.*",
            "SDL2-2.0.4/src/joystick/*.*",
            "SDL2-2.0.4/src/thread/*.*",
            "SDL2-2.0.4/src/timer/*.*",
            "SDL2-2.0.4/src/video/*.*",
            "SDL2-2.0.4/src/video/dummy/*.*",
    };
    private static String[] WINDOWS_SRC = {
            "SDL2-2.0.4/src/cpuinfo/*.*",
            "SDL2-2.0.4/src/thread/windows/*.*",
            "SDL2-2.0.4/src/core/windows/*.*",
            "SDL2-2.0.4/src/haptic/windows/*.*",
            "SDL2-2.0.4/src/joystick/windows/*.*",
            "SDL2-2.0.4/src/loadso/windows/*.*",
            "SDL2-2.0.4/src/timer/windows/*.*",
            "SDL2-2.0.4/src/render/*.*",
    };
    private static String WINDOWS_CONFIG_COMMAND = "./cross-configure.sh";
    private static String WINDOWS_CONFIG_ARGS = " --disable-audio --disable-render --disable-video --disable-power" +
            " --disable-filesystem --disable-assembly";

    private static String[] LINUX_SRC = {
            "SDL2-2.0.4/src/core/linux/*.*",
            "SDL2-2.0.4/src/haptic/linux/*.*",
            "SDL2-2.0.4/src/joystick/linux/*.*",
            "SDL2-2.0.4/src/loadso/dlopen/*.*",
            "SDL2-2.0.4/src/thread/pthread/*.*",
            "SDL2-2.0.4/src/timer/unix/*.*",
    };
    private static String LINUX_CONFIG_COMMAND = "./configure";
    private static String LINUX_CONFIG_ARGS = " --disable-audio --disable-render --disable-power --disable-filesystem " +
            "--disable-cpuinfo --disable-assembly --disable-dbus --disable-ibus --disable-video-x11 " +
            "--disable-video-wayland --disable-video-mir --disable-video-opengl --disable-video-opengles " +
            "--disable-video-opengles1 --disable-video-opengles2";
    private static String[] MAC_SRC = {
            "SDL2-2.0.4/src/cpuinfo/*.*",
            "SDL2-2.0.4/src/file/cocoa/*.*",
            "SDL2-2.0.4/src/haptic/darwin/*.*",
            "SDL2-2.0.4/src/joystick/darwin/*.*",
            "SDL2-2.0.4/src/loadso/dlopen/*.*",
            "SDL2-2.0.4/src/thread/pthread/*.*",
            "SDL2-2.0.4/src/timer/unix/*.*", 
            "SDL2-2.0.4/src/render/*.*",
            "SDL2-2.0.4/src/video/cocoa/*.*",
    };
    private static String OSX_CONFIG_COMMAND = "./configure";
    private static String OSX_CONFIG_ARGS = " --disable-audio --disable-render --disable-power --disable-filesystem " +
            "--disable-cpuinfo --disable-assembly";

    private static String[] INCLUDES = new String[] {"include", "SDL2-2.0.4/include", "SDL2-2.0.4/src"};
    private static String[] EXCLUDES = {"SDL2-2.0.4/**/*.cpp"};

    private static String[] merge(String[] a, String ... b) {
        String[] n = new String[a.length + b.length];
        System.arraycopy(a, 0, n, 0, a.length);
        System.arraycopy(b, 0, n, a.length, b.length);
        return n;
    }

	public static void main(String[] args) throws Exception {
        //Deal with arguments
        boolean useSystemSDL = false;
        boolean buildWindows = false;
        boolean buildLinux = false;
        boolean buildOSX = false;

        for(String s: args) {
            switch (s) {
                case "system-SDL2":
                    useSystemSDL = true;
                    break;
                case "build-windows":
                    buildWindows = true;
                    break;
                case "build-linux":
                    buildLinux = true;
                    break;
                case "build-OSX":
                    buildOSX = true;
                    break;
            }
        }

        System.out.println("Using system SDL     (arg: system-SDL2)   " + (useSystemSDL ? "ON" : "OFF"));
        System.out.println("Building for Windows (arg: build-windows) " + (buildWindows ? "ON" : "OFF"));
        System.out.println("Building for Linux   (arg: build-linux)   " + (buildLinux ? "ON" : "OFF"));
        System.out.println("Building for OSX     (arg: build-OSX)     " + (buildOSX ? "ON" : "OFF"));
        System.out.println();

        File sdlSrcDir = new File("jni/SDL2-2.0.4");

        //Windows build configs
        BuildTarget win32 = BuildTarget.newDefaultTarget(TargetOs.Windows, false);
        BuildTarget win64 = BuildTarget.newDefaultTarget(TargetOs.Windows, true);

        win32.cIncludes = merge(COMMON_SRC, WINDOWS_SRC);
        win32.cppExcludes = EXCLUDES;
        win32.headerDirs = INCLUDES;
        win32.cFlags = "-c -Wall -O2 -mfpmath=sse -msse2 -fmessage-length=0 -m32 -g -O3 -DUSING_GENERATED_CONFIG_H";
        win32.linkerFlags = "-Wl,--kill-at -shared -m32 -static -static-libgcc -static-libstdc++";
        win32.libraries = "-lmingw32 -lm -ldinput8 -ldxguid -ldxerr8 -luser32 -lgdi32 -lwinmm -limm32 -lole32 -loleaut32 -lshell32 -lversion -luuid -static-libgcc";

        win64.cIncludes = merge(COMMON_SRC, WINDOWS_SRC);
        win64.cppExcludes = EXCLUDES;
        win64.headerDirs = INCLUDES;
        win64.cFlags = "-c -Wall -O2 -fmessage-length=0 -m64 -g -O3 -DUSING_GENERATED_CONFIG_H";
        win64.linkerFlags = "-Wl,--kill-at -shared -m64 -static -static-libgcc -static-libstdc++";
        win64.libraries = "-lmingw32 -lm -ldinput8 -ldxguid -ldxerr8 -luser32 -lgdi32 -lwinmm -limm32 -lole32 -loleaut32 -lshell32 -lversion -luuid -static-libgcc";

        //Linux build configs
        BuildTarget lin32 = BuildTarget.newDefaultTarget(TargetOs.Linux, false);
        BuildTarget lin64 = BuildTarget.newDefaultTarget(TargetOs.Linux, true);
        if(!useSystemSDL) {
            lin32.cIncludes = merge(COMMON_SRC, LINUX_SRC);
            lin32.cppExcludes = EXCLUDES;
            lin32.headerDirs = INCLUDES;
            lin32.cFlags = "-c -Wall -O2 -fmessage-length=0 -m32 -fPIC -DUSING_GENERATED_CONFIG_H";
            lin32.linkerFlags = "-shared -m32 -Wl,-wrap,memcpy";
            lin32.libraries = "-lm -lpthread -lrt";

            lin64.cIncludes = merge(COMMON_SRC, LINUX_SRC);
            lin64.cppExcludes = EXCLUDES;
            lin64.headerDirs = INCLUDES;
            lin64.cFlags = "-c -Wall -O2 -fmessage-length=0 -m64 -fPIC -DUSING_GENERATED_CONFIG_H";
            lin64.linkerFlags = "-shared -m64 -Wl,-wrap,memcpy";
            lin64.libraries = "-lm -lpthread -lrt";
        } else {
            lin32.cIncludes = new String[] {};
            lin32.cppExcludes = EXCLUDES;
            lin32.headerDirs = new String[] {"/usr/include/SDL2"};
            lin32.libraries = "-lSDL2";

            lin64.cIncludes = new String[] {};
            lin64.cppExcludes = EXCLUDES;
            lin64.headerDirs = new String[] {"/usr/include/SDL2"};
            lin64.libraries = "-lSDL2";
        }

        //OSX build configs
        BuildTarget mac32 = BuildTarget.newDefaultTarget(TargetOs.MacOsX, false);
        BuildTarget mac64 = BuildTarget.newDefaultTarget(TargetOs.MacOsX, true);

        mac32.cIncludes = merge(COMMON_SRC, MAC_SRC);
        mac32.cppExcludes = EXCLUDES;
        mac32.headerDirs = INCLUDES;
        mac32.cFlags = "-c -Wall -O2 -m32 -DFIXED_POINT -fmessage-length=0 -fPIC -DUSING_GENERATED_CONFIG_H";
        mac32.linkerFlags = "-shared -m32";
        mac32.libraries = "-lm -liconv  -Wl,-framework,ForceFeedback -lobjc -Wl,-framework,CoreVideo -Wl,-framework,Cocoa -Wl,-framework,Carbon -Wl,-framework,IOKit -Wl,-framework,CoreAudio -Wl,-framework,AudioToolbox -Wl,-framework,AudioUnit";

        mac64.cIncludes = merge(COMMON_SRC, MAC_SRC);
        mac64.cppExcludes = EXCLUDES;
        mac64.headerDirs = INCLUDES;
        mac64.cFlags = "-c -Wall -O2 -m64 -DFIXED_POINT -fmessage-length=0 -fPIC -DUSING_GENERATED_CONFIG_H";
        mac64.linkerFlags = "-shared -m64";
        mac64.libraries = "-lm -liconv  -Wl,-framework,ForceFeedback -lobjc -Wl,-framework,CoreVideo -Wl,-framework,Cocoa -Wl,-framework,Carbon -Wl,-framework,IOKit -Wl,-framework,CoreAudio -Wl,-framework,AudioToolbox -Wl,-framework,AudioUnit";

        //Generate native code, build scripts
        System.out.println("##### GENERATING NATIVE CODE AND BUILD SCRIPTS #####");
        new NativeCodeGenerator().generate("src", "build/classes/main", "jni");
        new AntScriptGenerator().generate(
                new BuildConfig("jamepad", "build/tmp", "libs", "jni"), win32, win64, lin32, lin64, mac32, mac64
        );
        System.out.println();

        //Build library for all platforms and bitnesses
        if (buildWindows) {
            System.out.println("##### COMPILING NATIVES FOR WINDOWS #####");

            //Configure for linux
            System.out.println("Configuring SDL for windows build...");
            System.out.println("Running: " + WINDOWS_CONFIG_COMMAND + WINDOWS_CONFIG_ARGS);
            Runtime.getRuntime()
                    .exec(WINDOWS_CONFIG_COMMAND + WINDOWS_CONFIG_ARGS, null, sdlSrcDir)
                    .waitFor();

            BuildExecutor.executeAnt("jni/build-windows32.xml", "-Dhas-compiler=true clean postcompile");
            BuildExecutor.executeAnt("jni/build-windows64.xml", "-Dhas-compiler=true clean postcompile");
            System.out.println();
        }
        if (buildLinux) {
            System.out.println("##### COMPILING NATIVES FOR LINUX #####");

            if(!useSystemSDL) {
                //Configure for linux
                System.out.println("Configuring SDL for linux build...");
                System.out.println("Running: " + LINUX_CONFIG_COMMAND + LINUX_CONFIG_ARGS);
                Runtime.getRuntime()
                        .exec(LINUX_CONFIG_COMMAND + LINUX_CONFIG_ARGS, null, sdlSrcDir)
                        .waitFor();
            }

            BuildExecutor.executeAnt("jni/build-linux32.xml", "-Dhas-compiler=true clean postcompile");
            BuildExecutor.executeAnt("jni/build-linux64.xml", "-Dhas-compiler=true clean postcompile");
            System.out.println();
        }
        if (buildOSX) {
            System.out.println("##### COMPILING NATIVES FOR OSX #####");

            //Configure for linux
            System.out.println("Configuring SDL for OSX build...");
            System.out.println("Running: " + OSX_CONFIG_COMMAND + OSX_CONFIG_ARGS);
            Runtime.getRuntime()
                    .exec(OSX_CONFIG_COMMAND + OSX_CONFIG_ARGS, null, sdlSrcDir)
                    .waitFor();

            BuildExecutor.executeAnt("jni/build-macosx32.xml", "-Dhas-compiler=true  clean postcompile");
            BuildExecutor.executeAnt("jni/build-macosx64.xml", "-Dhas-compiler=true  clean postcompile");
            System.out.println();
        }

        System.out.println("##### PACKING NATIVES INTO .JAR #####");
        BuildExecutor.executeAnt("jni/build.xml", "pack-natives");
	}
}