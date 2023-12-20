import Controller.Controller;
import Model.Channel;
import Model.Program;
import View.RadioInfoUI;

import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            SwingUtilities.invokeLater(() -> {
                //RadioInfoUI view = new RadioInfoUI();
                //view.createAndShowGUI();

                Controller ctrl = new Controller();
                List<Program> programs = ctrl.getSchedule(132);

                // Print program information for testing
                for (Program program : programs) {
                    System.out.println("Program Name: " + program.getName());
                    System.out.println("Start Time: " + program.getStartTime());
                    System.out.println("End Time: " + program.getEndTime());
                    System.out.println("---------------");
                }

                /*
                List<Channel> channels = Controller.getChannels();

                // Print channel information for testing
                for (Channel channel : channels) {
                    System.out.println("Channel ID: " + channel.getId());
                    System.out.println("Channel Name: " + channel.getName());
                    System.out.println("Image URL: " + channel.getImageURL());

                    System.out.println("---------------");
                }

                 */

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
