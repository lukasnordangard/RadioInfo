package Controller;

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
    private final Timer timer;
    private final RadioInfoUI gui;
    private List<Program> programList;

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
        gui.createMenuBar();
        gui.createMainPanel();
        gui.centerFrame();
        gui.getFrame().setVisible(true);
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
        TimerTask timerTask = new TimerTask() {
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
        timer.scheduleAtFixedRate(timerTask,0, TimeUnit.SECONDS.toMillis(updateTime));
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
                    gui.showProgramInfo(programId);
                }
            });
        }
    }


}
