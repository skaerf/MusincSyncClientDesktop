package xyz.skaerf.MusincClient;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import xyz.skaerf.MusincClient.GUIs.JFormMain;
import xyz.skaerf.MusincClient.GUIs.LoginGUI;

import java.io.*;
import java.net.Socket;

public class Main {

    public static File configFile;
    public static JSONObject configJson;
    static JSONObject loginData;
    public static JFormMain mainGUI;
    public static LoginGUI loginGUI;
    static String hostname;
    static int port;
    static Socket socket;

    static String username;
    public static String firstName;
    public static String lastName;
    static String email;

    static String keepalive;
    static String refreshToken;

    static OutputStream output;
    static PrintWriter writer;
    static BufferedReader reader;

    static boolean isLogIn = true;
    public static boolean isLoggedIn = false;
    public static Thread albumArtUpdater;


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
            FileReader reader = new FileReader(configFile);
            Object obj = parser.parse(reader);
            configJson = (JSONObject) obj;
            keepalive = (String) configJson.get("keepalive");
            refreshToken = (String) configJson.get("refreshToken");
        }
        catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        hostname = "localhost";
        port = 1905;
        loginGUI = new LoginGUI();
        mainGUI = new JFormMain();

        if (keepalive != null) {
            System.out.println("Keepalive was found in configuration file, auto logging in");
            logInRequest(keepalive, null, true);
            if (isLoggedIn) {
                System.out.println("Successfully logged in with saved credentials");
                mainGUI.getMusinc().setVisible(true);
                if (refreshToken != null) {
                    System.out.println("Spotify refresh token found in configuration file, auto-connecting");
                    if (autoLinkSpotify(refreshToken)) {
                        System.out.println("Successfully re-authenticated using refresh token");
                        System.out.println("Starting album cover updater thread");
                        albumArtUpdater = new Thread(new AlbumArtUpdater());
                        albumArtUpdater.start();
                    }
                }
            }
            else {
                System.out.println("Saved details were not accepted by server, opening log in window");
                loginGUI.startFrame();
            }
        }
        else {
            System.out.println("Keepalive not found in config");
            loginGUI.startFrame();
        }

    }

    public static void wipeContentsOfDataFile() {
        try {
            configJson = new JSONObject();
            FileWriter fileWriter = new FileWriter(configFile);
            fileWriter.write(configJson.toJSONString());
            fileWriter.close();
        }
        catch (IOException e) {
            System.out.println("Could not save blank JSON string to data file to reset");
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
            System.exit(0);
        }
        return null;
    }

    public static boolean logInRequest(String username, String pass, boolean isKeepAlive) {
        if (socket != null && socket.isConnected()) {
            try {
                socket.close();
                socket = null;
            }
            catch (IOException e) {
                System.out.println("Could not close preexisting socket");
            }
        }
        Main.username = username;
        try {
            socket = new Socket(hostname, port);
        }
        catch (IOException e) {
            System.out.println("Connection was not accepted.");
            return false;
        }
        try {
            output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (isKeepAlive) {
            writer.println(RequestArgs.KEEPALIVE+keepalive+"\n");
        }
        else {
            writer.println(RequestArgs.LOG_IN + username + ":!:" + PassManager.hashPassword(pass) + "\n"); // username+":!:"+pass+"\n" - do not transmit unencrypted password obviously
        }
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
                    }
                    firstName = vars[1];
                    lastName = vars[2];
                    email = vars[3];
                    if (!isKeepAlive) {
                        keepalive = vars[4];
                        System.out.println("New keepalive saved as old appeared to be invalid");
                        FileWriter fileWriter = new FileWriter(configFile);
                        configJson.put("keepalive", keepalive);
                        fileWriter.write(configJson.toJSONString());
                        fileWriter.close();
                    }
                    isLoggedIn = true;
                    return true;
                }
                else if (response.startsWith(RequestArgs.DENIED)) {
                    System.out.println("Credentials were not accepted.");
                    return false;
                }
            }
        }
        catch (IOException e) {
            System.out.println("Connection was reset by server.");
            return false;
        }
        return false;
    }

    public static boolean autoLinkSpotify(String refreshToken) {
        String response = Main.makeRequest(RequestArgs.REAUTHENTICATE_SPOTIFY_ACCOUNT+refreshToken);
        if (response != null) {
            String arg = response.split(";")[0]+";";
            if (arg.equalsIgnoreCase(RequestArgs.ACCEPTED)) {
                return true;
            }
        }
        return false;
    }

}