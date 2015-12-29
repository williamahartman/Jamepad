package com.studiohartman.jamepad;

import java.io.File;

import com.badlogic.gdx.jnigen.*;
import com.badlogic.gdx.jnigen.BuildTarget.TargetOs;

public class JamepadBuild {
	public static void main(String[] args) throws Exception {
		BuildTarget win32 = BuildTarget.newDefaultTarget(TargetOs.Windows, false);
		BuildTarget win64 = BuildTarget.newDefaultTarget(TargetOs.Windows, true);
		BuildTarget lin32 = BuildTarget.newDefaultTarget(TargetOs.Linux, false);
		BuildTarget lin64 = BuildTarget.newDefaultTarget(TargetOs.Linux, true);
		BuildTarget mac32 = BuildTarget.newDefaultTarget(TargetOs.MacOsX, false);
		BuildTarget mac64 = BuildTarget.newDefaultTarget(TargetOs.MacOsX, true);

		new NativeCodeGenerator().generate("src", "build/classes/main", "jni");
		new AntScriptGenerator().generate(new BuildConfig("jamepad", "build/tmp", "libs", "jni"),
				win32, win64, lin32, lin64, mac32, mac64);
		BuildExecutor.executeAnt("jni/build-windows32.xml", "-v -Dhas-compiler=true clean postcompile");
		BuildExecutor.executeAnt("jni/build-windows64.xml", "-v -Dhas-compiler=true clean postcompile");
		BuildExecutor.executeAnt("jni/build-linux32.xml", "-v -Dhas-compiler=true clean postcompile");
		BuildExecutor.executeAnt("jni/build-linux64.xml", "-v -Dhas-compiler=true clean postcompile");
		BuildExecutor.executeAnt("jni/build-macosx32.xml", "-v -Dhas-compiler=true  clean postcompile");
		BuildExecutor.executeAnt("jni/build-macosx64.xml", "-v -Dhas-compiler=true  clean postcompile");
		BuildExecutor.executeAnt("jni/build.xml", "-v pack-natives");
	}
}