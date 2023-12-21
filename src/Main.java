import View.RadioInfoUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            SwingUtilities.invokeLater(() -> {
                RadioInfoUI view = new RadioInfoUI();
                view.createAndShowGUI();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
