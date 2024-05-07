/*
 * Created by JFormDesigner on Thu Jul 13 17:26:20 BST 2023
 */

package xyz.skaerf.MusincClient.GUIs;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import javax.swing.table.*;
import xyz.skaerf.MusincClient.AlbumArtUpdater;
import xyz.skaerf.MusincClient.Main;
import xyz.skaerf.MusincClient.ProgressBarUpdater;
import xyz.skaerf.MusincClient.RequestArgs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * @author Lawrence Harrison
 */
public class JFormMain {

    private static Thread progressBarUpdater;
    public JFormMain() {
        initComponents();
    }

    public JFrame getMusinc() {
        return Musinc;
    }

    private void playPauseButtonClicked(ActionEvent e) {
        String response = Main.makeRequest(RequestArgs.PLAY_PAUSE);
        if (response != null) {
            String arg = response.split(";")[0]+";";
            if (arg.equals(RequestArgs.ACCEPTED)) {
                String result = response.split(";")[1];
                if (result.equalsIgnoreCase("paused")) {
                    ProgressBarUpdater.pause();
                }
                else if (result.equalsIgnoreCase("playing")) {
                    ProgressBarUpdater.play();
                }
            }
        }
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
        Main.closeSocket();
        Main.loginGUI.startFrame();
        Musinc.setVisible(false);
        Musinc.dispose();
    }

    private void newSessionRequest(ActionEvent e) {
        String response = Main.makeRequest(RequestArgs.CREATE_SESSION);
        if (response != null) {
            String code = response.split(";")[1];
            System.out.println("New session created with ID "+code);
            // TODO add information to sessionUsers or use something other than a table - list?
            showSessionCreatedDialog(code);
        }
    }

    private void showSessionCreatedDialog(String code) {
        displayCode.setText("Your code is "+code);
        sessionCreatedDialog.repaint();
        SwingUtilities.invokeLater(() -> sessionCreatedDialog.setVisible(true));
    }

    public static void updateProgressBar(long millisElapsed, long millisTotal, boolean fromUpdater) {
        //System.out.println(millisElapsed+" elapsed, "+millisTotal+" total");
        if (fromUpdater) {
            int value = Math.toIntExact((long) (((float)millisElapsed/(float)millisTotal)*100));
            progressBar.setValue(value);
        }
        else {
            if (progressBarUpdater != null) {
                progressBarUpdater.interrupt();
            }
            progressBarUpdater = new Thread(new ProgressBarUpdater(millisElapsed, millisTotal));
            progressBarUpdater.start();
        }
        long secsVal = (millisElapsed/1000)%60;
        String pre = "";
        if (secsVal <= 9) pre = "0";
        progress.setText(((millisElapsed/1000)/60)+":"+pre+((millisElapsed/1000)%60));
    }

    public static void updateQueuedSongs(ArrayList<ArrayList<String>> songs) {
        DefaultTableModel model = (DefaultTableModel) queuedSongs.getModel();
        model.setRowCount(0);
        if (songs == null) return;
        int queueNumber = 1;
        for (ArrayList<String> song : songs) {
            model.addRow(new Object[]{String.valueOf(queueNumber), song.get(0), song.get(1)});
            queueNumber++;
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

    private void joinSessionClicked(ActionEvent e) {
        // this one is for the Join Session button in the dropdown menu
        joinSessionPane.setVisible(true);
    }

    private void joinSessionButtonClicked(ActionEvent e) {
        // this one is for the Join button on the pane
        joinSessionPane.setVisible(false);
        String response = Main.makeRequest(RequestArgs.JOIN_SESSION + sessionCode.getText());
        if (response != null) {
            String arg = response.split(";")[0]+";";
            if (arg.equals(RequestArgs.DENIED)) {
                System.out.println("Request to join session was denied");
                return;
            }
            String[] result = response.split(";")[1].split(":!:");
            System.out.println("Accepted. Host user is "+result[0]);
            // grab and update playing info
            response = Main.makeRequest(RequestArgs.GET_SESSION_QUEUE_UPDATE_PLAYING);
            if (response != null) {
                arg = response.split(";")[0]+";";
                if (arg.equals(RequestArgs.DENIED)) {
                    System.out.println("Server denied request for updated song and queue info for Session");
                    return;
                }
                result = response.split(";")[1].split(":!:");
                JFormMainManager.refreshPlayingInfo(result[0], result[1], result[2], Long.parseLong(result[3].split("/")[0]), Long.parseLong(result[3].split("/")[1]));
                String queue = result[4];
                String[] songs = queue.split(":!:");
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
                System.out.println("Client users are "+result[1]);
            }
            catch (ArrayIndexOutOfBoundsException ignored) {
                System.out.println("You're the only client (so far!)");
            }
        }
    }

    private void copyAndCloseClicked(MouseEvent e) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection strSel = new StringSelection(displayCode.getText().replace("Your code is ", ""));
        clip.setContents(strSel, strSel);
        System.out.println("Put code in clipboard");
        sessionCreatedDialog.setVisible(false);
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
        progress = new JLabel();
        progressBar = new JProgressBar();
        length = new JLabel();
        previousButton = new JButton();
        playPauseButton = new JButton();
        nextButton = new JButton();
        sessionUsers = new JTable();
        queuedSongs = new JTable();
        sessionLabel = new JLabel();
        queueLabel = new JLabel();
        about = new JDialog();
        label5 = new JLabel();
        label6 = new JLabel();
        label7 = new JLabel();
        label8 = new JLabel();
        joinSessionPane = new JDialog();
        sessionCode = new JTextField();
        joinSessionButton = new JButton();
        warning = new JLabel();
        sessionCreatedDialog = new JDialog();
        created = new JLabel();
        displayCode = new JLabel();
        copyAndClose = new JButton();

        //======== Musinc ========
        {
            Musinc.setTitle("Musinc");
            Musinc.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            Musinc.setBackground(new Color(0xf2f2f2));
            Musinc.setMinimumSize(new Dimension(500, 400));
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
                    joinSession.addActionListener(e -> joinSessionClicked(e));
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
                    songArtist.setText("Pley something on Spotify!");
                    songArtist.setFont(songArtist.getFont().deriveFont(songArtist.getFont().getStyle() | Font.BOLD));
                    songInfoPanel.add(songArtist, new GridBagConstraints(1, 0, 3, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- songName ----
                    songName.setText("Musinc > Manual Refresh");
                    songName.setFont(songName.getFont().deriveFont(songName.getFont().getStyle() & ~Font.BOLD));
                    songName.setAutoscrolls(true);
                    songInfoPanel.add(songName, new GridBagConstraints(1, 1, 3, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- progress ----
                    progress.setText("0:00");
                    songInfoPanel.add(progress, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 5), 0, 0));
                    songInfoPanel.add(progressBar, new GridBagConstraints(1, 2, 3, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- length ----
                    length.setText("0:00");
                    songInfoPanel.add(length, new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- previousButton ----
                    previousButton.setIcon(new ImageIcon("C:\\Users\\lawre\\IdeaProjects\\MusincClient\\ico\\previous.png"));
                    previousButton.addActionListener(e -> previousTrack(e));
                    songInfoPanel.add(previousButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- playPauseButton ----
                    playPauseButton.setIcon(new ImageIcon("C:\\Users\\lawre\\IdeaProjects\\MusincClient\\ico\\playpause.png"));
                    playPauseButton.setMnemonic(' ');
                    playPauseButton.addActionListener(e -> playPauseButtonClicked(e));
                    songInfoPanel.add(playPauseButton, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- nextButton ----
                    nextButton.setIcon(new ImageIcon("C:\\Users\\lawre\\IdeaProjects\\MusincClient\\ico\\next.png"));
                    nextButton.addActionListener(e -> nextTrack(e));
                    songInfoPanel.add(nextButton, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));
                }

                GroupLayout playerPanelLayout = new GroupLayout(playerPanel);
                playerPanel.setLayout(playerPanelLayout);
                playerPanelLayout.setHorizontalGroup(
                    playerPanelLayout.createParallelGroup()
                        .addGroup(playerPanelLayout.createSequentialGroup()
                            .addGroup(playerPanelLayout.createParallelGroup()
                                .addGroup(playerPanelLayout.createSequentialGroup()
                                    .addGap(53, 53, 53)
                                    .addComponent(albumCover, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE))
                                .addComponent(songInfoPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGap(12, 12, 12))
                );
                playerPanelLayout.setVerticalGroup(
                    playerPanelLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, playerPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(albumCover, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(songInfoPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(15, 15, 15))
                );
            }

            //---- queuedSongs ----
            queuedSongs.setModel(new DefaultTableModel(
                new Object[][] {
                    {null, null, null},
                },
                new String[] {
                    null, null, null
                }
            ) {
                boolean[] columnEditable = new boolean[] {
                    false, false, false
                };
                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return columnEditable[columnIndex];
                }
            });
            {
                TableColumnModel cm = queuedSongs.getColumnModel();
                cm.getColumn(0).setMaxWidth(2);
                cm.getColumn(0).setPreferredWidth(2);
            }

            //---- sessionLabel ----
            sessionLabel.setText("Session");
            sessionLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            sessionLabel.setHorizontalAlignment(SwingConstants.CENTER);
            sessionLabel.setLabelFor(sessionUsers);

            //---- queueLabel ----
            queueLabel.setText("Queue");
            queueLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            queueLabel.setHorizontalAlignment(SwingConstants.CENTER);
            queueLabel.setLabelFor(queuedSongs);

            GroupLayout MusincContentPaneLayout = new GroupLayout(MusincContentPane);
            MusincContentPane.setLayout(MusincContentPaneLayout);
            MusincContentPaneLayout.setHorizontalGroup(
                MusincContentPaneLayout.createParallelGroup()
                    .addGroup(MusincContentPaneLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(playerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(MusincContentPaneLayout.createParallelGroup()
                            .addComponent(queueLabel, GroupLayout.PREFERRED_SIZE, 358, GroupLayout.PREFERRED_SIZE)
                            .addComponent(queuedSongs, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(GroupLayout.Alignment.TRAILING, MusincContentPaneLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(sessionLabel, GroupLayout.PREFERRED_SIZE, 358, GroupLayout.PREFERRED_SIZE))
                            .addComponent(sessionUsers, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
            );
            MusincContentPaneLayout.setVerticalGroup(
                MusincContentPaneLayout.createParallelGroup()
                    .addGroup(MusincContentPaneLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(playerPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(MusincContentPaneLayout.createSequentialGroup()
                        .addComponent(queueLabel)
                        .addGap(5, 5, 5)
                        .addComponent(queuedSongs, GroupLayout.PREFERRED_SIZE, 168, GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(sessionLabel)
                        .addGap(5, 5, 5)
                        .addComponent(sessionUsers, GroupLayout.PREFERRED_SIZE, 162, GroupLayout.PREFERRED_SIZE))
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
            label5.setText("Musinc");
            label5.setFont(new Font("Segoe UI", Font.BOLD, 22));
            label5.setForeground(new Color(0xff3333));
            label5.setHorizontalAlignment(SwingConstants.CENTER);

            //---- label6 ----
            label6.setText("Designed and written by");
            label6.setHorizontalAlignment(SwingConstants.CENTER);

            //---- label7 ----
            label7.setText("Lawrence Harrison");
            label7.setHorizontalAlignment(SwingConstants.CENTER);

            //---- label8 ----
            label8.setText("Copyright \u00a92024");
            label8.setHorizontalAlignment(SwingConstants.CENTER);

            GroupLayout aboutContentPaneLayout = new GroupLayout(aboutContentPane);
            aboutContentPane.setLayout(aboutContentPaneLayout);
            aboutContentPaneLayout.setHorizontalGroup(
                aboutContentPaneLayout.createParallelGroup()
                    .addGroup(aboutContentPaneLayout.createSequentialGroup()
                        .addComponent(label5, GroupLayout.PREFERRED_SIZE, 198, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(aboutContentPaneLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(aboutContentPaneLayout.createParallelGroup()
                            .addComponent(label8, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(label7, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(label6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
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
                        .addContainerGap())
            );
            about.setSize(200, 200);
            about.setLocationRelativeTo(about.getOwner());
        }

        //======== joinSessionPane ========
        {
            joinSessionPane.setTitle("Join Session");
            joinSessionPane.setAlwaysOnTop(true);
            joinSessionPane.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            joinSessionPane.setResizable(false);
            joinSessionPane.setType(Window.Type.POPUP);
            var joinSessionPaneContentPane = joinSessionPane.getContentPane();

            //---- sessionCode ----
            sessionCode.setToolTipText("Session Code");

            //---- joinSessionButton ----
            joinSessionButton.setText("Join");
            joinSessionButton.addActionListener(e -> joinSessionButtonClicked(e));

            //---- warning ----
            warning.setText("Please clear your Spotify queue first!");
            warning.setFont(warning.getFont().deriveFont(warning.getFont().getStyle() & ~Font.BOLD));

            GroupLayout joinSessionPaneContentPaneLayout = new GroupLayout(joinSessionPaneContentPane);
            joinSessionPaneContentPane.setLayout(joinSessionPaneContentPaneLayout);
            joinSessionPaneContentPaneLayout.setHorizontalGroup(
                joinSessionPaneContentPaneLayout.createParallelGroup()
                    .addGroup(joinSessionPaneContentPaneLayout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addGroup(joinSessionPaneContentPaneLayout.createParallelGroup()
                            .addComponent(sessionCode, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
                            .addComponent(joinSessionButton))
                        .addGap(61, 65, Short.MAX_VALUE))
                    .addComponent(warning, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
            );
            joinSessionPaneContentPaneLayout.setVerticalGroup(
                joinSessionPaneContentPaneLayout.createParallelGroup()
                    .addGroup(joinSessionPaneContentPaneLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(warning, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sessionCode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(joinSessionButton)
                        .addContainerGap())
            );
            joinSessionPane.setSize(210, 200);
            joinSessionPane.setLocationRelativeTo(joinSessionPane.getOwner());
        }

        //======== sessionCreatedDialog ========
        {
            sessionCreatedDialog.setAlwaysOnTop(true);
            sessionCreatedDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            sessionCreatedDialog.setTitle("Create Session");
            var sessionCreatedDialogContentPane = sessionCreatedDialog.getContentPane();

            //---- created ----
            created.setText("Your Session has been created.");
            created.setHorizontalAlignment(SwingConstants.CENTER);

            //---- displayCode ----
            displayCode.setText("Your code is ");
            displayCode.setHorizontalAlignment(SwingConstants.CENTER);

            //---- copyAndClose ----
            copyAndClose.setText("Copy and close");
            copyAndClose.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    copyAndCloseClicked(e);
                }
            });

            GroupLayout sessionCreatedDialogContentPaneLayout = new GroupLayout(sessionCreatedDialogContentPane);
            sessionCreatedDialogContentPane.setLayout(sessionCreatedDialogContentPaneLayout);
            sessionCreatedDialogContentPaneLayout.setHorizontalGroup(
                sessionCreatedDialogContentPaneLayout.createParallelGroup()
                    .addGroup(sessionCreatedDialogContentPaneLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(sessionCreatedDialogContentPaneLayout.createParallelGroup()
                            .addComponent(created, GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                            .addComponent(displayCode, GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(sessionCreatedDialogContentPaneLayout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(copyAndClose, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(37, Short.MAX_VALUE))
            );
            sessionCreatedDialogContentPaneLayout.setVerticalGroup(
                sessionCreatedDialogContentPaneLayout.createParallelGroup()
                    .addGroup(sessionCreatedDialogContentPaneLayout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(created)
                        .addGap(18, 18, 18)
                        .addComponent(displayCode)
                        .addGap(18, 18, 18)
                        .addComponent(copyAndClose)
                        .addContainerGap(37, Short.MAX_VALUE))
            );
            sessionCreatedDialog.pack();
            sessionCreatedDialog.setLocationRelativeTo(sessionCreatedDialog.getOwner());
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
    public static JLabel progress;
    private static JProgressBar progressBar;
    public static JLabel length;
    private JButton previousButton;
    private JButton playPauseButton;
    private JButton nextButton;
    public static JTable sessionUsers;
    public static JTable queuedSongs;
    private JLabel sessionLabel;
    private JLabel queueLabel;
    private JDialog about;
    private JLabel label5;
    private JLabel label6;
    private JLabel label7;
    private JLabel label8;
    private JDialog joinSessionPane;
    private JTextField sessionCode;
    private JButton joinSessionButton;
    private JLabel warning;
    private static JDialog sessionCreatedDialog;
    private JLabel created;
    private JLabel displayCode;
    private JButton copyAndClose;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
