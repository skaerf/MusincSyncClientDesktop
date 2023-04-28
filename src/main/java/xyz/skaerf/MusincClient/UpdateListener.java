package xyz.skaerf.MusincClient;

import java.io.IOException;

public class UpdateListener implements Runnable {

    private boolean runner = true;

    public UpdateListener() {

    }


    @Override
    public void run() {
        while (runner) {
            String message;
            try {
                System.out.println("god hlp me");
                if (!(message = Main.reader.readLine()).equalsIgnoreCase("")) {
                    System.out.println(message);
                    String arg = message.split(";")[0] + ";";
                    String[] data = message.split(";")[1].split(":!:");
                    if (arg.equalsIgnoreCase(RequestArgs.UPDATE_PLAYING)) {
                        System.out.println("Received new song information");
                        Main.mainGUI.refreshPlayingInfo(data[0], data[1], data[2]);
                    }
                }
            } catch (IOException e) {
                System.out.println("Could not read from server - UpdateListener");
            }
        }
    }
}
