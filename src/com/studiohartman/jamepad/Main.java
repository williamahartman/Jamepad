package com.studiohartman.jamepad;

import com.badlogic.gdx.jnigen.JniGenSharedLibraryLoader;

public class Main {
    static public native int add (int a, int b); /*
        return a + b;
    */

    public static void main (String[] args) throws Exception {
        //JamepadBuild.main(new String[0]);

        new JniGenSharedLibraryLoader("libs/jamepad-natives.jar");
        new JniGenSharedLibraryLoader().load("jamepad");
        System.out.println(add(1, 2));
    }
}
