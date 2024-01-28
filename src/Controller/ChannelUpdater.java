package Controller;

import Model.Channel;

import javax.swing.*;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * ChannelUpdater updates the list of radio channels in the background. It retrieves
 * channels from the API, manages menu updates, and handles exceptions during the process.
 */
public class ChannelUpdater extends SwingWorker<List<Channel>, Void> {

    // Attributes
    private final ApiController apiController;
    private final MenuController menuController;

    /**
     * Constructor method that initializes ScheduleUpdater.
     */
    public ChannelUpdater(MenuController menuController) {
        this.apiController = new ApiController();
        this.menuController = menuController;
    }

    @Override
    protected List<Channel> doInBackground() throws Exception {
        return apiController.getChannels();
    }

    @Override
    protected void done() {
        try {
            List<Channel> menuChannels = get();

            if (menuChannels != null) {
                menuController.clearChannels();
                menuController.setAllChannels(menuChannels);
                menuController.filterAndAddChannel();
                menuController.updateChannelMenus();
            } else {
                // Handle the case where the background task failed
                String message = "Failed to retrieve channels.";
                JOptionPane.showMessageDialog(null, message, "ERROR", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            if (e.getCause() instanceof UnknownHostException) {
                String message = "API host not reachable. Please check your internet connection.";
                JOptionPane.showMessageDialog(null, message, "ERROR", JOptionPane.INFORMATION_MESSAGE);
            } else if (e.getCause() instanceof SocketException){
                String message = "(Network unreachable or other socket-related issues)";
                JOptionPane.showMessageDialog(null, message, "ERROR", JOptionPane.INFORMATION_MESSAGE);
            } else{
                String message = "An unexpected error occurred.";
                JOptionPane.showMessageDialog(null, message, "ERROR", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
