package com.studiohartman.jamepad;

import java.io.File;

import com.badlogic.gdx.jnigen.*;
import com.badlogic.gdx.jnigen.BuildTarget.TargetOs;

public class JamepadBuild {
	public static void main(String[] args) throws Exception {
        /*
        PRE BUILD:

        run:
        ./configure --disable-audio --disable-video --disable-render --disable-power --disable-filesystem --disable-file --disable-loadso --disable-cpuinfo

         */

		String[] windowsSrc = {

        };
		String[] linuxSrc = {
                "*",
                "SDL2-2.0.4/src/*",
                "SDL2-2.0.4/src/atomic/*",
                "SDL2-2.0.4/src/core/linux/*",
	            "SDL2-2.0.4/src/events/*",
	            "SDL2-2.0.4/src/haptic/*",
	            "SDL2-2.0.4/src/haptic/linux/*",
	            "SDL2-2.0.4/src/joystick/*",
	            "SDL2-2.0.4/src/joystick/linux/*",
	            "SDL2-2.0.4/src/stdlib/*",
                "SDL2-2.0.4/src/thread/*",
                "SDL2-2.0.4/src/thread/pthread/*",
                "SDL2-2.0.4/src/timer/*",
                "SDL2-2.0.4/src/timer/unix/*",
        };
        String[] macSrc = {

        };
        String[] linuxExcludes = {"SDL2-2.0.4/**/*.cpp"};

		String[] includes = new String[] {"include", "SDL2-2.0.4/include"};

		BuildTarget win32 = BuildTarget.newDefaultTarget(TargetOs.Windows, false);
		BuildTarget win64 = BuildTarget.newDefaultTarget(TargetOs.Windows, true);

		BuildTarget lin32 = BuildTarget.newDefaultTarget(TargetOs.Linux, false);
		BuildTarget lin64 = BuildTarget.newDefaultTarget(TargetOs.Linux, true);
        lin64.cIncludes = linuxSrc;
        lin64.cppExcludes = linuxExcludes;
        lin64.headerDirs = includes;
        lin64.cFlags = "-c -Wall -O2 -mfpmath=sse -msse -fmessage-length=0 -m64 -fPIC -DUSING_GENERATED_CONFIG_H";
        lin64.linkerFlags = "-shared -m64 -Wl,-wrap,memcpy";
        lin64.libraries = "-lm -ldl -lpthread -lrt";

		BuildTarget mac32 = BuildTarget.newDefaultTarget(TargetOs.MacOsX, false);
		BuildTarget mac64 = BuildTarget.newDefaultTarget(TargetOs.MacOsX, true);

		new NativeCodeGenerator().generate("src", "build/classes/main", "jni");
		new AntScriptGenerator().generate(new BuildConfig("jamepad", "build/tmp", "libs", "jni"),
				/*win32, win64, lin32,*/ lin64/*, mac32, mac64*/);
//		BuildExecutor.executeAnt("jni/build-windows32.xml", "-v -Dhas-compiler=true clean postcompile");
//		BuildExecutor.executeAnt("jni/build-windows64.xml", "-v -Dhas-compiler=true clean postcompile");
//		BuildExecutor.executeAnt("jni/build-linux32.xml", "-Dhas-compiler=true clean postcompile");
		BuildExecutor.executeAnt("jni/build-linux64.xml", "-Dhas-compiler=true clean postcompile");
//		BuildExecutor.executeAnt("jni/build-macosx32.xml", "-v -Dhas-compiler=true  clean postcompile");
//		BuildExecutor.executeAnt("jni/build-macosx64.xml", "-v -Dhas-compiler=true  clean postcompile");
		BuildExecutor.executeAnt("jni/build.xml", "-v pack-natives");
	}
}