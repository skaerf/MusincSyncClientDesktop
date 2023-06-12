package xyz.skaerf.MusincClient;

import xyz.skaerf.MusincClient.GUIs.MainGUI;

import java.util.concurrent.TimeUnit;

public class AlbumArtUpdater implements Runnable {

    private String albumCover;
    private String songName = "";
    private String artist;
    private long timestamp;
    private boolean runner = true;
    private final MainGUI gui;
    private int counter;
    private boolean denied = false;
    private long time;

    public AlbumArtUpdater(MainGUI gui) {
        this.gui = gui;
    }

    @Override
    public void run() {
        while (runner) {
            getAlbumCover();
            if (counter == 3) {
                if (!this.denied) {
                    gui.refreshPlayingInfo(albumCover, songName, artist, null);
                    counter = 0;
                }
                else {
                    System.out.println("Was denied a request for song information at "+time);
                }
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
            denied = false;
            data = response.split(";")[1].split(":!:");
            // 0 = album cover link, 1 = song name, 2 = main artist, 3 = timestamp in ms
            if (!songName.equalsIgnoreCase(data[1])) {
                albumCover = data[0];
                songName = data[1];
                artist = data[2];
                timestamp = Long.parseLong(data[3]);
                System.out.println(data[0]+"\n"+data[1]+"\n"+data[2]);
                gui.refreshPlayingInfo(data[0], data[1], data[2], Long.parseLong(data[3]));
            }
            songName = data[1];
        }
        else if (response != null && (response.split(";")[0]+";").equalsIgnoreCase(RequestArgs.DENIED)) {
            this.denied = true;
            this.time = System.currentTimeMillis();
        }
    }

    public void kill() {
        runner = false;
    }
}
