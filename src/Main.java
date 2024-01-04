import View.RadioInfoUI;

import javax.swing.*;

/**
 * Main class that initialize and run the program RadioInfo.
 *
 * @author Lukas Nordangård (id20lsd) (luno0020)
 * @version 1.0
 */
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
