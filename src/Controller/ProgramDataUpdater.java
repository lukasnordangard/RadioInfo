package Controller;

import Model.Channel;

import javax.swing.*;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * CacheUpdater is responsible for updating the cache of radio channel schedules in the
 * background. The background task retrieves schedules for each channel in the cache, updates
 * the GUI components, and handles exceptions that may occur during the update process.
 */
public class ProgramDataUpdater extends SwingWorker<List<Channel>, Void> {

    // Attributes
    private final ApiController apiController;
    private final MenuController menuController;
    private final GuiController guiController;
    private final int lastSelectedChannel;
    private final List<Channel> cacheCopy;

    /**
     * Constructor method that initializes CacheUpdater.
     */
    public ProgramDataUpdater(MenuController menuController, GuiController guiController, ApiController apiController, List<Channel> cacheCopy, int lastSelectedChannel) {
        this.apiController = apiController;
        this.menuController = menuController;
        this.guiController = guiController;
        this.lastSelectedChannel = lastSelectedChannel;
        this.cacheCopy = cacheCopy;
    }

    @Override
    protected List<Channel> doInBackground() throws Exception {
        List<Channel> updatedMenuChannels = apiController.getChannels();
        List<Channel> updatedCacheCopy = apiController.updateAllCachedSchedules(cacheCopy);

        for (Channel cachedChannel : updatedCacheCopy){
            for (Channel channel : updatedMenuChannels){
                if (cachedChannel.getId() == channel.getId()){
                    channel.setSchedule(cachedChannel.getSchedule());
                }
            }
        }

        return updatedMenuChannels;
    }

    @Override
    protected void done() {
        try {
            List<Channel> newUpdatedCache = new ArrayList<>();
            List<Channel> updatedMenuChannels = get();

            if (updatedMenuChannels != null) {
                // Update channel schedules in menu
                menuController.setAllChannels(updatedMenuChannels);

                for (Channel channel : updatedMenuChannels){
                    if (!channel.getSchedule().isEmpty()){
                        newUpdatedCache.add(channel);
                    }
                }

                // Update cache list
                guiController.setCachedChannels(newUpdatedCache);
                guiController.onChannelSelected(lastSelectedChannel);// Have to be here so the channel is selected when the thread is done and not before
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
                String message = "Network unreachable or other socket-related issues. Please check your internet connection.";
                guiController.showErrorDialog(message);
            } else{
                String message = "An unexpected error occurred.";
                guiController.showErrorDialog(message);
            }
        }
    }
}
