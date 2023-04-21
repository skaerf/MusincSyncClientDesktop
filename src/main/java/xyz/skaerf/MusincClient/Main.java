package xyz.skaerf.MusincClient;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import xyz.skaerf.MusincClient.GUIs.LoginGUI;
import xyz.skaerf.MusincClient.GUIs.MainGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.Socket;

public class Main {

    static File configFile;
    static JSONObject configJson;
    static JSONObject loginData;
    public static MainGUI mainGUI;
    static String hostname;
    static int port;
    static Socket socket;

    static String username;
    public static String firstName;
    public static String lastName;
    static String email;

    static OutputStream output;
    static PrintWriter writer;
    static BufferedReader reader;

    static boolean isLogIn = true;
    public static boolean isLoggedIn = false;


    public static void main(String[] args) {
        configFile = new File("data.json");
        if (!configFile.exists()) {
            try {
                if (configFile.createNewFile()) {
                    System.out.println("Successfully created configuration file.");
                }
            }
            catch (IOException e) {
                System.out.println("Could not create configuration file.");
            }
        }
        JSONParser parser = new JSONParser();
        try {
            FileReader reader = new FileReader("data.json");
            Object obj = parser.parse(reader);
            configJson = (JSONObject) obj;
            username = (String) configJson.get("username");
        }
        catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        hostname = "localhost";
        port = 1905;
        LoginGUI loginGUI = new LoginGUI();
        mainGUI = new MainGUI();

        if (username != null) {
            System.out.println("Username was found in configuration file, auto logging in");
            logInRequest(username, null); // TODO add pass
            if (isLoggedIn) {
                System.out.println("Successfully logged in with saved credentials");
                mainGUI.startFrame();
            }
            else {
                System.out.println("Saved details were not accepted by server, opening log in window");
                loginGUI.startFrame();
            }
        }
        else {
            System.out.println("username is still null");
            loginGUI.startFrame();
        }
    }

    public static void logInRequest(String username, String pass) {
        Main.username = username;
        try {
            socket = new Socket(hostname, port);
        }
        catch (IOException e) {
            System.out.println("Connection was not accepted.");
            return;
        }
        try {
            output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        writer.println(RequestArgs.LOG_IN+username+"\n"); // username+":!:"+pass+"\n" - do not transmit unencrypted password obviously
        System.out.println("Transmitted login request");
        String response;
        try {
            while ((response = reader.readLine()) != null) {
                if (response.startsWith(RequestArgs.ACCEPTED)) {
                    String[] vars = response.split(";")[1].split(":!:");
                    if (username != null) {
                        Main.username = vars[0];
                    }
                    else {
                        Main.username = vars[0];
                        loginData.put("username", vars[0]); // TODO what?
                    }
                    firstName = vars[1];
                    lastName = vars[2];
                    email = vars[3];
                    isLoggedIn = true;
                    break;
                }
                else if (response.startsWith(RequestArgs.DENIED)) {
                    System.out.println("Credentials were not accepted.");
                    break;
                }
            }
        }
        catch (IOException e) {
            System.out.println("Connection was reset by server.");
        }
    }

    public static void initialiseGUI() {
        final String[] usernameFieldText = {"Username"};

        JFrame frame = new JFrame("Musinc");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Musinc");
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setForeground(Color.RED);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.TOP);
        label.setBounds(0, 65, frame.getWidth(), 30);
        frame.add(label);

        JTextField usernameField = new JTextField(usernameFieldText[0]);
        usernameField.setBounds(100, 125, 300, 30);
        frame.add(usernameField);

        JTextField passwordField = new JTextField("Password");
        passwordField.setBounds(100, 175, 300, 30);
        frame.add(passwordField);

        JButton loginButton = new JButton("Log In");
        loginButton.setBounds(100, 225, 300, 30);
        frame.add(loginButton);

        JLabel createAccountLabel = new JLabel("Create Account");
        createAccountLabel.setForeground(Color.BLUE);
        createAccountLabel.setHorizontalAlignment(JLabel.CENTER);
        createAccountLabel.setVerticalAlignment(JLabel.TOP);
        createAccountLabel.setBounds(0, 275, frame.getWidth(), 30);
        frame.add(createAccountLabel);

        frame.setLayout(null);
        frame.setVisible(true);

        // CREATE ACCOUNT MENU

        JTextField setEmailField = new JTextField("Email");
        setEmailField.setBounds(50, 175, 100, 30);

        JTextField createPassField = new JTextField("Password");
        createPassField.setBounds(50, 175, 100, 30);

        JTextField confPassField = new JTextField("Confirm Password");
        confPassField.setBounds(50, 225, 100, 30);

        JTextField setFirstNameField = new JTextField("First Name");
        setEmailField.setBounds(50, 275, 100, 30);

        JTextField setLastNameField = new JTextField("Last Name");
        createPassField.setBounds(50, 325, 150, 30);

        usernameField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                usernameField.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (usernameField.getText().isEmpty()) {
                    usernameField.setText(usernameFieldText[0]);
                }
            }
        });

        passwordField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                passwordField.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (passwordField.getText().isEmpty()) {
                    passwordField.setText("Password");
                }
            }
        });

        setEmailField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                setEmailField.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (setEmailField.getText().isEmpty()) {
                    setEmailField.setText("Email");
                }
            }
        });

        createPassField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                createPassField.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (createPassField.getText().isEmpty()) {
                    createPassField.setText("Password");
                }
            }
        });

        confPassField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                confPassField.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (confPassField.getText().isEmpty()) {
                    confPassField.setText("Confirm Password");
                }
            }
        });

        setFirstNameField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                setFirstNameField.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (setFirstNameField.getText().isEmpty()) {
                    setFirstNameField.setText("First Name");
                }
            }
        });

        setLastNameField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                setLastNameField.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (setLastNameField.getText().isEmpty()) {
                    setLastNameField.setText("Last Name");
                }
            }
        });

        createAccountLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("help");
                if (isLogIn) {
                    // show new display entirely - change this
                    frame.getContentPane().removeAll();
                    frame.add(label);
                    frame.add(setEmailField);
                    frame.add(createPassField);
                    frame.add(confPassField);
                    frame.add(setFirstNameField);
                    frame.add(setLastNameField);
                    frame.add(createAccountLabel);
                    frame.validate();
                    frame.repaint();
                    isLogIn = false;
                }
                else {
                    frame.removeAll();
                    frame.add(label);
                    frame.add(usernameField);
                    frame.add(passwordField);
                    frame.add(loginButton);
                    frame.add(createAccountLabel);
                    frame.repaint();
                    frame.revalidate();
                    usernameFieldText[0] = "Username";
                    usernameField.setText(usernameFieldText[0]);
                    isLogIn = true;
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        loginButton.addActionListener(e -> {
            if (loginButton.getText().equalsIgnoreCase("Log In")) {
                if ((usernameField.getText() != null && !usernameField.getText().equalsIgnoreCase("Username")) && (passwordField.getText() != null && !passwordField.getText().equalsIgnoreCase("Password"))) {
                    if (isLoggedIn) {
                        System.out.println("Welcome "+firstName+" "+lastName+"!");
                        // change ui
                        frame.removeAll();
                        frame.getContentPane().add(new JTextArea(firstName+" "+lastName), BorderLayout.CENTER);
                        frame.getContentPane().add(new JTextArea(email), BorderLayout.AFTER_LINE_ENDS);
                        frame.repaint();
                    }
                    else {
                        System.out.println("Incorrect credentials");
                    }
                }
                else {
                    System.out.println("Input username and password");
                }
            }
            else {
                frame.removeAll();
            }
        });
    }
}