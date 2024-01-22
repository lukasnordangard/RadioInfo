package Controller;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;
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
                if (SwingUtilities.isEventDispatchThread()) {
                    System.out.println("EDT");
                } else {
                    System.out.println("Thread");
                }

                guiController.updateChannelSchedule(channelId);
                guiController.refreshTable();
            }
        };

        // Schedule the new TimerTask
        timer.scheduleAtFixedRate(currentTimerTask, 0, TimeUnit.MINUTES.toMillis(updateTime));
    }

    /**
     * Updates channels in the background and refreshes the menu bar.
     */
    public void updateChannels() {
        // Perform channel updates in the background
        SwingWorker<Void, Void> updateChannelsWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                apiController.loadChannels();
                return null;
            }

            @Override
            protected void done() {
                guiController.updateChannelMenus();
            }
        };
        updateChannelsWorker.execute();
    }
}
