package xyz.skaerf.MusincClient.GUIs;

import com.sun.net.httpserver.HttpServer;
import org.imgscalr.Scalr;
import xyz.skaerf.MusincClient.AlbumArtUpdater;
import xyz.skaerf.MusincClient.Main;
import xyz.skaerf.MusincClient.RequestArgs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class JFormMainManager {

    private static String refreshToken;

    public static void refreshPlayingInfo(String icon, String songName, String songArtist, Long timestamp, Long songLength) {
        JFormMain.songName.setText(songName);
        JFormMain.songArtist.setText(songArtist);
        long secsVal = (songLength/1000)%60;
        String pre = "";
        if (secsVal <= 9) pre = "0";
        JFormMain.length.setText(((songLength/1000)/60)+":"+pre+((songLength/1000)%60));
        JFormMain.updateProgressBar(timestamp, songLength, false);
        String response = Main.makeRequest(RequestArgs.GET_QUEUE);
        if (response == null || (response.split(";")[0]+";").equalsIgnoreCase(RequestArgs.DENIED)) {
            System.out.println("Request for queue information returned null, skipping");
        }
        else {
            String[] songs = response.split(";")[1].split(":!:");
            ArrayList<ArrayList<String>> list = new ArrayList<>();
            for (String song : songs) {
                System.out.println(song);
                ArrayList<String> arr = new ArrayList<>();
                String[] nameAndArtist = song.split("\\+++");
                arr.add(nameAndArtist[0].substring(1));
                arr.add(nameAndArtist[1].substring(0, nameAndArtist[1].length()-1));
                list.add(arr);
            }
            JFormMain.updateQueuedSongs(list);
        }
        try {
            Image scaledImage = Scalr.resize(ImageIO.read(new URL(icon)), Scalr.Method.ULTRA_QUALITY, 200, Scalr.OP_ANTIALIAS);
            ImageIcon newIcon = new ImageIcon(scaledImage);
            roundTheCorners(JFormMain.albumCover, 20, newIcon);
        }
        catch (IOException e) {
            System.out.println("Could not resolve the URL for the album cover, leaving it the same");
        }
    }

    /**
     * Rounds the corners of a JLabel. Absolutely useless functionally, but it makes it look nice, doesn't it?
     * @param label the component to be rounded
     * @param cornerRadius the radius by which the corners should be cut
     */
    public static void roundTheCorners(JLabel label, int cornerRadius, ImageIcon icon) {
        label.setIcon(icon);
        label.setLayout(new BorderLayout());
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        label.setOpaque(false);
        label.setBackground(new Color(0, 0, 0, 0));
        label.removeAll();
        label.add(new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D graphics = (Graphics2D) g.create();
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int width = getWidth();
                int height = getHeight();
                RoundRectangle2D roundedRect = new RoundRectangle2D.Float(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
                graphics.setClip(roundedRect);
                super.paintComponent(graphics);
                graphics.dispose();
            }
        }, BorderLayout.CENTER);
        label.revalidate();
        label.repaint();
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
