package xyz.skaerf.MusincClient.GUIs;

import com.sun.net.httpserver.HttpServer;
import xyz.skaerf.MusincClient.AlbumArtUpdater;
import xyz.skaerf.MusincClient.Main;
import xyz.skaerf.MusincClient.RequestArgs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

public class JFormMainManager {

    private static String refreshToken;

    public static void refreshPlayingInfo(String icon, String songName, String songArtist, Long timestamp, Long songLength) {
        try {
            BufferedImage originalImage = ImageIO.read(new URL(icon));
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            int maxSize = 200;
            int newWidth, newHeight;
            if (width > height) {
                newWidth = maxSize;
                newHeight = (int) (height * ((double) maxSize / width));
            }
            else {
                newWidth = (int) (width * ((double) maxSize / height));
                newHeight = maxSize;
            }

            Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            ImageIcon newIcon = new ImageIcon(scaledImage);
            JFormMain.albumCover.setIcon(newIcon);
            JFormMain.songName.setText(songName);
            JFormMain.songArtist.setText(songArtist);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void makeSpotifyAccountRequest() {
        String response = Main.makeRequest(RequestArgs.CREATE_SPOTIFY_ACCOUNT);
        if (response != null) {
            String arg = response.split(";")[0] + ";";
            String[] data = response.split(";")[1].split(":!:");
            final String[] authCode = new String[1];
            if (arg.equalsIgnoreCase(RequestArgs.ACCEPTED)) {
                try {
                    try {
                        Desktop.getDesktop().browse(new URI(data[0]));
                        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
                        httpServer.createContext("/scallb", webResponse -> {
                            System.out.println("Response received");
                            authCode[0] = webResponse.getRequestURI().toString().split("callb\\?code=")[1];
                            String conf = """
                                    <h1>Musinc</h1>
                                    
                                    <p>Your Spotify account has been confirmed! Please go back to the application.</p>
                                    """;
                            webResponse.sendResponseHeaders(200, conf.length());
                            OutputStream outStream = webResponse.getResponseBody();
                            outStream.write(conf.getBytes());
                            outStream.close();
                            sendSpotifyCode(authCode);
                            httpServer.stop(0);
                        });
                        httpServer.setExecutor(null);
                        httpServer.start();
                        System.out.println("Waiting for response..");
                    }
                    catch (IOException er) {
                        System.out.println("Could not initialise web server");
                    }
                }
                catch (URISyntaxException ex) {
                    System.out.println("Could not convert " + data[0] + " to URI format");
                }
            }
        }
    }

    private static void sendSpotifyCode(String[] authCode) {
        String arg;
        String[] data;
        String response = Main.makeRequest(RequestArgs.GENERAL + authCode[0]);
        if (response != null) {
            arg = response.split(";")[0]+";";
            if (arg.equalsIgnoreCase(RequestArgs.ACCEPTED)) {
                System.out.println("Successfully linked Spotify account");
                try {
                    data = response.split(";")[1].split(":!:");
                    refreshToken = data[0];
                    System.out.println(Arrays.toString(data));
                    FileWriter fileWriter = new FileWriter(Main.configFile);
                    Main.configJson.put("refreshToken", refreshToken);
                    fileWriter.write(Main.configJson.toJSONString());
                    fileWriter.close();
                    String[] times = data[4].split("/");
                    refreshPlayingInfo(data[1], data[2], data[3], Long.parseLong(times[0]), Long.parseLong(times[1]));
                }
                catch (NullPointerException e) {
                    System.out.println("User not currently playing any music");
                }
                catch (IOException e) {
                    System.out.println("Could not write refresh token to file");
                }
                System.out.println("Starting album cover updater thread");
                Main.albumArtUpdater = new Thread(new AlbumArtUpdater());
                Main.albumArtUpdater.start();
            }
            else {
                System.out.println("Failed to link Spotify account");
            }
        }
    }

    public void addToSessionUsersTable(String columnName, String value) {

    }

    public void removeFromSessionUsersTable(String columnName, String value) {

    }
}
