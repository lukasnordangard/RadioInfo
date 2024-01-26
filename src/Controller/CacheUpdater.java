package Controller;

import Model.Channel;
import Model.Program;

import javax.swing.*;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * CacheUpdater is responsible for updating the cache of radio channel schedules in the
 * background. The background task retrieves schedules for each channel in the cache, updates
 * the GUI components, and handles exceptions that may occur during the update process.
 */
public class CacheUpdater extends SwingWorker<List<Channel>, Void> {

    // Attributes
    private final ApiController apiController;
    private final MenuController menuController;
    private final GuiController guiController;
    private final int lastSelectedChannel;
    private final List<Channel> cache;

    /**
     * Constructor method that initializes CacheUpdater.
     */
    public CacheUpdater(MenuController menuController, GuiController guiController, int lastSelectedChannel) {
        this.apiController = new ApiController();
        this.menuController = menuController;
        this.guiController = guiController;
        this.lastSelectedChannel = lastSelectedChannel;
        this.cache = guiController.getCachedChannels();
    }

    @Override
    protected List<Channel> doInBackground() throws Exception {
        for (Channel channel : cache){
            String s = "Update " + channel.getName();
            if (SwingUtilities.isEventDispatchThread()) {
                System.out.println(s);
            } else {
                System.out.println("\t"+s);
            }
            List<Program> schedule = apiController.getAllEpisodesInSchedule(channel.getId());
            channel.setSchedule(schedule);
        }
        return cache;
    }

    @Override
    protected void done() {
        try {
            List<Channel> cachedChannels = get();

            if (cachedChannels != null) {
                guiController.setCachedChannels(cachedChannels);
                for (Channel cachedChannel : cachedChannels){
                    for (Channel channel : menuController.getAllChannels()){
                        if (cachedChannel.getId() == channel.getId()){
                            channel.setSchedule(cachedChannel.getSchedule());
                        }
                    }
                }
                guiController.onChannelSelected(lastSelectedChannel);
            } else {
                // Handle the case where the background task failed
                String message = "Failed to retrieve schedule.";
                JOptionPane.showMessageDialog(null, message, "ERROR", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            if (e.getCause() instanceof UnknownHostException) {
                String message = "API host not reachable. Please check your internet connection.";
                JOptionPane.showMessageDialog(null, message, "ERROR", JOptionPane.INFORMATION_MESSAGE);
            } else if (e.getCause() instanceof SocketException){
                String message = "Network unreachable or other socket-related issues. Please check your internet connection.";
                JOptionPane.showMessageDialog(null, message, "ERROR", JOptionPane.INFORMATION_MESSAGE);
            } else{
                String message = "An unexpected error occurred.";
                JOptionPane.showMessageDialog(null, message, "ERROR", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
