package com.studiohartman.jamepad;

import com.badlogic.gdx.jnigen.*;
import com.badlogic.gdx.jnigen.BuildTarget.TargetOs;

import java.io.File;
import java.io.FileNotFoundException;

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

        String sdl = "0";
        try {
            sdl = execCmd("sdl2-config --version").trim();
        } catch (Exception e){
            System.out.println("SDL must be installed and sdl2-config command must be on path.");
            e.printStackTrace();
        }
        System.out.println("SDL version found: "+sdl);
        if(compareVersions(sdl, "2.0.9")<0){
            throw new FileNotFoundException("\n!!! SDL version must be >= 2.0.9");
        }

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
        BuildTarget lin64 = BuildTarget.newDefaultTarget(TargetOs.Linux, true);

        lin64.cIncludes = new String[] {};
        lin64.cppExcludes = EXCLUDES;
        String cflags = execCmd("sdl2-config --cflags"); // "-I/usr/local/include/SDL2"
        lin64.cFlags = lin64.cFlags + " "  + cflags;
        lin64.cppFlags = lin64.cFlags;
        lin64.linkerFlags = "-shared -m64";
        String libraries = execCmd("sdl2-config --static-libs").replace("-lSDL2","-l:libSDL2.a" );
        //"-L/usr/local/lib -Wl,-rpath,/usr/local/lib -Wl,--enable-new-dtags -l:libSDL2.a -Wl,--no-undefined -lm -ldl -lsndio -lpthread -lrt";
        lin64.libraries = libraries;


        //OSX build configs
        BuildTarget mac64 = BuildTarget.newDefaultTarget(TargetOs.MacOsX, true);

        mac64.cIncludes = new String[] {};
        mac64.cppExcludes = EXCLUDES;
        mac64.headerDirs = new String[] {"/usr/local/include/SDL2"};
        mac64.cFlags = "-c -Wall -O2 -arch x86_64 -DFIXED_POINT -fmessage-length=0 -fPIC -mmacosx-version-min=10.6";
        mac64.cppFlags = mac64.cFlags;
        mac64.linkerFlags = "-shared -arch x86_64 -mmacosx-version-min=10.6";
        mac64.libraries = "/usr/local/lib/libSDL2.a -lm -liconv -Wl,-framework,CoreAudio -Wl,-framework,AudioToolbox -Wl,-framework,ForceFeedback -lobjc -Wl,-framework,CoreVideo -Wl,-framework,Cocoa -Wl,-framework,Carbon -Wl,-framework,IOKit -Wl,-weak_framework,QuartzCore -Wl,-weak_framework,Metal";


        //Generate native code, build scripts
        System.out.println("##### GENERATING NATIVE CODE AND BUILD SCRIPTS #####");
        new NativeCodeGenerator().generate("src", "build/classes/main", "jni");
        new AntScriptGenerator().generate(
                new BuildConfig("jamepad", "build/tmp", "libs", "jni"), win32, win64, lin64, mac64
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

            BuildExecutorFixed.executeAnt("jni/build-windows32.xml", "-Dhas-compiler=true clean postcompile");
            BuildExecutorFixed.executeAnt("jni/build-windows64.xml", "-Dhas-compiler=true clean postcompile");
            System.out.println();
        }
        if (buildLinux) {
            System.out.println("##### COMPILING NATIVES FOR LINUX #####");

            if(!useSystemSDL) {
                throw new IllegalArgumentException("Linux build is only available using system SDL");
            }
            BuildExecutorFixed.executeAnt("jni/build-linux64.xml", "-Dhas-compiler=true clean postcompile");
            System.out.println();
        }
        if (buildOSX) {
            System.out.println("##### COMPILING NATIVES FOR OSX #####");
            if(!useSystemSDL) {
                throw new IllegalArgumentException("Mac build is only available using system SDL");
            }
            BuildExecutorFixed.executeAnt("jni/build-macosx64.xml", "-Dhas-compiler=true clean postcompile");
            System.out.println();
        }

        System.out.println("##### PACKING NATIVES INTO .JAR #####");
        BuildExecutorFixed.executeAnt("jni/build.xml", "pack-natives");
	}

    public static String execCmd(String cmd) throws java.io.IOException {
        java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next().trim() : "";
    }

    public static int compareVersions(String v1, String v2) {
        String[] components1 = v1.split("\\.");
        String[] components2 = v2.split("\\.");
        int length = Math.min(components1.length, components2.length);
        for(int i = 0; i < length; i++) {
            int result = new Integer(components1[i]).compareTo(Integer.parseInt(components2[i]));
            if(result != 0) {
                return result;
            }
        }
        return Integer.compare(components1.length, components2.length);
    }
}