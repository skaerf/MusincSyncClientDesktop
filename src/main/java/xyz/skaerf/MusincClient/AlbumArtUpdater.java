package xyz.skaerf.MusincClient;

import xyz.skaerf.MusincClient.GUIs.MainGUI;

import java.util.concurrent.TimeUnit;

public class AlbumArtUpdater implements Runnable {

    private String albumCover;
    private String songName = "";
    private String artist;
    private boolean runner = true;
    private final MainGUI gui;
    private int counter;

    public AlbumArtUpdater(MainGUI gui) {
        this.gui = gui;
    }

    @Override
    public void run() {
        while (runner) {
            getAlbumCover();
            if (counter == 3) {
                gui.refreshPlayingInfo(albumCover, songName, artist);
                counter = 0;
            }
            counter+=1;
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            }
            catch (InterruptedException e) {
                System.out.println("Could not sleep between album data requests");
            }
        }
    }

    private void getAlbumCover() {
        String response = Main.makeRequest(RequestArgs.UPDATE_PLAYING);
        String[] data;
        if (response != null && (response.split(";")[0]+";").equalsIgnoreCase(RequestArgs.ACCEPTED)) {
            data = response.split(";")[1].split(":!:");
            // 0 = album cover link, 1 = song name, 2 = main artist
            if (!songName.equalsIgnoreCase(data[1])) {
                albumCover = data[0];
                songName = data[1];
                artist = data[2];
                System.out.println(data[0]+"\n"+data[1]+"\n"+data[2]);
                gui.refreshPlayingInfo(data[0], data[1], data[2]);
            }
            songName = data[1];
        }
    }

    public void kill() {
        runner = false;
    }
}
