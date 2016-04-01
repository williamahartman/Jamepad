package com.studiohartman.jamepad.tester;

import com.studiohartman.jamepad.ControllerAxis;
import com.studiohartman.jamepad.ControllerButton;
import com.studiohartman.jamepad.ControllerIndex;
import com.studiohartman.jamepad.ControllerUnpluggedException;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * A JPanel that displays information about a given ControllerIndex.
 */
public class ControllerInfoPanel extends JPanel {
    private JPanel title;
    private JPanel axes;
    private JPanel buttons;
    private JButton vibrateButton;
    private JButton stopVibrateButton;
    private JLabel titleLabel;

    public ControllerInfoPanel() {
        setLayout(new BorderLayout());

        title = new JPanel();
        axes = new JPanel();
        buttons = new JPanel();

        JPanel vibratePanel = new JPanel();
        vibrateButton = new JButton("Start vibrating");
        stopVibrateButton = new JButton("Stop vibrating");
        vibratePanel.add(vibrateButton);
        vibratePanel.add(stopVibrateButton);

        title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel = new JLabel();
        title.add(titleLabel);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        middlePanel.add(title);
        middlePanel.add(axes);
        middlePanel.add(buttons);

        add(middlePanel);
        add(vibratePanel, BorderLayout.SOUTH);
    }

    public void updatePanel(ControllerIndex c) {
        try {
            titleLabel.setText(c.getName());

            axes.removeAll();
            for (ControllerAxis a : ControllerAxis.values()) {
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

            buttons.removeAll();
            for (ControllerButton b : ControllerButton.values()) {
                JButton button = new JButton(b.name());
                button.setEnabled(c.isButtonPressed(b));
                buttons.add(button);
            }

            Arrays.stream(vibrateButton.getActionListeners()).forEach(vibrateButton::removeActionListener);
            vibrateButton.addActionListener(event -> {
                try {
                    c.startVibration(1, 1);
                } catch (ControllerUnpluggedException e) {
                    System.err.println("Failed to vibrate!");
                }
            });

            Arrays.stream(stopVibrateButton.getActionListeners()).forEach(stopVibrateButton::removeActionListener);
            stopVibrateButton.addActionListener(event -> c.stopVibration());
        } catch (ControllerUnpluggedException e) {
            e.printStackTrace();

            titleLabel.setText("a Jamepad runtime exception occurred!");
            axes.removeAll();
            buttons.removeAll();

            axes.add(new JLabel(e.getMessage()));
        }
    }

    public void setAsDisconnected() {
        titleLabel.setText("No controller connected at this index!");
        axes.removeAll();
        buttons.removeAll();
    }
}
