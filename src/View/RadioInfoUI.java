package View;

import Controller.ApiController;
import Controller.GuiController;
import Model.Channel;
import Model.Program;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RadioInfoUI {

    private JFrame frame;
    private JTable table;
    private JLabel programDetailsLabel;
    private List<Program> programList;

    public RadioInfoUI() {
    }

    public void createAndShowGUI() {
        initializeFrame();
        createMenuBar();
        createMainPanel();
        centerFrame();
        frame.setVisible(true);
    }

    private void initializeFrame() {
        frame = new JFrame("RadioInfoUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 720);
        frame.setLocationRelativeTo(null);
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

        createMenu(menuBar, "File", "Exit", e -> System.exit(0));

        createMenu(menuBar, "Help", "Help", e -> GuiController.showHelpDialog(frame));

        ApiController ctrl = new ApiController();
        ctrl.loadChannels();

        createChannelMenu(menuBar, "P1", ctrl.p1);
        createChannelMenu(menuBar, "P2", ctrl.p2);
        createChannelMenu(menuBar, "P3", ctrl.p3);
        createChannelMenu(menuBar, "P4", ctrl.p4);
        createChannelMenu(menuBar, "Other", ctrl.other);

        frame.setJMenuBar(menuBar);
    }

    private void createMenu(JMenuBar menuBar, String menuName, String menuItemName, ActionListener actionListener) {
        JMenu menu = new JMenu(menuName);
        JMenuItem menuItem = new JMenuItem(menuItemName);
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        menuBar.add(menu);
    }

    private void createChannelMenu(JMenuBar menuBar, String menuName, List<Channel> channels) {
        JMenu channelMenu = new JMenu(menuName);
        for (Channel channel : channels) {
            JMenuItem channelMenuItem = new JMenuItem(channel.getName());
            int id = channel.getId();
            channelMenuItem.addActionListener(e -> displayChannelSchedule(id));
            channelMenu.add(channelMenuItem);
        }
        menuBar.add(channelMenu);
    }

    private void createMainPanel() {
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

    private void showProgramInfo(int programId) {
        Program selectedProgram = getProgramById(programId);

        programDetailsLabel.removeAll();

        if (selectedProgram != null) {
            JPanel infoLabelsPanel = new JPanel(new GridLayout(0, 1));

            JPanel imagePanel = createImagePanel(selectedProgram);
            JScrollPane descriptionTextArea = createDescriptionTextArea(selectedProgram);

            addInfoLabel(infoLabelsPanel, "Program ID:", String.valueOf(selectedProgram.getId()));
            addInfoLabel(infoLabelsPanel, "Name:", selectedProgram.getName());
            addInfoLabel(infoLabelsPanel, "Start Time:", selectedProgram.getStartTime().toString());
            addInfoLabel(infoLabelsPanel, "End Time:", selectedProgram.getEndTime().toString());

            JPanel rightPanel = createRightPanel(imagePanel, descriptionTextArea, infoLabelsPanel);
            programDetailsLabel.add(rightPanel);

        }
        programDetailsLabel.revalidate();
        programDetailsLabel.repaint();
    }

    private JPanel createRightPanel(JPanel imagePanel, JScrollPane descriptionScrollPane, JPanel infoLabelsPanel) {
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

    private Program getProgramById(int programId) {
        for (Program program : programList) {
            if (program.getId() == programId) {
                return program;
            }
        }
        return null;
    }

    private void displayChannelSchedule(int channelId) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        ApiController ctrl = new ApiController();

        programList = ctrl.getSchedule(channelId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Program program : programList) {
            Object[] rowData = new Object[]{program.getName(), program.getStartTime().format(formatter), program.getEndTime().format(formatter)};
            model.addRow(rowData);

            int programId = program.getId();
            int rowIndex = model.getRowCount() - 1;
            table.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && table.getSelectedRow() == rowIndex) {
                    showProgramInfo(programId);
                }
            });
        }
    }
}
