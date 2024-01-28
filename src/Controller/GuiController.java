package Controller;

import Model.Channel;
import Model.Program;
import View.RadioInfoUI;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Controller class responsible for managing the graphical user
 * interface (GUI) interactions and updates.
 */
public class GuiController {

    // Attributes
    private final MenuController menuController;
    private final ApiController apiController;
    private final RadioInfoUI view;
    private List<Channel> cachedChannels;
    private List<Program> currentSchedule;
    private final Timer timer;

    /**
     * Constructor method that initializes GuiController.
     *
     * @param view The RadioInfoUI the user interacts with.
     */
    public GuiController(RadioInfoUI view) {
        this.view = view;
        this.menuController = new MenuController(this, this.view);
        this.apiController = new ApiController();
        this.currentSchedule = new ArrayList<>();
        this.cachedChannels = new ArrayList<>();
        timer = new Timer();
    }

    /**
     * Sets a list of cashed channels.
     *
     * @param channels cached channels.
     */
    public void setCachedChannels(List<Channel> channels){
        cachedChannels = channels;
    }

    /**
     * Gets the list of cached channels.
     *
     * @return list of cached channels.
     */
    public List<Channel> getCachedChannels(){
        return cachedChannels;
    }

    /**
     * Sets the current schedule to be displayed.
     *
     * @param currentSchedule schedule to be displayed.
     */
    public void setCurrentSchedule(List<Program> currentSchedule) {
        this.currentSchedule = currentSchedule;
    }

    /**
     * Creates and displays the main GUI.
     */
    public void createAndShowGUI() {
        view.initializeFrame();
        menuController.createMenuBar();
        startTimer();
        view.createMainPanel();

        // Set the action listener for the "Update" button
        view.setUpdateButtonListener(e -> handleUpdateButtonClick());

        view.getFrame().setVisible(true);
    }

    public void handleUpdateButtonClick() {
        int selectedChannel = menuController.getLastSelectedChannel();
        ChannelUpdater channelUpdater = new ChannelUpdater(menuController);
        channelUpdater.execute();
        if (selectedChannel != -1){
            List<Channel> cacheCopy = new ArrayList<>(cachedChannels);
            CacheUpdater cacheUpdater = new CacheUpdater(menuController, this, apiController, cacheCopy, selectedChannel);
            cacheUpdater.execute();
        }
    }

    private void startTimer() {
        GuiController guiCtrl = this;

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    System.out.println("timer");
                    int selectedChannel = menuController.getLastSelectedChannel();
                    ChannelUpdater channelUpdater = new ChannelUpdater(menuController);
                    channelUpdater.execute();
                    if (selectedChannel != -1){
                        List<Channel> cacheCopy = new ArrayList<>(cachedChannels);
                        CacheUpdater cacheUpdater = new CacheUpdater(menuController, guiCtrl, apiController, cacheCopy, selectedChannel);
                        cacheUpdater.execute();
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask,0, TimeUnit.SECONDS.toMillis(20));
    }

    /**
     * Displays a help dialog for the user.
     */
    public void showHelpDialog() {
        String helpMessage = """
            Welcome to RadioInfoUI!

            This application is used to display schedules and programs for various radio channels provided by SR!
            To get started, select a channel from the menu to display its schedule.
            You can also click on a program in the table to view more information.
            """;

        JOptionPane.showMessageDialog(view.getFrame(), helpMessage, "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays a error dialog for the user.
     *
     * @param message Errormessage to be displayed.
     */
    public void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "ERROR", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Handles the selection of a channel.
     *
     * @param channelId The ID of the selected channel.
     */
    public void onChannelSelected(int channelId) {
        boolean channelExistsInCache = false;

        for (Channel channel : menuController.getAllChannels()) {
            if (channel.getId() == channelId) {
                if (channel.getSchedule().isEmpty()) {
                    ScheduleUpdater scheduleUpdater = new ScheduleUpdater(this, channelId);
                    scheduleUpdater.execute();

                    // Check if the channel exists in the cachedChannels list
                    for (Channel cachedChannel : cachedChannels) {
                        if (cachedChannel.getId() == channelId) {
                            channelExistsInCache = true;
                            break;
                        }
                    }

                    // Add the channel to the cachedChannels list if it doesn't already exist
                    if (!channelExistsInCache) {
                        cachedChannels.add(channel);
                    }
                } else {
                    System.out.println("Channel is cached");
                    currentSchedule = channel.getSchedule();
                    refreshTable();
                }
            }
        }
    }

    public void updateSchedule(int channelId, List<Program> schedule){
        for (Channel channel : getCachedChannels()){
            if(channel.getId() == channelId) {
                System.out.println("Update " + channel.getName());
                channel.setSchedule(schedule);
                setCurrentSchedule(channel.getSchedule());
            }
        }
    }

    /**
     * Requests a refresh of the GUI table.
     */
    public void refreshTable() {
        SwingUtilities.invokeLater(() -> {
            displayChannelSchedule();

            if (!cachedChannels.isEmpty()){
                for (Channel channel : cachedChannels){
                    if(!channel.getSchedule().isEmpty()) {
                        System.out.println(channel.getName() + ": " + channel.getSchedule());
                    }else{
                        System.out.println(channel.getName() + ": " + "Schedule is empty");
                    }
                }
                System.out.println("===============================");
            }
        });

        //SwingUtilities.invokeLater(this::displayChannelSchedule);
    }

    private final ListSelectionListener listSelectionListener = this::handleListSelectionEvent;

    /**
     * Handles the event of a list selection change.
     *
     * @param e The ListSelectionEvent.
     */
    private void handleListSelectionEvent(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && view.getTable().isShowing()) {
            int selectedRow = view.getTable().getSelectedRow();
            if (selectedRow != -1) {
                Program selectedProgram = getProgramBySelectedRow(selectedRow);
                if (selectedProgram != null) {
                    view.showProgramInfo(selectedProgram);
                }
            }
        }
    }

    /**
     * Retrieves a program based on the selected row in the table.
     *
     * @param selectedRow The selected row index.
     * @return The Program object or null if not found.
     */
    private Program getProgramBySelectedRow(int selectedRow) {
        return (selectedRow >= 0 && selectedRow < currentSchedule.size()) ?
                getProgramById(currentSchedule.get(selectedRow).getEpisodeId(), currentSchedule.get(selectedRow).getId()) : null;
    }

    /**
     * Retrieves a program based on its ID.
     *
     * @param episodeId The ID of the episode to retrieve.
     * @return The Program object or null if not found.
     */
    private Program getProgramById(int episodeId, int programId) {
        if(episodeId != -1){
            for (Program program : currentSchedule) {
                if (program.getEpisodeId() == episodeId) {
                    return program;
                }
            }
        } else if (programId != -1) {
            for (Program program : currentSchedule) {
                if (program.getId() == programId) {
                    return program;
                }
            }
        } else {
            return null;
        }
        return null;
    }

    /**
     * Clears list selection listeners from the table model.
     *
     * @param model The ListSelectionModel to clear listeners from.
     */
    private void clearSelectionListeners(ListSelectionModel model) {
        model.removeListSelectionListener(listSelectionListener);
    }

    /**
     * Clears list selection listeners from the table.
     */
    private void clearTableSelectionListeners() {
        clearSelectionListeners(view.getTable().getSelectionModel());
    }

    /**
     * Updates the GUI table with the list of programs.
     */
    private void updateTableWithPrograms() {
        DefaultTableModel model = (DefaultTableModel) view.getTable().getModel();
        model.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Program program : currentSchedule) {
            Object[] rowData = new Object[]{program.getTitle(), program.getStartTime().format(formatter), program.getEndTime().format(formatter)};
            model.addRow(rowData);
        }
    }

    /**
     * Adds a list selection listener to the table.
     */
    private void addTableSelectionListener() {
        view.getTable().getSelectionModel().addListSelectionListener(listSelectionListener);
    }

    /**
     * Displays the schedule of the selected channel.
     */
    private void displayChannelSchedule() {
        clearTableSelectionListeners();
        updateTableWithPrograms();
        addTableSelectionListener();
    }
}
