package xyz.skaerf.MusincClient;

import xyz.skaerf.MusincClient.GUIs.JFormMain;

import java.util.concurrent.TimeUnit;

public class ProgressBarUpdater implements Runnable {

    private long millisElapsed;
    private final long millisTotal;
    private long lastUpdate;
    private static boolean go = true;
    private static boolean paused = false;

    public ProgressBarUpdater(long millisElapsed, long millisTotal) {
        this.millisElapsed = millisElapsed;
        this.millisTotal = millisTotal;
        this.lastUpdate = System.currentTimeMillis();
    }

    public static void pause() {
        paused = true;
    }

    public static void play() {
        paused = false;
    }

    @Override
    public void run() {
        System.out.println("Started progress bar updater thread");
        while (go) {
            try {
                if (!paused) {
                    TimeUnit.MILLISECONDS.sleep(100); // this is doubly as fast as necessary to maintain accuracy
                    millisElapsed = millisElapsed + (System.currentTimeMillis() - lastUpdate);
                    if (millisElapsed >= millisTotal) {
                        // runs if the amount of time elapsed of the song is greater than the length of the song
                        // for some reason this is ALWAYS wrong - the song is usually about five seconds longer than Spotify says it is
                        // doing this rather than using updatePlayerInfoSlowly() because the time can be shorter without bugging
                        // updating the progress bar one last time because it was annoying me that it never got to 100%
                        JFormMain.updateProgressBar(millisTotal, millisTotal, true);
                        TimeUnit.SECONDS.sleep(2);
                        AlbumArtUpdater.updatePlayerInfo();
                        break; // go = false;
                    }
                    lastUpdate = System.currentTimeMillis();
                    JFormMain.updateProgressBar(millisElapsed, millisTotal, true);
                }
            }
            catch (InterruptedException e) {
                System.out.println("ProgressBarUpdater thread was interrupted");
                break;
            }
        }
    }
}
