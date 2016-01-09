package com.studiohartman.jamepad;

public class Main {
    private static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            }
            else {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    private static void printWithWhitespace(String str, int len) {
        System.out.print(str);
        for(int i = str.length() - 1; i < len; i ++) {
            System.out.print(" ");
        }
    }
    private static void printButtonsAndAxes(ControllerManager manager) {
        System.out.println(manager.getControllers()[0].getControllerName());
        for (ControllerButton button : ControllerButton.values()) {
            printWithWhitespace(button.toString(), 20);
            System.out.println(manager.getControllers()[0].isButtonPressed(button) ? "[#]" : "[ ]");
        }
        for (ControllerAxis axis : ControllerAxis.values()) {
            float axisState = (manager.getControllers()[0].getAxisState(axis) + 1) / 2f;
            int numTicks = (int) (axisState * 15);

            printWithWhitespace(axis.toString(), 20);
            System.out.print("[");
            for (int i = 0; i < 15; i++) {
                if (i <= numTicks) {
                    System.out.print("#");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println("]");
        }
        System.out.println();
    }

    public static void main (String[] args) throws InterruptedException {
        ControllerManager manager = new ControllerManager();
        manager.initSDLGamepad();

        while (true) {
            Thread.sleep(30);
            clearConsole();

            if(manager.getControllers().length > 0) {
                printButtonsAndAxes(manager);
            } else {
                System.err.println("Controller 1 is not connected!");
            }

            manager.refreshSDLGamepad();
        }
    }
}
