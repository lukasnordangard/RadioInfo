package Controller;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class BackgroundUpdater {
    private final Timer timer;
    private TimerTask currentTimerTask;
    private final GuiController guiController;

    public BackgroundUpdater(GuiController guiController) {
        this.timer = new Timer();
        this.guiController = guiController;
    }

    public synchronized void startUpdates(int channelId) {
        int updateTime = 60;

        // Cancel the current TimerTask if it exists
        if (currentTimerTask != null) {
            currentTimerTask.cancel();
        }

        currentTimerTask = new TimerTask() {
            @Override
            public void run() {
                guiController.updateProgramList(channelId);
                guiController.refreshView();
            }
        };

        // Schedule the new TimerTask
        timer.scheduleAtFixedRate(currentTimerTask, 0, TimeUnit.MINUTES.toMillis(updateTime));
    }

    public void stopUpdates() {
        if (currentTimerTask != null) {
            currentTimerTask.cancel();
        }
    }
}
