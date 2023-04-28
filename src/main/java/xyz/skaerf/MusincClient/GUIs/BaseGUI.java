package xyz.skaerf.MusincClient.GUIs;

import javax.swing.*;
import java.awt.*;

public class BaseGUI {

    private final boolean isMain;

    public BaseGUI(boolean isMain) {
        this.isMain = isMain;
        init();
    }

    public JFrame frame = new JFrame("Musinc");
    public int[] frameRes = null;

    private void init() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int[] primaryRes = new int[]{(int) screenSize.getWidth(), (int) screenSize.getHeight()};
        frame.setLayout(new BorderLayout());
        if (this.isMain) {
            frame.setMinimumSize(new Dimension(primaryRes[0], primaryRes[1]));
            frame.setSize(primaryRes[0], primaryRes[1]);
        }
        else {
            // login gui size
            frameRes = new int[]{primaryRes[0], primaryRes[1]};
            frame.setSize(frameRes[0], frameRes[1]);
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void closeFrame() {
        frame.setVisible(false);
        frame.dispose();
    }

    public JFrame getFrame() {
        return this.frame;
    }

}
