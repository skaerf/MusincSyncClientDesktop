package xyz.skaerf.MusincClient.GUIs;

import javax.swing.*;

public class MainGUI {

    private final BaseGUI base;

    public MainGUI() {
        base = new BaseGUI(true);
    }

    public void startFrame() {
        addButtons();
        base.getFrame().pack();
        base.getFrame().setVisible(true);
    }

    private void addButtons() {
        JButton test = new JButton("test");
        base.getFrame().add(test);

    }
}
