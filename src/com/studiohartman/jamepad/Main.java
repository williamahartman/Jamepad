package com.studiohartman.jamepad;

import com.badlogic.gdx.jnigen.JniGenSharedLibraryLoader;

public class Main {
    // @off

    static public native int add (int a, int b); /*
        return a + b;
    */

    public static void main (String[] args) throws Exception {
        JamepadBuild.main(new String[0]);

        new JniGenSharedLibraryLoader().load("libs/jamepad-natives.jar");
        System.out.println(add(1, 2));
    }
}
