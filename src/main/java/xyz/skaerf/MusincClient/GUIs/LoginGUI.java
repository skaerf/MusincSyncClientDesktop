package xyz.skaerf.MusincClient.GUIs;

import xyz.skaerf.MusincClient.Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

public class LoginGUI {

    private JButton logIn;
    private JButton createAccount;
    private final BaseGUI base;

    private final Action enter = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            logIn.doClick();
        }
    };

    public LoginGUI() {
        base = new BaseGUI(false);
    }

    public int[] getFrameRes() {
        return this.base.frameRes;
    }

    public void startFrame() {
        addButtons();
        base.getFrame().setMinimumSize(new Dimension(400,500));
        base.getFrame().pack();
        base.getFrame().setSize(base.getFrame().getMinimumSize());
        base.getFrame().setVisible(true);
    }


    private void addButtons() {
        // GRIDBAG AND SETUP
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.insets = new Insets(10, 10, 10, 10);
        // MUSINC TITLE
        JLabel title = new JLabel("Musinc", JLabel.CENTER);
        title.setBorder(new EmptyBorder(50,0,0,0));
        title.setForeground(Color.RED);
        title.setFont(title.getFont().deriveFont(26.0f));
        base.getFrame().add(title, BorderLayout.NORTH);
        // USERNAME
        JLabel userLabel = new JLabel("Username:");
        c.gridy = 1;
        panel.add(userLabel, c);
        c.gridy = 2;
        JTextField user = new JTextField(10);
        user.addActionListener(enter);
        panel.add(user, c);
        // PASSWORD
        JLabel passLabel = new JLabel("Password:");
        c.gridy = 3;
        panel.add(passLabel, c);
        c.gridy = 4;
        JPasswordField pass = new JPasswordField(10);
        pass.addActionListener(enter);
        panel.add(pass, c);
        // LOG IN BUTTON
        c.gridy = 5;
        c.weighty = 0;
        logIn = new JButton("Log In");
        logIn.setSize(100, 50);
        logIn.setMaximumSize(new Dimension(100, 50));
        panel.add(logIn, c);
        // CREATE ACCOUNT BUTTON
        c.gridy = 6;
        createAccount = new JButton("Create Account");
        createAccount.setSize(100,50);
        createAccount.setMaximumSize(new Dimension(100, 50));
        panel.add(createAccount, c);
        base.getFrame().add(panel, BorderLayout.CENTER);
        // ERROR MESSAGE
        JLabel errorLabel = new JLabel("Username and/or password incorrect!");
        errorLabel.setForeground(Color.RED);
        errorLabel.setVisible(false);
        panel.add(errorLabel, JLabel.CENTER);

        // BUTTON RESPONSE
        logIn.addActionListener(e -> {
            errorLabel.setVisible(false);
            base.getFrame().repaint();
            if (!user.getText().isEmpty() && pass.getPassword().length != 0) {
                if (Main.logInRequest(user.getText(), Arrays.toString(pass.getPassword()), false)) {
                    base.closeFrame();
                    System.out.println("Successfully logged in");
                    Main.mainGUI.getMusinc().setVisible(true);
                    if (Main.autoLinkSpotify(Main.refreshToken)) {
                        System.out.println("Successfully auto-re-authenticated Spotify");
                    }
                    else {
                        System.out.println("Failed to automatically re-authenticate Spotify");
                    }
                }
                else {
                    errorLabel.setVisible(true);
                    base.getFrame().repaint();
                }
            }
        });

        createAccount.addActionListener(e -> {
            base.closeFrame();
            CreateAccount createAcc = new CreateAccount();
            createAcc.getCreateAccount().setVisible(true);
        });
    }

}
