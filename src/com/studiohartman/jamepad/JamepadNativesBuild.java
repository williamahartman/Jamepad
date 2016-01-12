package com.studiohartman.jamepad;

import com.badlogic.gdx.jnigen.*;
import com.badlogic.gdx.jnigen.BuildTarget.TargetOs;

import java.io.File;

public class JamepadNativesBuild {
    //Listing needed source files for each platform
    private static String[] COMMON_SRC = {
            "*.*",
            "SDL2-2.0.4/src/*.*",
            "SDL2-2.0.4/src/atomic/*.*",
            "SDL2-2.0.4/src/events/*.*",
            "SDL2-2.0.4/src/file/*.*",
            "SDL2-2.0.4/src/haptic/*.*",
            "SDL2-2.0.4/src/joystick/*.*",
            "SDL2-2.0.4/src/stdlib/*.*",
            "SDL2-2.0.4/src/thread/*.*",
            "SDL2-2.0.4/src/timer/*.*",
    };
    private static String[] WINDOWS_SRC = {
            "SDL2-2.0.4/src/core/windows/*.*",
            "SDL2-2.0.4/src/haptic/windows/*.*",
            "SDL2-2.0.4/src/joystick/windows/*.*",
            "SDL2-2.0.4/src/loadso/windows/*.*",
            "SDL2-2.0.4/src/thread/windows/*.*",
            "SDL2-2.0.4/src/timer/windows/*.*",
    };
    private static String[] LINUX_SRC = {
            "SDL2-2.0.4/src/core/linux/*.*",
            "SDL2-2.0.4/src/haptic/linux/*.*",
            "SDL2-2.0.4/src/joystick/linux/*.*",
            "SDL2-2.0.4/src/loadso/dlopen/*.*",
            "SDL2-2.0.4/src/thread/pthread/*.*",
            "SDL2-2.0.4/src/timer/unix/*.*",
    };
    private static String[] MAC_SRC = {
            "SDL2-2.0.4/src/haptic/darwin/*.*",
            "SDL2-2.0.4/src/joystick/darwin/*.*",
            "SDL2-2.0.4/src/loadso/dlopen/*.*",
            "SDL2-2.0.4/src/thread/pthread/*.*",
            "SDL2-2.0.4/src/timer/unix/*.*",
    };
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

        System.out.println("Using system SDL (arg: system-SDL2)       " + (useSystemSDL ? "ON" : "OFF"));
        System.out.println("Building for Windows (arg: build-windows) " + (buildWindows ? "ON" : "OFF"));
        System.out.println("Building for Linux (arg: build-linux)     " + (buildLinux ? "ON" : "OFF"));
        System.out.println("Building for OSX (arg: build-OSX)         " + (buildOSX ? "ON" : "OFF"));
        System.out.println();

        File f = new File("jni/SDL2-2.0.4");

        //Windows build configs
        BuildTarget win32 = BuildTarget.newDefaultTarget(TargetOs.Windows, false);
        BuildTarget win64 = BuildTarget.newDefaultTarget(TargetOs.Windows, true);

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
            BuildExecutor.executeAnt("jni/build-windows32.xml", "-v -Dhas-compiler=true clean postcompile");
            BuildExecutor.executeAnt("jni/build-windows64.xml", "-v -Dhas-compiler=true clean postcompile");
            System.out.println();
        }
        if (buildLinux) {
            System.out.println("##### COMPILING NATIVES FOR LINUX #####");

            if(!useSystemSDL) {
                //Configure for linux
                System.out.println("Configuring SDL for linux build...");
                Runtime.getRuntime()
                        .exec("./configure --disable-audio --disable-video --disable-render --disable-power " +
                              "--disable-filesystem --disable-cpuinfo --disable-assembly --disable-dbus --disable-ibus",
                                null, f)
                        .waitFor();
            }

            BuildExecutor.executeAnt("jni/build-linux32.xml", "-Dhas-compiler=true clean postcompile");
            BuildExecutor.executeAnt("jni/build-linux64.xml", "-Dhas-compiler=true clean postcompile");
            System.out.println();
        }
        if (buildOSX) {
            System.out.println("##### COMPILING NATIVES FOR OSX #####");

            BuildExecutor.executeAnt("jni/build-macosx32.xml", "-v -Dhas-compiler=true  clean postcompile");
            BuildExecutor.executeAnt("jni/build-macosx64.xml", "-v -Dhas-compiler=true  clean postcompile");
            System.out.println();
        }

        System.out.println("##### PACKING NATIVES INTO .JAR #####");
        BuildExecutor.executeAnt("jni/build.xml", "pack-natives");
	}
}