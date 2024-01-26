package Controller;

import Model.Channel;
import Model.Program;

import javax.swing.*;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * ScheduleUpdater updates the schedule for a specific radio channel in the background.
 * It fetches channel schedules from the API, manages GUI updates, and handles exceptions.
 */
public class ScheduleUpdater extends SwingWorker<List<Program>, Void> {

    // Attributes
    private final ApiController apiController;
    private final MenuController menuController;
    private final GuiController guiController;
    private final int channelId;

    /**
     * Constructor method that initializes ScheduleUpdater.
     */
    public ScheduleUpdater(MenuController menuController, GuiController guiController, int channelId) {
        this.apiController = new ApiController();
        this.menuController = menuController;
        this.guiController = guiController;
        this.channelId = channelId;
    }

    @Override
    protected List<Program> doInBackground() throws Exception {
        return apiController.getAllEpisodesInSchedule(channelId);
    }

    @Override
    protected void done() {
        try {
            List<Program> schedule = get();

            if (schedule != null) {
                for (Channel channel : guiController.getCachedChannels()){
                    if(channel.getId() == channelId) {
                        String s = "Update " + channel.getName();
                        System.out.println(s);
                        // without this if the items in menu and table gets stacked
                        if (channel.getSchedule().isEmpty()){
                            channel.setSchedule(schedule);
                            menuController.filterAndAddChannel();
                        }
                        guiController.setCurrentSchedule(channel.getSchedule());
                    }
                }
            } else {
                // Handle the case where the background task failed
                String message = "Failed to retrieve schedule.";
                guiController.showErrorDialog(message);
            }
        } catch (Exception e) {
            if (e.getCause() instanceof UnknownHostException) {
                String message = "API host not reachable. Please check your internet connection.";
                guiController.showErrorDialog(message);
            } else if (e.getCause() instanceof SocketException){
                String message = "(Network unreachable or other socket-related issues)";
                guiController.showErrorDialog(message);
            } else{
                String message = "An unexpected error occurred.";
                guiController.showErrorDialog(message);
            }
        }
        guiController.refreshTable();
    }
}
