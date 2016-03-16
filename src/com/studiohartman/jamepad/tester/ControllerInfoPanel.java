package com.studiohartman.jamepad.tester;

import com.studiohartman.jamepad.ControllerIndex;
import com.studiohartman.jamepad.ControllerAxis;
import com.studiohartman.jamepad.ControllerButton;
import com.studiohartman.jamepad.JamepadRuntimeException;

import javax.swing.*;
import java.awt.*;

/**
 * Created by will on 3/10/16.
 */
public class ControllerInfoPanel extends JPanel {
    private JPanel title;
    private JPanel axes;
    private JPanel buttons;
    private JLabel titleLabel;

    public ControllerInfoPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        title = new JPanel();
        axes = new JPanel();
        buttons = new JPanel();
        title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel = new JLabel();
        title.add(titleLabel);

        add(title);
        add(axes);
        add(buttons);
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
        } catch (JamepadRuntimeException e) {
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
