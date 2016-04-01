package com.studiohartman.jamepad.tester;

import com.studiohartman.jamepad.ControllerIndex;
import com.studiohartman.jamepad.ControllerManager;

import javax.swing.*;
import java.awt.*;

/**
 * A quick and dirty interface to check if a controller is working. I hope you like swing!
 */
public class ControllerTester {
    public static int NUM_CONTROLLERS = 4;

    public static void run() {
        JTabbedPane tabbedPane = new JTabbedPane();

        ControllerManager controllers = new ControllerManager(NUM_CONTROLLERS);
        controllers.initSDLGamepad();

        JFrame testFrame = new JFrame();
        testFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        testFrame.setLocationRelativeTo(null);
        testFrame.setMinimumSize(new Dimension(640, 350));
        testFrame.setResizable(false);
        testFrame.setVisible(true);

        ControllerInfoPanel[] controllerTabs = new ControllerInfoPanel[NUM_CONTROLLERS];
        for(int i = 0; i < NUM_CONTROLLERS; i++) {
            controllerTabs[i] = new ControllerInfoPanel();
            tabbedPane.add("   Controller " + (i + 1) + "   ", controllerTabs[i]);

        }
        testFrame.setContentPane(tabbedPane);

        while (true) {
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            controllers.update();
            for(int i = 0;  i < controllerTabs.length; i++) {
                ControllerIndex controllerAtIndex = controllers.getControllerIndex(i);
                if(controllerAtIndex.isConnected()) {
                    controllerTabs[i].updatePanel(controllerAtIndex);
                } else {
                    controllerTabs[i].setAsDisconnected();
                }
            }
            testFrame.repaint();
        }
    }

    public static void main (String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        run();
    }
}
