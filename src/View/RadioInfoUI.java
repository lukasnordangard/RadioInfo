package View;

import Model.Program;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * User Interface class responsible for displaying radio program information.
 */
public class RadioInfoUI {

    // Attributes
    private JFrame frame;
    private JTable table;
    private JLabel programDetailsLabel;
    private JButton updateButton;

    /**
     * Default constructor for RadioInfoUI.
     */
    public RadioInfoUI() { }

    /**
     * Getter method for the main JFrame.
     *
     * @return The main JFrame.
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * Getter method for the JTable.
     *
     * @return The JTable displaying program information.
     */
    public JTable getTable() {
        return table;
    }

    /**
     * Initializes the main JFrame for the RadioInfoUI.
     */
    public void initializeFrame() {
        frame = new JFrame("RadioInfo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 720);
        frame.setLocationRelativeTo(null);
        centerFrame();
    }

    /**
     * Centers the main JFrame on the screen.
     */
    private void centerFrame() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);
    }

    /**
     * Creates a menu item in the specified menu bar.
     *
     * @param menuBar        The menu bar to add the menu item to.
     * @param menuName       The name of the menu.
     * @param menuItemName   The name of the menu item.
     * @param actionListener The ActionListener for the menu item.
     */
    public void createMenu(JMenuBar menuBar, String menuName, String menuItemName, ActionListener actionListener) {
        JMenu menu = new JMenu(menuName);
        JMenuItem menuItem = new JMenuItem(menuItemName);
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        menuBar.add(menu);
    }
    /**
     * Creates the main panel for the GUI.
     */
    public void createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create a panel for the table and program details
        JPanel tableAndDetailsPanel = new JPanel(new GridLayout(1, 2));

        // Create the table and program details label
        JPanel tablePanel = createTablePanel();
        JPanel programDetailsPanel = createProgramDetailsPanel();

        // Add the table and program details label to the tableAndDetailsPanel
        tableAndDetailsPanel.add(tablePanel);
        tableAndDetailsPanel.add(programDetailsPanel);

        // Add the tableAndDetailsPanel to the main panel
        mainPanel.add(tableAndDetailsPanel, BorderLayout.CENTER);

        // Create a panel for the "Update" button with a FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Create and add the "Update" button to the buttonPanel
        updateButton = new JButton("Update program info");
        buttonPanel.add(updateButton);

        // Add the buttonPanel to the main panel
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
    }



    public void setUpdateButtonListener(ActionListener actionListener) {
        if (updateButton != null) {
            updateButton.addActionListener(actionListener);
        }
    }

    /**
     * Creates the panel containing the JTable to display program information.
     *
     * @return The table panel.
     */
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setPreferredSize(new Dimension(frame.getWidth() / 2, frame.getHeight()));
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Program", "Start Time", "End Time"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make all cells non-editable
                return false;
            }
        };
        table = new JTable(model);
        JScrollPane tableScrollPane = new JScrollPane(table);
        tablePanel.add(tableScrollPane);
        return tablePanel;
    }

    /**
     * Creates the panel containing the JLabel to display program details.
     *
     * @return The program details panel.
     */
    private JPanel createProgramDetailsPanel() {
        JPanel programDetailsPanel = new JPanel(new BorderLayout());
        programDetailsPanel.setPreferredSize(new Dimension(frame.getWidth() / 2, frame.getHeight()));
        programDetailsLabel = new JLabel();
        programDetailsLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        programDetailsLabel.setLayout(new GridBagLayout());
        programDetailsPanel.add(programDetailsLabel, BorderLayout.CENTER);
        return programDetailsPanel;
    }

    /**
     * Displays detailed information about the selected program.
     *
     * @param selectedProgram The selected program to display details for.
     */
    public void showProgramInfo(Program selectedProgram) {
        programDetailsLabel.removeAll();

        if (selectedProgram != null) {
            JPanel infoLabelsPanel = new JPanel(new GridLayout(0, 1));
            JPanel imagePanel = createImagePanel(selectedProgram);
            JScrollPane descriptionTextArea = createDescriptionTextArea(selectedProgram);

            addInfoLabel(infoLabelsPanel, "Program ID:", String.valueOf(selectedProgram.getId()));
            addInfoLabel(infoLabelsPanel, "Episode ID:", String.valueOf(selectedProgram.getEpisodeId()));
            addInfoLabel(infoLabelsPanel, "Title:", selectedProgram.getTitle());
            addInfoLabel(infoLabelsPanel, "Start Time:", selectedProgram.getStartTime().toString());
            addInfoLabel(infoLabelsPanel, "End Time:", selectedProgram.getEndTime().toString());

            JPanel infoPanel = createInfoPanel(imagePanel, descriptionTextArea, infoLabelsPanel);
            programDetailsLabel.add(infoPanel);
        }

        programDetailsLabel.revalidate();
        programDetailsLabel.repaint();
    }

    /**
     * Creates a panel to display program information, including an image, description, and additional labels.
     *
     * @param imagePanel       The panel displaying the program image.
     * @param descriptionPanel The panel displaying the program description.
     * @param infoLabelsPanel  The panel displaying additional information labels.
     * @return The combined info panel.
     */
    private JPanel createInfoPanel(JPanel imagePanel, JScrollPane descriptionPanel, JPanel infoLabelsPanel) {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(imagePanel, BorderLayout.NORTH);
        rightPanel.add(descriptionPanel, BorderLayout.CENTER);
        rightPanel.add(infoLabelsPanel, BorderLayout.SOUTH);
        return rightPanel;
    }

    /**
     * Creates a scrollable text area to display the program description.
     *
     * @param selectedProgram The program containing the description.
     * @return The scrollable text area.
     */
    private JScrollPane createDescriptionTextArea(Program selectedProgram) {
        JTextArea descriptionTextArea = new JTextArea(selectedProgram.getDescription());
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setPreferredSize(new Dimension(250, 100));
        return new JScrollPane(descriptionTextArea);
    }

    /**
     * Creates a panel to display the program image.
     *
     * @param selectedProgram The program containing the image URL.
     * @return The panel displaying the program image.
     */
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

    /**
     * Resizes the given ImageIcon to the specified dimensions.
     *
     * @param icon         The original ImageIcon.
     * @param targetWidth  The target width for the resized image.
     * @param targetHeight The target height for the resized image.
     * @return The resized ImageIcon.
     */
    private ImageIcon resizeImage(ImageIcon icon, int targetWidth, int targetHeight) {
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    /**
     * Adds an information label to the specified panel.
     *
     * @param panel The panel to add the label to.
     * @param label The label text.
     * @param value The value text.
     */
    private void addInfoLabel(JPanel panel, String label, String value) {
        JLabel infoLabel = new JLabel(label + " " + value);
        panel.add(infoLabel);
    }
}
