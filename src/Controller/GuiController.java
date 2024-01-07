package Controller;

import Model.Channel;
import Model.Program;
import View.RadioInfoUI;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GuiController {

    private final ApiController apiCtrl = new ApiController();
    private final RadioInfoUI gui;
    private List<Program> programList;
    private final BackgroundUpdater backgroundUpdater;

    public GuiController(RadioInfoUI gui) {
        this.gui = gui;
        this.backgroundUpdater = new BackgroundUpdater(this);
    }

    public Program getProgramById(int programId) {
        for (Program program : programList) {
            if (program.getId() == programId) {
                return program;
            }
        }
        return null;
    }

    public void refreshView() {
        SwingUtilities.invokeLater(() -> {
            if (SwingUtilities.isEventDispatchThread()) {
                System.out.println("Code executed on EDT");
            } else {
                System.out.println("Code executed on a background thread");
            }

            displayChannelSchedule();
            System.out.println("displayChannelSchedule");
        });
    }

    public void createAndShowGUI() {
        gui.initializeFrame();
        createMenuBar();
        gui.createMainPanel();
        gui.getFrame().setVisible(true);
    }

    public void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        gui.createMenu(menuBar, "File", "Exit", e -> System.exit(0));
        gui.createMenu(menuBar, "Help", "Help", e -> showHelpDialog(gui.getFrame()));

        SwingWorker<Void, Void> loadChannelsWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                System.out.println("loadChannels");
                apiCtrl.loadChannels();
                return null;
            }

            @Override
            protected void done() {
                System.out.println("createChannelMenu");

                createChannelMenu(menuBar, "P1", apiCtrl.getP1());
                createChannelMenu(menuBar, "P2", apiCtrl.getP2());
                createChannelMenu(menuBar, "P3", apiCtrl.getP3());
                createChannelMenu(menuBar, "P4", apiCtrl.getP4());
                createChannelMenu(menuBar, "Other", apiCtrl.getOther());

                gui.getFrame().setJMenuBar(menuBar);

                System.out.println("=======================");
            }
        };
        loadChannelsWorker.execute();
    }

    public void createChannelMenu(JMenuBar menuBar, String menuName, List<Channel> channels) {
        JMenu channelMenu = new JMenu(menuName);
        for (Channel channel : channels) {
            JMenuItem channelMenuItem = new JMenuItem(channel.getName());
            int id = channel.getId();
            channelMenuItem.addActionListener(e -> onChannelSelected(id));
            channelMenu.add(channelMenuItem);
        }
        menuBar.add(channelMenu);
    }

    public void showHelpDialog(JFrame frame) {
        String helpMessage = """
            Welcome to RadioInfoUI!

            This application is used to display schedules and programs for various radio channels provided by SR!
            To get started, select a channel from the menu to display its schedule.
            You can also click on a program in the table to view more information.
            """;

        JOptionPane.showMessageDialog(frame, helpMessage, "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    public void onChannelSelected(int channelId) {
        backgroundUpdater.startUpdates(channelId);
    }

    public void updateProgramList(int channelId) {
        programList = apiCtrl.getSchedule(channelId);
    }

    private final ListSelectionListener listSelectionListener = this::handleListSelectionEvent;

    private void handleListSelectionEvent(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && gui.getTable().isShowing()) {
            int selectedRow = gui.getTable().getSelectedRow();
            if (selectedRow != -1) {
                Program selectedProgram = getProgramBySelectedRow(selectedRow);
                if (selectedProgram != null) {
                    System.out.println("SET UP PROGRAM: " + selectedProgram.getId() + " " + selectedProgram.getTitle());
                    gui.showProgramInfo(selectedProgram);
                } else {
                    System.out.println("WTF?");
                }
            }
        }
    }

    private Program getProgramBySelectedRow(int selectedRow) {
        return (selectedRow >= 0 && selectedRow < programList.size()) ?
                getProgramById(programList.get(selectedRow).getId()) : null;
    }

    private void clearSelectionListeners(ListSelectionModel model) {
        model.removeListSelectionListener(listSelectionListener);
    }

    private void clearTableSelectionListeners() {
        clearSelectionListeners(gui.getTable().getSelectionModel());
    }

    private void updateTableWithPrograms() {
        DefaultTableModel model = (DefaultTableModel) gui.getTable().getModel();
        model.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Program program : programList) {
            Object[] rowData = new Object[]{program.getTitle(), program.getStartTime().format(formatter), program.getEndTime().format(formatter)};
            model.addRow(rowData);
        }
    }

    private void addTableSelectionListener() {
        gui.getTable().getSelectionModel().addListSelectionListener(listSelectionListener);
    }

    private void displayChannelSchedule() {
        clearTableSelectionListeners();
        updateTableWithPrograms();
        addTableSelectionListener();
    }
}
