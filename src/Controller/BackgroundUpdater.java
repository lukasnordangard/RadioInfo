package Controller;

import Model.Channel;

import javax.swing.*;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Class responsible for background updates in the GUI.
 */
public class BackgroundUpdater {

    // Attributes
    private final GuiController guiController;
    private final ApiController apiController;
    private final Timer timer;
    private TimerTask currentTimerTask;

    /**
     * Constructor method that initializes BackgroundUpdater.
     *
     * @param guiController GuiController instance.
     * @param apiController ApiController instance.
     */
    public BackgroundUpdater(GuiController guiController, ApiController apiController) {
        this.timer = new Timer();
        this.guiController = guiController;
        this.apiController = apiController;
    }

    /**
     * Updates programs for a specific channel with a timed interval.
     *
     * @param channelId The ID of the channel to update programs for.
     */
    public void updateChannelScheduleWithTimer(int channelId) {
        int updateTime = 60;

        // Cancel the current TimerTask if it exists
        if (currentTimerTask != null) {
            currentTimerTask.cancel();
        }

        currentTimerTask = new TimerTask() {
            @Override
            public void run() {
                //guiController.updateChannelSchedule(channelId);
                guiController.refreshTable();
            }
        };

        // Schedule the new TimerTask
        timer.scheduleAtFixedRate(currentTimerTask, 0, TimeUnit.MINUTES.toMillis(updateTime));
    }

    public void updateCachedSchedules(List<Channel> cachedChannels) {
        SwingWorker<Void, Void> updateChannelsWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                for (Channel channel : cachedChannels){
                    guiController.updateChannelSchedule(channel.getId());
                }
                return null;
            }

            @Override
            protected void done() {
                guiController.refreshTable();
            }
        };
        updateChannelsWorker.execute();
    }

    /**
     * Updates channels in the background and refreshes the menu bar.
     */
    public void updateChannels() {
        // Perform channel updates in the background
        SwingWorker<Void, Void> updateChannelsWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                //apiController.loadChannels();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException | ExecutionException e) {
                    handleUncaughtException(e);
                }
                //guiController.updateChannelMenus();
            }
        };
        updateChannelsWorker.execute();
    }

    private void handleUncaughtException(Exception e) {
        if (e.getCause() instanceof UnknownHostException) {
            guiController.showErrorDialog("API host not reachable. Please check your internet connection.");
        } else if (e.getCause() instanceof SocketException){
            guiController.showErrorDialog("(Network unreachable or other socket-related issues)");
        } else{
            guiController.showErrorDialog("An unexpected error occurred.");
        }
    }

    public void printMethod(String s){
        if (SwingUtilities.isEventDispatchThread()) {
            System.out.println(s);
        } else {
            System.out.println("\t"+s);
        }
    }
}
