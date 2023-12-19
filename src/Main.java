import Controller.Controller;
import Model.Channel;
import View.RadioInfoUI;

import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();

        try {
            SwingUtilities.invokeLater(() -> {
                RadioInfoUI view = new RadioInfoUI(controller);
                view.createAndShowGUI();

                //controller.getChannelFromAPI(4540);

                List<Channel> channels = Controller.getChannels();

                // Print channel information for testing
                for (Channel channel : channels) {
                    System.out.println("Channel ID: " + channel.getId());
                    System.out.println("Channel Name: " + channel.getName());
                    System.out.println("Image URL: " + channel.getImageURL());

                    System.out.println("---------------");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
