package Controller;

import Model.Channel;

import javax.swing.*;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

public class ChannelUpdater extends SwingWorker<List<Channel>, Void> {

    private final MenuController menuController;
    private final ApiController apiController;

    public ChannelUpdater(MenuController menuController) {
        this.menuController = menuController;
        this.apiController = new ApiController();
    }

    @Override
    protected List<Channel> doInBackground() throws Exception {
        return apiController.getChannels();
    }

    @Override
    protected void done() {
        try {
            List<Channel> channels = get();

            if (channels != null) {
                menuController.clearChannels();
                menuController.setAllChannels(channels);
                menuController.filterAndAddChannel();
                menuController.updateChannelMenus();
            } else {
                // Handle the case where the background task failed
                System.err.println("Failed to retrieve channels.");
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
