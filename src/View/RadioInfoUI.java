package View;
import Controller.Controller;
import Model.Channel;
import Model.Program;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RadioInfoUI {
    private JFrame frame;
    private JTable table;
    private JLabel programDetailsLabel;

    private List<Program> programList;


    public RadioInfoUI(){ }

    public void createAndShowGUI() {
        frame = new JFrame("RadioInfoUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createMenuBar();
        createMainPanel();

        centerFrame();

        frame.pack();
        frame.setSize(1200, 720);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void centerFrame() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        int frameWidth = frame.getWidth();
        int frameHeight = frame.getHeight();

        int x = (screenWidth - frameWidth) / 2;
        int y = (screenHeight - frameHeight) / 2;

        frame.setLocation(x, y);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitMenuItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpMenuItem = new JMenuItem("Help");
        helpMenuItem.addActionListener(e -> showHelpDialog());
        helpMenu.add(helpMenuItem);

        Controller ctrl = new Controller();
        ctrl.loadChannels();

        JMenu channelMenuP1 = new JMenu("P1");
        for (Channel channel : ctrl.p1) {
            JMenuItem channelMenuItem = new JMenuItem(channel.getName());
            channelMenuItem.addActionListener(e -> {
                int id = channel.getId();
                displayChannelSchedule(id);
            });
            channelMenuP1.add(channelMenuItem);
        }

        JMenu channelMenuP2 = new JMenu("P2");
        for (Channel channel : ctrl.p2) {
            JMenuItem channelMenuItem = new JMenuItem(channel.getName());
            channelMenuItem.addActionListener(e -> {
                int id = channel.getId();
                displayChannelSchedule(id);
            });
            channelMenuP2.add(channelMenuItem);
        }

        JMenu channelMenuP3 = new JMenu("P3");
        for (Channel channel : ctrl.p3) {
            JMenuItem channelMenuItem = new JMenuItem(channel.getName());
            channelMenuItem.addActionListener(e -> {
                int id = channel.getId();
                displayChannelSchedule(id);
            });
            channelMenuP3.add(channelMenuItem);
        }

        JMenu channelMenuP4 = new JMenu("P4");
        for (Channel channel : ctrl.p4) {
            JMenuItem channelMenuItem = new JMenuItem(channel.getName());
            channelMenuItem.addActionListener(e -> {
                int id = channel.getId();
                displayChannelSchedule(id);
            });
            channelMenuP4.add(channelMenuItem);
        }

        JMenu channelMenuOther = new JMenu("Other");
        for (Channel channel : ctrl.other) {
            JMenuItem channelMenuItem = new JMenuItem(channel.getName());
            channelMenuItem.addActionListener(e -> {
                int id = channel.getId();
                displayChannelSchedule(id);
            });
            channelMenuOther.add(channelMenuItem);
        }

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        menuBar.add(channelMenuP1);
        menuBar.add(channelMenuP2);
        menuBar.add(channelMenuP3);
        menuBar.add(channelMenuP4);
        menuBar.add(channelMenuOther);

        frame.setJMenuBar(menuBar);
    }

    private void showHelpDialog() {
        String helpMessage = "Welcome to RadioInfoUI!\n\n"
                + "This application allows you to view channel schedules.\n"
                + "To get started, select a channel from the menu to display its schedule.\n"
                + "You can also click on a program in the table to view more information.\n\n"
                + "For further assistance, contact support.";

        JOptionPane.showMessageDialog(frame, helpMessage, "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    private void createMainPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2)); // One row, two columns

        // Left side: Table
        table = new JTable(new DefaultTableModel(new Object[]{"Program", "Start Time", "End Time"}, 0));
        JScrollPane tableScrollPane = new JScrollPane(table);
        mainPanel.add(tableScrollPane);

        // Right side: Program details
        programDetailsLabel = new JLabel();
        programDetailsLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        programDetailsLabel.setLayout(new GridBagLayout());
        mainPanel.add(programDetailsLabel);

        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
    }


    private void showMoreClicked(int programId) {
        Program selectedProgram = getProgramById(programId);

        // Clear the program details label
        programDetailsLabel.removeAll();

        if (selectedProgram != null) {
            // Create a panel to hold the information labels
            JPanel infoLabelsPanel = new JPanel(new GridLayout(0, 1));

            // Create a panel to hold the image
            JPanel imagePanel = new JPanel();
            if (selectedProgram.getImageUrl() != null && !selectedProgram.getImageUrl().isEmpty()) {
                try {
                    // Load image from URL
                    ImageIcon originalIcon = new ImageIcon(new URL(selectedProgram.getImageUrl()));

                    // Resize the image while preserving its aspect ratio
                    int targetSize = 250;
                    ImageIcon resizedIcon = resizeImage(originalIcon, targetSize, targetSize);

                    JLabel imageLabel = new JLabel(resizedIcon);
                    imagePanel.add(imageLabel);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

            // Create a panel to hold the description text area
            JTextArea descriptionTextArea = new JTextArea(selectedProgram.getDescription());
            descriptionTextArea.setLineWrap(true);
            descriptionTextArea.setWrapStyleWord(true);
            descriptionTextArea.setEditable(false);

            // Set preferred size for the description text area
            descriptionTextArea.setPreferredSize(new Dimension(250, 100)); // Adjust the size as needed

            JScrollPane descriptionScrollPane = new JScrollPane(descriptionTextArea);

            // Add information labels
            addInfoLabel(infoLabelsPanel, "Program ID:", String.valueOf(selectedProgram.getProgramId()));
            addInfoLabel(infoLabelsPanel, "Name:", selectedProgram.getName());
            addInfoLabel(infoLabelsPanel, "Start Time:", selectedProgram.getStartTime().toString());
            addInfoLabel(infoLabelsPanel, "End Time:", selectedProgram.getEndTime().toString());

            // Create a panel to hold everything on the right side
            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.add(imagePanel, BorderLayout.NORTH);
            rightPanel.add(descriptionScrollPane, BorderLayout.CENTER);
            rightPanel.add(infoLabelsPanel, BorderLayout.SOUTH);

            // Add the right panel to programDetailsLabel
            programDetailsLabel.add(rightPanel);

            // Refresh the layout
            programDetailsLabel.revalidate();
            programDetailsLabel.repaint();
        } else {
            // Refresh the layout even if no program is selected
            programDetailsLabel.revalidate();
            programDetailsLabel.repaint();
        }
    }

    // Helper method to resize an ImageIcon while preserving its aspect ratio
    private ImageIcon resizeImage(ImageIcon icon, int targetWidth, int targetHeight) {
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    // Helper method to add an information label to the panel
    private void addInfoLabel(JPanel panel, String label, String value) {
        JLabel infoLabel = new JLabel(label + " " + value);
        panel.add(infoLabel);
    }

    // Helper method to retrieve a program by its ID
    private Program getProgramById(int programId) {
        // Implement logic to retrieve the program by ID from your data source
        // For example, you can iterate over the list of programs and find the one with the matching ID
        // Return null if no program is found with the specified ID
        for (Program program : programList) {
            if (program.getProgramId() == programId) {
                return program;
            }
        }
        return null;
    }

    private void displayChannelSchedule(int channelId) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        programList = Controller.getSchedule(channelId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Program program : programList) {
            Object[] rowData = new Object[]{program.getName(), program.getStartTime().format(formatter), program.getEndTime().format(formatter)};
            model.addRow(rowData);

            // Add an action listener to each row
            int programId = program.getProgramId();
            int rowIndex = model.getRowCount() - 1;
            table.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && table.getSelectedRow() == rowIndex) {
                    showMoreClicked(programId);
                }
            });
        }
    }

}
