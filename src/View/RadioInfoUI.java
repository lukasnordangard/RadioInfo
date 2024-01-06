package View;

import Model.Program;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

public class RadioInfoUI {

    private JFrame frame;
    private JTable table;
    private JLabel programDetailsLabel;

    public RadioInfoUI() { }

    public JFrame getFrame(){
        return frame;
    }

    public JTable getTable(){
        return table;
    }

    public void initializeFrame() {
        frame = new JFrame("RadioInfoUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 720);
        frame.setLocationRelativeTo(null);
        centerFrame();
    }

    private void centerFrame() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;

        frame.setLocation(x, y);
    }


    public void createMenu(JMenuBar menuBar, String menuName, String menuItemName, ActionListener actionListener) {
        JMenu menu = new JMenu(menuName);
        JMenuItem menuItem = new JMenuItem(menuItemName);
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        menuBar.add(menu);
    }

    public void createMainPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        createTable(mainPanel);
        createProgramDetailsLabel(mainPanel);

        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

    private void createTable(JPanel mainPanel) {
        table = new JTable(new DefaultTableModel(new Object[]{"Program", "Start Time", "End Time"}, 0));
        JScrollPane tableScrollPane = new JScrollPane(table);
        mainPanel.add(tableScrollPane);
    }

    private void createProgramDetailsLabel(JPanel mainPanel) {
        programDetailsLabel = new JLabel();
        programDetailsLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        programDetailsLabel.setLayout(new GridBagLayout());
        mainPanel.add(programDetailsLabel);
    }

    public void showProgramInfo(Program selectedProgram) {

        programDetailsLabel.removeAll();

        if (selectedProgram != null) {
            JPanel infoLabelsPanel = new JPanel(new GridLayout(0, 1));

            JPanel imagePanel = createImagePanel(selectedProgram);
            JScrollPane descriptionTextArea = createDescriptionTextArea(selectedProgram);

            addInfoLabel(infoLabelsPanel, "Program ID:", String.valueOf(selectedProgram.getId()));
            addInfoLabel(infoLabelsPanel, "Title:", selectedProgram.getTitle());
            addInfoLabel(infoLabelsPanel, "Start Time:", selectedProgram.getStartTime().toString());
            addInfoLabel(infoLabelsPanel, "End Time:", selectedProgram.getEndTime().toString());

            JPanel infoPanel = createInfoPanel(imagePanel, descriptionTextArea, infoLabelsPanel);
            programDetailsLabel.add(infoPanel);

        }
        programDetailsLabel.revalidate();
        programDetailsLabel.repaint();
    }

    private JPanel createInfoPanel(JPanel imagePanel, JScrollPane descriptionScrollPane, JPanel infoLabelsPanel) {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(imagePanel, BorderLayout.NORTH);
        rightPanel.add(descriptionScrollPane, BorderLayout.CENTER);
        rightPanel.add(infoLabelsPanel, BorderLayout.SOUTH);
        return rightPanel;
    }

    private JScrollPane createDescriptionTextArea(Program selectedProgram) {
        JTextArea descriptionTextArea = new JTextArea(selectedProgram.getDescription());
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setPreferredSize(new Dimension(250, 100));
        return new JScrollPane(descriptionTextArea);
    }

    private JPanel createImagePanel(Program selectedProgram) {
        JPanel imagePanel = new JPanel();
        if (selectedProgram.getImageUrl() != null && !selectedProgram.getImageUrl().isEmpty()) {
            try {
                ImageIcon originalIcon = new ImageIcon(new URL(selectedProgram.getImageUrl()));
                int targetSize = 250;
                ImageIcon resizedIcon = resizeImage(originalIcon, targetSize, targetSize);
                JLabel imageLabel = new JLabel(resizedIcon);
                imagePanel.add(imageLabel);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return imagePanel;
    }

    private ImageIcon resizeImage(ImageIcon icon, int targetWidth, int targetHeight) {
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    private void addInfoLabel(JPanel panel, String label, String value) {
        JLabel infoLabel = new JLabel(label + " " + value);
        panel.add(infoLabel);
    }

}
