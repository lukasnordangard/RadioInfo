import Controller.Controller;
import View.RadioInfoUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                RadioInfoUI view = new RadioInfoUI(controller);
                view.createAndShowGUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
