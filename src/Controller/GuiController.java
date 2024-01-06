package Controller;

import Model.Channel;
import Model.Program;
import View.RadioInfoUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class GuiController {

    private final ApiController apiCtrl = new ApiController();
    private final RadioInfoUI gui;
    private List<Program> programList;
    private final Timer timer;
    private TimerTask currentTimerTask;

    public GuiController(RadioInfoUI gui){
        this.timer = new Timer();
        this.gui = gui;
    }

    public Program getProgramById(int programId) {
        for (Program program : programList) {
            if (program.getId() == programId) {
                return program;
            }
        }
        return null;
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
            channelMenuItem.addActionListener(e -> startTimer(id));
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

    /**
     * Initiates a timer for background updates, ensuring data retrieval from the server
     * occurs without blocking the graphical interface. The timer task periodically
     * updates Swing components using the 'updateTable' method on the EDT.
     */
    public synchronized void startTimer(int channelId) {
        int updateTime = 10;

        // Cancel the current TimerTask if it exists
        if (currentTimerTask != null) {
            currentTimerTask.cancel();
        }

        currentTimerTask = new TimerTask() {
            @Override
            public void run() {
                SwingWorker<Void, Void> updateProgramListWorker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        System.out.println("updateProgramList");
                        updateProgramList(channelId);
                        return null;
                    }

                    @Override
                    protected void done() {
                        displayChannelSchedule();
                        System.out.println("displayChannelSchedule");
                    }
                };
                updateProgramListWorker.execute();
            }
        };

        // Schedule the new TimerTask
        timer.scheduleAtFixedRate(currentTimerTask, 0, TimeUnit.SECONDS.toMillis(updateTime));
    }


    public void updateProgramList(int channelId) {
        programList = apiCtrl.getSchedule(channelId);
    }

    private void displayChannelSchedule() {
        DefaultTableModel model = (DefaultTableModel) gui.getTable().getModel();
        model.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Program program : programList) {
            Object[] rowData = new Object[]{program.getTitle(), program.getStartTime().format(formatter), program.getEndTime().format(formatter)};
            model.addRow(rowData);

            int programId = program.getId();
            int rowIndex = model.getRowCount() - 1;
            gui.getTable().getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && gui.getTable().getSelectedRow() == rowIndex) {
                    Program selectedProgram = getProgramById(programId);
                    gui.showProgramInfo(selectedProgram);
                }
            });
        }
    }


}
