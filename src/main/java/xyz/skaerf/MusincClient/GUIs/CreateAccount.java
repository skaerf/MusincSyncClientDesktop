/*
 * Created by JFormDesigner on Fri Jan 26 12:00:46 GMT 2024
 */

package xyz.skaerf.MusincClient.GUIs;

import xyz.skaerf.MusincClient.Main;
import xyz.skaerf.MusincClient.PassManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

/**
 * @author lawre
 */
public class CreateAccount {
    public CreateAccount() {
        initComponents();
    }

    public JFrame getCreateAccount() {
        return CreateAccount;
    }

    private void signUpButtonClicked(ActionEvent e) {
        String username = usernameBox.getText();
        String email = emailBox.getText();
        String firstName = firstNameBox.getText();
        String lastName = lastNameBox.getText();
        String password = Arrays.toString(passwordBox.getPassword());
        String passwordConf = Arrays.toString(passwordConfBox.getPassword());
        if (!passwordConf.equals(password)) {
            errorMessage.setVisible(true);
            System.out.println("Passwords do not match");
            return;
        }
        if (Main.createAccount(username, email, firstName, lastName, PassManager.hashPassword(password))) {
            System.out.println("Successfully created account, logging in with the same details");
            Main.logInRequest(username, password, false);
            if (Main.isLoggedIn) {
                System.out.println("Successfully logged in");
                Main.mainGUI.getMusinc().setVisible(true);
                CreateAccount.setVisible(false);
                CreateAccount.dispose();
            }
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Lawrence Harrison (MR L A J HARRISON)
        CreateAccount = new JFrame();
        header = new JLabel();
        header2 = new JLabel();
        firstNameBox = new JTextField();
        lastNameBox = new JTextField();
        emailBox = new JTextField();
        usernameBox = new JTextField();
        passwordBox = new JPasswordField();
        passwordConfBox = new JPasswordField();
        signUp = new JButton();
        label1 = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        label4 = new JLabel();
        label5 = new JLabel();
        label6 = new JLabel();
        errorMessage = new JLabel();

        //======== CreateAccount ========
        {
            CreateAccount.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            CreateAccount.setTitle("Create Account");
            CreateAccount.setMinimumSize(new Dimension(250, 250));
            var CreateAccountContentPane = CreateAccount.getContentPane();

            //---- header ----
            header.setText("Musinc");
            header.setFont(new Font("Segoe UI", Font.BOLD, 24));
            header.setForeground(new Color(0xff3333));
            header.setHorizontalAlignment(SwingConstants.CENTER);

            //---- header2 ----
            header2.setText("Sign Up");
            header2.setHorizontalAlignment(SwingConstants.CENTER);

            //---- signUp ----
            signUp.setText("Sign Up");
            signUp.addActionListener(e -> signUpButtonClicked(e));

            //---- label1 ----
            label1.setText("First Name");
            label1.setLabelFor(firstNameBox);

            //---- label2 ----
            label2.setText("Username");
            label2.setLabelFor(usernameBox);

            //---- label3 ----
            label3.setText("Last Name");
            label3.setLabelFor(lastNameBox);

            //---- label4 ----
            label4.setText("Password");
            label4.setLabelFor(passwordBox);

            //---- label5 ----
            label5.setText("Email");
            label5.setLabelFor(emailBox);

            //---- label6 ----
            label6.setText("Confirm Password");
            label6.setLabelFor(passwordConfBox);

            //---- errorMessage ----
            errorMessage.setText("Please check that all the boxes have been filled and that your passwords match!");
            errorMessage.setHorizontalAlignment(SwingConstants.CENTER);
            errorMessage.setForeground(new Color(0xff3333));
            errorMessage.setFont(errorMessage.getFont().deriveFont(errorMessage.getFont().getStyle() & ~Font.BOLD));
            errorMessage.setVisible(false);

            GroupLayout CreateAccountContentPaneLayout = new GroupLayout(CreateAccountContentPane);
            CreateAccountContentPane.setLayout(CreateAccountContentPaneLayout);
            CreateAccountContentPaneLayout.setHorizontalGroup(
                CreateAccountContentPaneLayout.createParallelGroup()
                    .addComponent(header, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(header2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(CreateAccountContentPaneLayout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addGroup(CreateAccountContentPaneLayout.createParallelGroup()
                            .addGroup(CreateAccountContentPaneLayout.createSequentialGroup()
                                .addGroup(CreateAccountContentPaneLayout.createParallelGroup()
                                    .addGroup(CreateAccountContentPaneLayout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(label5))
                                    .addComponent(lastNameBox, GroupLayout.PREFERRED_SIZE, 148, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(CreateAccountContentPaneLayout.createParallelGroup()
                                    .addGroup(CreateAccountContentPaneLayout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(label6)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(passwordBox)))
                            .addGroup(CreateAccountContentPaneLayout.createSequentialGroup()
                                .addGroup(CreateAccountContentPaneLayout.createParallelGroup()
                                    .addComponent(label3)
                                    .addGroup(CreateAccountContentPaneLayout.createSequentialGroup()
                                        .addComponent(emailBox, GroupLayout.PREFERRED_SIZE, 182, GroupLayout.PREFERRED_SIZE)
                                        .addGap(27, 27, 27)
                                        .addComponent(passwordConfBox, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(CreateAccountContentPaneLayout.createSequentialGroup()
                                        .addGroup(CreateAccountContentPaneLayout.createParallelGroup()
                                            .addComponent(firstNameBox, GroupLayout.PREFERRED_SIZE, 148, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label1))
                                        .addGap(18, 18, 18)
                                        .addGroup(CreateAccountContentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                            .addComponent(label2)
                                            .addComponent(usernameBox, GroupLayout.PREFERRED_SIZE, 148, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label4))))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(GroupLayout.Alignment.TRAILING, CreateAccountContentPaneLayout.createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(signUp)
                        .addGap(192, 192, 192))
                    .addGroup(CreateAccountContentPaneLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(errorMessage, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())
            );
            CreateAccountContentPaneLayout.setVerticalGroup(
                CreateAccountContentPaneLayout.createParallelGroup()
                    .addGroup(CreateAccountContentPaneLayout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(header)
                        .addGap(0, 0, 0)
                        .addComponent(header2)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(CreateAccountContentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(label1)
                            .addComponent(label2))
                        .addGap(4, 4, 4)
                        .addGroup(CreateAccountContentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(firstNameBox, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                            .addComponent(usernameBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(CreateAccountContentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(label3)
                            .addComponent(label4))
                        .addGap(2, 2, 2)
                        .addGroup(CreateAccountContentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(lastNameBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(passwordBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(CreateAccountContentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(label5)
                            .addComponent(label6))
                        .addGap(2, 2, 2)
                        .addGroup(CreateAccountContentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(passwordConfBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(emailBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(24, 24, 24)
                        .addComponent(signUp)
                        .addGap(18, 18, 18)
                        .addComponent(errorMessage)
                        .addContainerGap())
            );
            CreateAccount.pack();
            CreateAccount.setLocationRelativeTo(CreateAccount.getOwner());
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Lawrence Harrison (MR L A J HARRISON)
    private JFrame CreateAccount;
    private JLabel header;
    private JLabel header2;
    private JTextField firstNameBox;
    private JTextField lastNameBox;
    private JTextField emailBox;
    private JTextField usernameBox;
    private JPasswordField passwordBox;
    private JPasswordField passwordConfBox;
    private JButton signUp;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JLabel label6;
    private JLabel errorMessage;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
