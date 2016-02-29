package com.studiohartman.jamepad;

import javax.swing.*;
import java.awt.*;

public class ControllerTester {
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

    public static void main (String[] args) throws InterruptedException {
        ControllerManager controllers = new ControllerManager();
        controllers.initSDLGamepad();

        JFrame testFrame = new JFrame();
        testFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        testFrame.setLocationRelativeTo(null);
        testFrame.setMinimumSize(new Dimension(640, 300));
        testFrame.setResizable(false);
        testFrame.setVisible(true);

        JPanel p = new JPanel();
        testFrame.setContentPane(p);

        while (true) {
            Thread.sleep(30);

            if (controllers.getNumControllers() > 0) {
                try {
                    updatePanel(p, controllers.get(0));
                } catch (JamepadRuntimeException e) {
                    p.removeAll();
                    p.add(new Label("Runtime Exception!"));
                    e.printStackTrace();
                }
            } else {
                p.removeAll();
                p.add(new Label("Controller Not Connected!"));
            }
            controllers.updateConnectedControllers();

            testFrame.setContentPane(p);
        }
    }
}
