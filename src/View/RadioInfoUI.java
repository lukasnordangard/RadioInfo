package View;
import Controller.Controller;
import Model.Channel;
import Model.Program;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RadioInfoUI {
    private JFrame frame;
    private JTable table;
    //private JButton showMoreButton;
    private JButton hideButton;
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
        JPanel mainPanel = new JPanel(new BorderLayout());

        table = new JTable(new DefaultTableModel(new Object[]{"Program", "Start Time", "End Time"}, 0));

        JScrollPane tableScrollPane = new JScrollPane(table);

        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Create and add the "Show More" button
        //showMoreButton = new JButton("Show More");
        //showMoreButton.addActionListener(e -> showMoreClicked());

        // Create a "Hide" button
        hideButton = new JButton("Hide");
        hideButton.addActionListener(e -> hideClicked());

        // Create a label to display program details
        programDetailsLabel = new JLabel();
        programDetailsLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create a panel to hold the buttons
        JPanel buttonPanel = new JPanel();
        //buttonPanel.add(showMoreButton);
        buttonPanel.add(hideButton);

        // Add components to the main panel
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(programDetailsLabel, BorderLayout.EAST);

        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

    private void showMoreClicked(int programId) {
        Program selectedProgram = getProgramById(programId);

        if (selectedProgram != null) {
            // Display program details to the right of the table
            String detailsText = "<html><p><b>Selected Program:</b></p><br>"
                    + "Description: " + selectedProgram.getDescription() + "<br>";

            // Check if the imageUrl is not empty
            if (selectedProgram.getImageUrl() != null && !selectedProgram.getImageUrl().isEmpty()) {
                detailsText += "Image URL: <br><img src='" + selectedProgram.getImageUrl() + "' width='200' height='200'>";
            }

            detailsText += "</html>";

            programDetailsLabel.setText(detailsText);
        } else {
            // Clear the program details label if no program is selected
            programDetailsLabel.setText("");
        }
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

    private void hideClicked() {
        // Clear the program details label when "Hide" button is clicked
        programDetailsLabel.setText("");
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
