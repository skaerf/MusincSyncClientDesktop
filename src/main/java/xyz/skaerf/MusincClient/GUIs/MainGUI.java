package xyz.skaerf.MusincClient.GUIs;

import com.sun.net.httpserver.HttpServer;
import xyz.skaerf.MusincClient.Main;
import xyz.skaerf.MusincClient.RequestArgs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class MainGUI {

    private final BaseGUI base;
    private String albumCoverPath;
    private JPanel panel;
    private JLabel albumCover;

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
        panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.insets = new Insets(10, 10, 10, 10);

        base.getFrame().add(panel);
    }

    private void addMenuBar() {
        JMenu musincMenu = new JMenu("Musinc");
        JMenuItem spotify = new JMenuItem("Add Spotify Account");
        JMenuItem deezer = new JMenuItem("Add Deezer Account");
        JMenuBar bar = new JMenuBar();
        musincMenu.add(spotify);
        musincMenu.add(deezer);
        bar.add(musincMenu);
        spotify.addActionListener(e -> {
            String response = Main.makeRequest(RequestArgs.CREATE_SPOTIFY_ACCOUNT);
            if (response != null) {
                String arg = response.split(";")[0]+";";
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
                                this.sendSpotifyCode(response, authCode);
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
                        System.out.println("Could not convert "+data[0]+" to URI format");
                    }
                }
            }
        });
        panel.add(bar);
    }

    private void sendSpotifyCode(String response, String[] authCode) {
        String arg;
        String[] data;
        response = Main.makeRequest(RequestArgs.GENERAL+ authCode[0]);
        if (response != null) {
            arg = response.split(";")[0]+";";
            if (arg.equalsIgnoreCase(RequestArgs.ACCEPTED)) {
                System.out.println("Successfully linked Spotify account");
                data = response.split(";")[1].split(":!:");
                albumCoverPath = data[0];
                this.refreshPlayingInfo(null, null, null);
            }
            else {
                System.out.println("Failed to link Spotify account");
            }
        }
    }

    public void refreshPlayingInfo(String albumCoverPath, String songTitle, String songArtist) {
        if (albumCoverPath == null) albumCoverPath = this.albumCoverPath;
        albumCover = new JLabel();
        albumCover.setMaximumSize(new Dimension(640, 640));
        panel.remove(albumCover);
        try {
            albumCover = new JLabel(new ImageIcon(ImageIO.read(new URL(albumCoverPath))));
        }
        catch (IOException e) {
            System.out.println("Could not read album art link");
        }
        base.getFrame().remove(panel);
        panel.add(albumCover);
        base.getFrame().add(panel);
        base.getFrame().revalidate();
        base.getFrame().repaint();
    }
}
