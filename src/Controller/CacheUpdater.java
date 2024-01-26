package Controller;

import Model.Channel;
import Model.Program;

import javax.swing.*;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

public class CacheUpdater extends SwingWorker<List<Channel>, Void> {

    private final GuiController guiController;
    private final ApiController apiController;
    private final MenuController menuController;
    private final int lastSelectedChannel;
    private final List<Channel> cache;

    public CacheUpdater(GuiController guiController, MenuController menuController, List<Channel> cache, int lastSelectedChannel) {
        this.guiController = guiController;
        this.apiController = new ApiController();
        this.menuController = menuController;
        this.lastSelectedChannel = lastSelectedChannel;
        this.cache = cache;
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
