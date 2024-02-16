package xyz.skaerf.MusincClient;

import xyz.skaerf.MusincClient.GUIs.JFormMainManager;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class AlbumArtUpdater implements Runnable {

    private static String albumCover;
    private static String songName = "";
    private static String artist;
    private static long timestamp;
    private static long songLength;
    private boolean runner = true;
    private int counter;
    private static boolean denied = false;
    private long timeSongStarted;

    public AlbumArtUpdater() {
    }

    @Override
    public void run() {
        while (runner) {
            runner = false;
            updatePlayerInfo();
            // TODO only refresh information when song is supposed to have ended to lessen requests
            if (counter == 3) {
                if (!denied) {
                    JFormMainManager.refreshPlayingInfo(albumCover, songName, artist, timestamp, songLength);
                    counter = 0;
                }
            }
            counter+=1;
            try {
                TimeUnit.MILLISECONDS.sleep(5000);
            }
            catch (InterruptedException e) {
                System.out.println("Could not sleep between album data requests");
            }
        }
    }

    public static void updatePlayerInfo() {
        String response = Main.makeRequest(RequestArgs.UPDATE_PLAYING);
        System.out.println("request submitted");
        String[] data;
        if (response != null && (response.split(";")[0]+";").equalsIgnoreCase(RequestArgs.ACCEPTED)) {
            System.out.println("request was accepted");
            denied = false;
            data = response.split(";")[1].split(":!:");
            System.out.println(Arrays.toString(data));
            // 0 = album cover link, 1 = song name, 2 = main artist, 3 = timestamp in ms
            if (!songName.equalsIgnoreCase(data[1])) {
                albumCover = data[0];
                songName = data[1];
                artist = data[2];
                timestamp = Long.parseLong(data[3].split("/")[0]);
                songLength = Long.parseLong(data[3].split("/")[1]);
                if (timestamp >= songLength) {
                    updatePlayerInfoSlowly();
                    return;
                }
                System.out.println(data[0]+"\n"+data[1]+"\n"+data[2]+"\n"+data[3]);
                JFormMainManager.refreshPlayingInfo(albumCover, songName, artist, timestamp, songLength);
            }
            songName = data[1];
        }
        else if (response != null && (response.split(";")[0]+";").equalsIgnoreCase(RequestArgs.DENIED)) {
            denied = true;
        }
    }

    public static void updatePlayerInfoSlowly() {
        new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(4500);
                updatePlayerInfo();
            }
            catch (InterruptedException e) {
                System.out.println("Was unable to wait whilst trying to update player info slowly");
            }
        }).start();
    }

    public void kill() {
        runner = false;
    }
}
