package View;
import Controller.Controller;
import Model.Channel;
import Model.Program;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RadioInfoUI {
    private JFrame frame;
    private JTable table;
    private JComboBox<String> channelComboBox;

    public RadioInfoUI(){ }

    public void createAndShowGUI() {
        frame = new JFrame("RadioInfoUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createMenuBar();
        createMainPanel();

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitMenuItem);

        JMenu channelMenu = new JMenu("Channels");
        List<Channel> channelList = Controller.getChannels();
        for (Channel channel : channelList) {
            JMenuItem channelMenuItem = new JMenuItem(channel.getName());
            channelMenuItem.addActionListener(e -> displayChannelSchedule());
            channelMenu.add(channelMenuItem);
        }

        menuBar.add(fileMenu);
        menuBar.add(channelMenu);

        frame.setJMenuBar(menuBar);
    }

    private void createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        channelComboBox = new JComboBox<>();
        List<Channel> channelList = Controller.getChannels();
        for (Channel channel : channelList) {
            channelComboBox.addItem(channel.getName());
        }

        JButton showScheduleButton = new JButton("Show Schedule");
        showScheduleButton.addActionListener(e -> { channelComboBox.getSelectedItem();
            displayChannelSchedule();
        });

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Select Channel: "));
        controlPanel.add(channelComboBox);
        controlPanel.add(showScheduleButton);

        table = new JTable(new DefaultTableModel(new Object[]{"Program", "Start Time", "End Time"}, 0));

        JScrollPane tableScrollPane = new JScrollPane(table);

        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

    private void displayChannelSchedule() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.minusHours(12);

        List<Program> programList = Controller.getSchedule(132);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Program program : programList) {
            model.addRow(new Object[]{program.getName(), program.getStartTime().format(formatter), program.getEndTime().format(formatter)});
        }
    }
}
