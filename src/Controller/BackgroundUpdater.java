package Controller;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class BackgroundUpdater {
    private final Timer timer;
    private TimerTask currentTimerTask;
    private final GuiController guiController;
    private final ApiController apiController;

    public BackgroundUpdater(GuiController guiController, ApiController apiController) {
        this.timer = new Timer();
        this.guiController = guiController;
        this.apiController = apiController;
    }

    public synchronized void updateProgramsWithTimer(int channelId) {
        int updateTime = 60;

        // Cancel the current TimerTask if it exists
        if (currentTimerTask != null) {
            currentTimerTask.cancel();
        }

        currentTimerTask = new TimerTask() {
            @Override
            public void run() {
                guiController.updateProgramList(channelId);
                guiController.refreshTable();
            }
        };

        // Schedule the new TimerTask
        timer.scheduleAtFixedRate(currentTimerTask, 0, TimeUnit.MINUTES.toMillis(updateTime));
    }

    public void updateChannels(JMenuBar menuBar) {
        // Perform channel updates in the background
        SwingWorker<Void, Void> updateChannelsWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                apiController.loadChannels();
                return null;
            }

            @Override
            protected void done() {
                guiController.createChannelMenus(menuBar);
            }
        };
        updateChannelsWorker.execute();
    }
}
