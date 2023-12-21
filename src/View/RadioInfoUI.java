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
        menuBar.add(channelMenuP1);
        menuBar.add(channelMenuP2);
        menuBar.add(channelMenuP3);
        menuBar.add(channelMenuP4);
        menuBar.add(channelMenuOther);


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
            displayChannelSchedule(132);
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

    private void displayChannelSchedule(int channelId) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        List<Program> programList = Controller.getSchedule(channelId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Program program : programList) {
            model.addRow(new Object[]{program.getName(), program.getStartTime().format(formatter), program.getEndTime().format(formatter)});
        }
    }

}
