package com.studiohartman.jamepad;

import com.badlogic.gdx.jnigen.*;
import com.badlogic.gdx.jnigen.BuildTarget.TargetOs;

public class JamepadBuild {
    private static String[] merge(String[] a, String ... b) {
        String[] n = new String[a.length + b.length];
        System.arraycopy(a, 0, n, 0, a.length);
        System.arraycopy(b, 0, n, a.length, b.length);
        return n;
    }

	public static void main(String[] args) throws Exception {
        /*
        PRE BUILD:

        run:
        ./configure --disable-audio --disable-video --disable-render --disable-power --disable-filesystem --disable-file --disable-loadso --disable-cpuinfo
         */

        //Listing needed source files for each platform
        String[] commonSrc = {
                "*.*",
                "SDL2-2.0.4/src/*.*",
                "SDL2-2.0.4/src/atomic/*.*",
                "SDL2-2.0.4/src/events/*.*",
                "SDL2-2.0.4/src/haptic/*.*",
                "SDL2-2.0.4/src/joystick/*.*",
                "SDL2-2.0.4/src/stdlib/*.*",
                "SDL2-2.0.4/src/thread/*.*",
                "SDL2-2.0.4/src/timer/*.*",
        };
		String[] windowsSrc = {
                "SDL2-2.0.4/src/core/windows/*.*",
                "SDL2-2.0.4/src/haptic/windows/*.*",
                "SDL2-2.0.4/src/joystick/windows/*.*",
                "SDL2-2.0.4/src/thread/windows/*.*",
                "SDL2-2.0.4/src/timer/windows/*.*",
        };
		String[] linuxSrc = {
                "SDL2-2.0.4/src/core/linux/*.*",
	            "SDL2-2.0.4/src/haptic/linux/*.*",
	            "SDL2-2.0.4/src/joystick/linux/*.*",
                "SDL2-2.0.4/src/thread/pthread/*.*",
                "SDL2-2.0.4/src/timer/unix/*.*",
        };
        String[] macSrc = {
                "SDL2-2.0.4/src/haptic/darwin/*.*",
                "SDL2-2.0.4/src/joystick/darwin/*.*",
                "SDL2-2.0.4/src/thread/pthread/*.*",
                "SDL2-2.0.4/src/timer/unix/*.*",
        };
        String[] includes = new String[] {"include", "SDL2-2.0.4/include", "SDL2-2.0.4/src"};
        String[] excludes = {"SDL2-2.0.4/**/*.cpp"};

        //Windows build configs
		BuildTarget win32 = BuildTarget.newDefaultTarget(TargetOs.Windows, false);
		BuildTarget win64 = BuildTarget.newDefaultTarget(TargetOs.Windows, true);

        //Linux build configs
		BuildTarget lin32 = BuildTarget.newDefaultTarget(TargetOs.Linux, false);
        lin32.cIncludes = merge(commonSrc, linuxSrc);
        lin32.cppExcludes = excludes;
        lin32.headerDirs = includes;
        lin32.cFlags = "-c -Wall -O2 -mfpmath=sse -msse -fmessage-length=0 -m32 -fPIC -DUSING_GENERATED_CONFIG_H";
        lin32.linkerFlags = "-shared -m32 -Wl,-wrap,memcpy";
        lin32.libraries = "-lm -ldl -lpthread -lrt";
		BuildTarget lin64 = BuildTarget.newDefaultTarget(TargetOs.Linux, true);
        lin64.cIncludes = merge(commonSrc, linuxSrc);;
        lin64.cppExcludes = excludes;
        lin64.headerDirs = includes;
        lin64.cFlags = "-c -Wall -O2 -mfpmath=sse -msse -fmessage-length=0 -m64 -fPIC -DUSING_GENERATED_CONFIG_H";
        lin64.linkerFlags = "-shared -m64 -Wl,-wrap,memcpy";
        lin64.libraries = "-lm -ldl -lpthread -lrt";

        //Mac OSX build configs
		BuildTarget mac32 = BuildTarget.newDefaultTarget(TargetOs.MacOsX, false);
		BuildTarget mac64 = BuildTarget.newDefaultTarget(TargetOs.MacOsX, true);

        //Generate native code
		new NativeCodeGenerator().generate("src", "build/classes/main", "jni");

        //Build our library for all platforms and bitnesses
		new AntScriptGenerator().generate(
                new BuildConfig("jamepad", "build/tmp", "libs", "jni"), win32, win64, lin32, lin64, mac32, mac64);
//		BuildExecutor.executeAnt("jni/build-windows32.xml", "-v -Dhas-compiler=true clean postcompile");
//		BuildExecutor.executeAnt("jni/build-windows64.xml", "-v -Dhas-compiler=true clean postcompile");
		BuildExecutor.executeAnt("jni/build-linux32.xml", "-Dhas-compiler=true clean postcompile");
		BuildExecutor.executeAnt("jni/build-linux64.xml", "-Dhas-compiler=true clean postcompile");
//		BuildExecutor.executeAnt("jni/build-macosx32.xml", "-v -Dhas-compiler=true  clean postcompile");
//		BuildExecutor.executeAnt("jni/build-macosx64.xml", "-v -Dhas-compiler=true  clean postcompile");
		BuildExecutor.executeAnt("jni/build.xml", "pack-natives");
	}
}