package com.studiohartman.jamepad;

import javax.swing.*;
import java.awt.*;

/**
 * A quick and dirty interface to check if a controller is working. I hope you like swing!
 */
public class ControllerTester {
    public static int NUM_CONTROLLERS = 4;

    public static void updatePanel(JPanel p, Controller c) {
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.removeAll();

        JPanel title = new JPanel();
        title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.add(new JLabel(c.getName()));

        JPanel axes = new JPanel();
        for(ControllerAxis a: ControllerAxis.values()) {
            JLabel label = new JLabel();
            label.setPreferredSize(new Dimension(100, 30));
            label.setText(a.name());

            JProgressBar progressBar = new JProgressBar(-100, 100);
            progressBar.setPreferredSize(new Dimension(200, 30));
            progressBar.setValue((int) (c.getAxisState(a) * 100));

            JPanel axisPanel = new JPanel();
            axisPanel.setLayout(new BoxLayout(axisPanel, BoxLayout.X_AXIS));
            axisPanel.add(label);
            axisPanel.add(progressBar);
            axes.add(axisPanel);
        }

        JPanel buttons = new JPanel();
        for(ControllerButton b: ControllerButton.values()) {
            JButton button = new JButton(b.name());
            button.setEnabled(c.isButtonPressed(b));
            buttons.add(button);
        }

        p.add(title);
        p.add(axes);
        p.add(buttons);
    }

    public static void run() {
        JTabbedPane tabbedPane = new JTabbedPane();

        ControllerManager controllers = new ControllerManager();
        controllers.initSDLGamepad();

        JFrame testFrame = new JFrame();
        testFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        testFrame.setLocationRelativeTo(null);
        testFrame.setMinimumSize(new Dimension(640, 300));
        testFrame.setResizable(false);
        testFrame.setVisible(true);

        JPanel[] controllerTabs = new JPanel[NUM_CONTROLLERS];
        for(int i = 0; i < controllerTabs.length; i++) {
            controllerTabs[i] = new JPanel();
            tabbedPane.add(controllerTabs[i]);
        }

        testFrame.setContentPane(tabbedPane);

        boolean currentControllerDisconnectedUpdated = false;
        while (true) {
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(int i = 0;  i < controllerTabs.length; i++) {
                JPanel p = controllerTabs[i];
                if (controllers.getNumControllers() > i) {
                    try {
                        updatePanel(p, controllers.get(0));
                        tabbedPane.setTitleAt(i, (i + 1) + " - Connected" );
                    } catch (JamepadRuntimeException e) {
                        p.removeAll();
                        p.add(new Label("Runtime Exception!"));
                        e.printStackTrace();
                    }

                    testFrame.setContentPane(tabbedPane);
                    if(i == tabbedPane.getSelectedIndex() && !currentControllerDisconnectedUpdated) {
                        currentControllerDisconnectedUpdated = false;
                    }
                } else {
                    tabbedPane.setTitleAt(i, (i + 1) + " - Disconnected" );
                    if(i == tabbedPane.getSelectedIndex() && !currentControllerDisconnectedUpdated) {
                        p.removeAll();
                        p.add(new Label("Controller Not Connected!"));
                        testFrame.setContentPane(tabbedPane);

                        currentControllerDisconnectedUpdated = true;
                    }
                }

                controllers.updateConnectedControllers();
            }
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
