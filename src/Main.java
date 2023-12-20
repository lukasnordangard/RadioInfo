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
                RadioInfoUI view = new RadioInfoUI();
                view.createAndShowGUI();

                /*
                List<Channel> channels = Controller.getChannels();

                // Print channel information for testing
                for (Channel channel : channels) {
                    System.out.println("Channel ID: " + channel.getId());
                    System.out.println("Channel Name: " + channel.getName());
                    System.out.println("Image URL: " + channel.getImageURL());

                    // Print program information for testing
                    List<Program> programs = Controller.getSchedule(channel.getId());
                    for (Program program : programs) {
                        System.out.println("\tProgram Name: " + program.getName());
                        System.out.println("\tStart Time: " + program.getStartTime());
                        System.out.println("\tEnd Time: " + program.getEndTime());
                        System.out.println("---------------");
                    }
                    System.out.println("---------------");
                }

                 */

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
