/*
 * Created by JFormDesigner on Thu Jul 13 17:26:20 BST 2023
 */

package xyz.skaerf.MusincClient.GUIs;

import xyz.skaerf.MusincClient.AlbumArtUpdater;
import xyz.skaerf.MusincClient.Main;
import xyz.skaerf.MusincClient.RequestArgs;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

/**
 * @author Lawrence Harrison
 */
public class JFormMain {
    public JFormMain() {
        initComponents();
    }

    public JFrame getMusinc() {
        return Musinc;
    }

    private void playPauseButtonClicked(ActionEvent e) {
        Main.makeRequest(RequestArgs.PLAY_PAUSE);
    }

    private void aboutButtonMouseClicked(ActionEvent e) {
        SwingUtilities.invokeLater(() -> about.setVisible(true));
    }

    private void manualRefreshRequest(ActionEvent e) {
        AlbumArtUpdater.updatePlayerInfo();
    }

    private void spotifyAccountLink(ActionEvent e) {
        JFormMainManager.makeSpotifyAccountRequest();
    }

    private void logOut(ActionEvent e) {
        Main.wipeContentsOfDataFile();

        Main.loginGUI.startFrame();
        Musinc.setVisible(false);
        Musinc.dispose();
    }

    private void newSessionRequest(ActionEvent e) {
        String response = Main.makeRequest(RequestArgs.CREATE_SESSION);
        if (response != null) {
            System.out.println("New session created with ID "+response.split(";")[1]);
            // TODO add information to sessionUsers or use something other than a table - list?
        }
    }

    private void previousTrack(ActionEvent e) {
        String response = Main.makeRequest(RequestArgs.PREVIOUS_TRACK);
        if (response != null) {
            String arg = response.split(";")[0]+";";
            songName.setText("Updating..");
            songArtist.setText("Updating..");
            if (arg.equalsIgnoreCase(RequestArgs.ACCEPTED)) AlbumArtUpdater.updatePlayerInfoSlowly();
        }
    }

    private void nextTrack(ActionEvent e) {
        String response = Main.makeRequest(RequestArgs.NEXT_TRACK);
        if (response != null) {
            String arg = response.split(";")[0]+";";
            songName.setText("Updating..");
            songArtist.setText("Updating..");
            if (arg.equalsIgnoreCase(RequestArgs.ACCEPTED)) AlbumArtUpdater.updatePlayerInfoSlowly();
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Educational license - Lawrence Harrison (MR L A J HARRISON)
        Musinc = new JFrame();
        menuBar = new JMenuBar();
        musincMenu = new JMenu();
        manualRefresh = new JMenuItem();
        aboutButton = new JMenuItem();
        session = new JMenu();
        newSession = new JMenuItem();
        joinSession = new JMenuItem();
        accountMenu = new JMenu();
        spotify = new JMenuItem();
        deezer = new JMenuItem();
        logOut = new JMenuItem();
        playerPanel = new JPanel();
        albumCover = new JLabel();
        songInfoPanel = new JPanel();
        songArtist = new JLabel();
        songName = new JLabel();
        progressBar = new JProgressBar();
        previousButton = new JButton();
        playPauseButton = new JButton();
        nextButton = new JButton();
        sessionUsers = new JTable();
        about = new JDialog();
        label5 = new JLabel();
        label6 = new JLabel();
        label7 = new JLabel();
        label8 = new JLabel();
        dialog1 = new JDialog();

        //======== Musinc ========
        {
            Musinc.setTitle("Musinc");
            Musinc.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            Musinc.setBackground(new Color(0xf2f2f2));
            Musinc.setMinimumSize(new Dimension(500, 500));
            var MusincContentPane = Musinc.getContentPane();

            //======== menuBar ========
            {

                //======== musincMenu ========
                {
                    musincMenu.setText("Musinc");

                    //---- manualRefresh ----
                    manualRefresh.setText("Manual Refresh");
                    manualRefresh.addActionListener(e -> manualRefreshRequest(e));
                    musincMenu.add(manualRefresh);

                    //---- aboutButton ----
                    aboutButton.setText("About Musinc");
                    aboutButton.addActionListener(e -> aboutButtonMouseClicked(e));
                    musincMenu.add(aboutButton);
                }
                menuBar.add(musincMenu);

                //======== session ========
                {
                    session.setText("Sessions");

                    //---- newSession ----
                    newSession.setText("New Session..");
                    newSession.addActionListener(e -> newSessionRequest(e));
                    session.add(newSession);

                    //---- joinSession ----
                    joinSession.setText("Join Session..");
                    session.add(joinSession);
                }
                menuBar.add(session);

                //======== accountMenu ========
                {
                    accountMenu.setText("Account");

                    //---- spotify ----
                    spotify.setText("Link Spotify Account");
                    spotify.addActionListener(e -> spotifyAccountLink(e));
                    accountMenu.add(spotify);

                    //---- deezer ----
                    deezer.setText("Link Deezer Account");
                    accountMenu.add(deezer);

                    //---- logOut ----
                    logOut.setText("Log Out..");
                    logOut.addActionListener(e -> logOut(e));
                    accountMenu.add(logOut);
                }
                menuBar.add(accountMenu);
            }
            Musinc.setJMenuBar(menuBar);

            //======== playerPanel ========
            {

                //---- albumCover ----
                albumCover.setIcon(UIManager.getIcon("FileView.fileIcon"));

                //======== songInfoPanel ========
                {
                    songInfoPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)songInfoPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
                    ((GridBagLayout)songInfoPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};

                    //---- songArtist ----
                    songArtist.setText("song artist");
                    songArtist.setFont(songArtist.getFont().deriveFont(songArtist.getFont().getStyle() | Font.BOLD));
                    songInfoPanel.add(songArtist, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- songName ----
                    songName.setText("song name");
                    songName.setFont(songName.getFont().deriveFont(songName.getFont().getStyle() & ~Font.BOLD));
                    songInfoPanel.add(songName, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                        new Insets(0, 0, 5, 0), 0, 0));
                    songInfoPanel.add(progressBar, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- previousButton ----
                    previousButton.setText("previous");
                    previousButton.addActionListener(e -> previousTrack(e));
                    songInfoPanel.add(previousButton, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- playPauseButton ----
                    playPauseButton.setText("play/pause");
                    playPauseButton.addActionListener(e -> playPauseButtonClicked(e));
                    songInfoPanel.add(playPauseButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- nextButton ----
                    nextButton.setText("next");
                    nextButton.addActionListener(e -> nextTrack(e));
                    songInfoPanel.add(nextButton, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }

                GroupLayout playerPanelLayout = new GroupLayout(playerPanel);
                playerPanel.setLayout(playerPanelLayout);
                playerPanelLayout.setHorizontalGroup(
                    playerPanelLayout.createParallelGroup()
                        .addGroup(playerPanelLayout.createSequentialGroup()
                            .addGap(25, 25, 25)
                            .addGroup(playerPanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(albumCover, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                                .addComponent(songInfoPanel, GroupLayout.PREFERRED_SIZE, 270, GroupLayout.PREFERRED_SIZE))
                            .addGap(26, 26, 26))
                );
                playerPanelLayout.setVerticalGroup(
                    playerPanelLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, playerPanelLayout.createSequentialGroup()
                            .addContainerGap(11, Short.MAX_VALUE)
                            .addComponent(albumCover, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(songInfoPanel, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                            .addGap(27, 27, 27))
                );
            }

            GroupLayout MusincContentPaneLayout = new GroupLayout(MusincContentPane);
            MusincContentPane.setLayout(MusincContentPaneLayout);
            MusincContentPaneLayout.setHorizontalGroup(
                MusincContentPaneLayout.createParallelGroup()
                    .addGroup(MusincContentPaneLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(playerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(sessionUsers, GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
                        .addContainerGap())
            );
            MusincContentPaneLayout.setVerticalGroup(
                MusincContentPaneLayout.createParallelGroup()
                    .addGroup(MusincContentPaneLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(MusincContentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(sessionUsers, GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                            .addComponent(playerPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(12, 12, 12))
            );
            Musinc.pack();
            Musinc.setLocationRelativeTo(null);
        }

        //======== about ========
        {
            about.setAlwaysOnTop(true);
            about.setResizable(false);
            about.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            about.setTitle("About");
            about.setType(Window.Type.POPUP);
            var aboutContentPane = about.getContentPane();

            //---- label5 ----
            label5.setText("          Musinc");
            label5.setFont(label5.getFont().deriveFont(label5.getFont().getStyle() | Font.BOLD, label5.getFont().getSize() + 10f));
            label5.setForeground(new Color(0xff3333));

            //---- label6 ----
            label6.setText("           Designed and written by");

            //---- label7 ----
            label7.setText("Lawrence Harrison");

            //---- label8 ----
            label8.setText("Copyright 2023");

            GroupLayout aboutContentPaneLayout = new GroupLayout(aboutContentPane);
            aboutContentPane.setLayout(aboutContentPaneLayout);
            aboutContentPaneLayout.setHorizontalGroup(
                aboutContentPaneLayout.createParallelGroup()
                    .addComponent(label6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(aboutContentPaneLayout.createSequentialGroup()
                        .addGroup(aboutContentPaneLayout.createParallelGroup()
                            .addGroup(aboutContentPaneLayout.createSequentialGroup()
                                .addGap(58, 58, 58)
                                .addComponent(label8))
                            .addGroup(aboutContentPaneLayout.createSequentialGroup()
                                .addGap(49, 49, 49)
                                .addComponent(label7))
                            .addComponent(label5, GroupLayout.PREFERRED_SIZE, 198, GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
            );
            aboutContentPaneLayout.setVerticalGroup(
                aboutContentPaneLayout.createParallelGroup()
                    .addGroup(aboutContentPaneLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(label5)
                        .addGap(18, 18, 18)
                        .addComponent(label6)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label7)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label8)
                        .addGap(24, 24, 24))
            );
            about.setSize(200, 200);
            about.setLocationRelativeTo(about.getOwner());
        }

        //======== dialog1 ========
        {
            dialog1.setTitle("Join Session");
            dialog1.setAlwaysOnTop(true);
            var dialog1ContentPane = dialog1.getContentPane();
            dialog1ContentPane.setLayout(new GridBagLayout());
            ((GridBagLayout)dialog1ContentPane.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
            ((GridBagLayout)dialog1ContentPane.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
            dialog1.setSize(200, 200);
            dialog1.setLocationRelativeTo(dialog1.getOwner());
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Lawrence Harrison (MR L A J HARRISON)
    private JFrame Musinc;
    private JMenuBar menuBar;
    private JMenu musincMenu;
    private JMenuItem manualRefresh;
    private JMenuItem aboutButton;
    private JMenu session;
    private JMenuItem newSession;
    private JMenuItem joinSession;
    private JMenu accountMenu;
    private JMenuItem spotify;
    private JMenuItem deezer;
    private JMenuItem logOut;
    private JPanel playerPanel;
    public static JLabel albumCover;
    private JPanel songInfoPanel;
    public static JLabel songArtist;
    public static JLabel songName;
    private JProgressBar progressBar;
    private JButton previousButton;
    private JButton playPauseButton;
    private JButton nextButton;
    public static JTable sessionUsers;
    private JDialog about;
    private JLabel label5;
    private JLabel label6;
    private JLabel label7;
    private JLabel label8;
    private JDialog dialog1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
