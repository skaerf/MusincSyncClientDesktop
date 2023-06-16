package xyz.skaerf.MusincClient.GUIs;

import com.sun.net.httpserver.HttpServer;
import xyz.skaerf.MusincClient.AlbumArtUpdater;
import xyz.skaerf.MusincClient.Main;
import xyz.skaerf.MusincClient.RequestArgs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

public class MainGUI {

    private final BaseGUI base;
    private String albumCoverPath;
    private JPanel menuBarPanel;
    private JPanel playerPanel;
    private JLabel albumCover;
    private String refreshToken;

    public MainGUI() {
        base = new BaseGUI(true);
    }

    public void startFrame() {
        addButtons();
        addMenuBar();
        base.getFrame().pack();
        base.getFrame().setVisible(true);
    }

    private void addButtons() {
        // add album cover label and position it in center-left of screen
        playerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        albumCover = new JLabel();
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.weighty = 0.5;
        c.insets = new Insets(0, 20, 0, 20);
        albumCover.setMaximumSize(new Dimension(50, 50));
        albumCover.setSize(new Dimension(50, 50));
        playerPanel.add(albumCover, c);

        playerPanel.setVisible(true);
        base.getFrame().add(playerPanel);
    }


    private void addMenuBar() {
        menuBarPanel = new JPanel(new GridBagLayout());
        JMenuBar bar = new JMenuBar();
        JMenu musincMenu = new JMenu("Musinc");
        JMenuItem spotify = new JMenuItem("Add Spotify Account");
        JMenuItem deezer = new JMenuItem("Add Deezer Account");
        musincMenu.add(spotify);
        musincMenu.add(deezer);
        bar.add(musincMenu);
        spotify.addActionListener(e -> this.makeSpotifyAccountRequest());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
        c.insets = new Insets(10, 10, 0, 0);
        c.anchor = GridBagConstraints.NORTHWEST;
        JPanel barPanel = new JPanel(new GridBagLayout());
        barPanel.add(bar);
        barPanel.setVisible(true);
        barPanel.setBounds(0,0, base.getFrame().getWidth(), 100);
        barPanel.setLocation(new Point(0,0));
        menuBarPanel.add(barPanel);
        menuBarPanel.setVisible(true);
        base.getFrame().add(menuBarPanel);
    }

    private void makeSpotifyAccountRequest() {
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
                            this.sendSpotifyCode(authCode);
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


    private void sendSpotifyCode(String[] authCode) {
        String arg;
        String[] data;
        String response = Main.makeRequest(RequestArgs.GENERAL + authCode[0]);
        if (response != null) {
            arg = response.split(";")[0]+";";
            if (arg.equalsIgnoreCase(RequestArgs.ACCEPTED)) {
                System.out.println("Successfully linked Spotify account");
                try {
                    data = response.split(";")[1].split(":!:");
                    this.refreshToken = data[0];
                    System.out.println(Arrays.toString(data));
                    FileWriter fileWriter = new FileWriter(Main.configFile);
                    Main.configJson.put("refreshToken", this.refreshToken);
                    fileWriter.write(Main.configJson.toJSONString());
                    fileWriter.close();
                    albumCoverPath = data[1];
                    this.refreshPlayingInfo(null, null, null, null);
                }
                catch (NullPointerException e) {
                    System.out.println("User not currently playing any music");
                }
                catch (IOException e) {
                    System.out.println("Could not write refresh token to file");
                }
                System.out.println("Starting album cover updater thread");
                Main.albumArtUpdater = new Thread(new AlbumArtUpdater(this));
                Main.albumArtUpdater.start();
            }
            else {
                System.out.println("Failed to link Spotify account");
            }
        }
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public void refreshPlayingInfo(String albumCoverPath, String songTitle, String songArtist, Long timestamp) {
        if (albumCoverPath == null) albumCoverPath = this.albumCoverPath;
        try {
            ImageIcon newIcon = new ImageIcon(ImageIO.read(new URL(albumCoverPath)));
            if (albumCover == null) {
                albumCover = new JLabel();
                albumCover.setMaximumSize(new Dimension(640, 640));
                playerPanel.add(albumCover);
            }
            albumCover.setIcon(newIcon);
        }
        catch (IOException e) {
            System.out.println("Could not read album art link");
        }
        playerPanel.revalidate();
        playerPanel.repaint();
    }
}
