import View.RadioInfoUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        RadioInfoUI ui = new RadioInfoUI();

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            ui.createAndShowGUI();
        });
    }
}
