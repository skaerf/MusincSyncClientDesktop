package xyz.skaerf.MusincClient;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import xyz.skaerf.MusincClient.GUIs.LoginGUI;
import xyz.skaerf.MusincClient.GUIs.MainGUI;

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

    public static String makeRequest(String request) {
        writer.println(request);
        try {
            String response = reader.readLine();
            while (response.equals("")) {
                response = reader.readLine();
            }
            return response;
        }
        catch (IOException e) {
            System.out.println("Could not read response from makeRequest");
        }
        return null;
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

}