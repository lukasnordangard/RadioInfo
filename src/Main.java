import Controller.Controller;
import View.RadioInfoUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();

        try {
            SwingUtilities.invokeLater(() -> {
                RadioInfoUI view = new RadioInfoUI(controller);
                view.createAndShowGUI();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
