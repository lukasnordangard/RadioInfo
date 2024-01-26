package Controller;

import Model.Channel;
import Model.Program;
import View.RadioInfoUI;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class responsible for managing the graphical user
 * interface (GUI) interactions and updates.
 */
public class GuiController {

    // Attributes
    private final ApiController apiController;
    private final MenuController menuController;
    private final RadioInfoUI view;
    private final BackgroundUpdater backgroundUpdater;
    private final List<Channel> cachedChannels;
    private List<Program> currentSchedule;

    /**
     * Constructor method that initializes GuiController.
     *
     * @param view The RadioInfoUI the user interacts with.
     */
    public GuiController(RadioInfoUI view) {
        this.view = view;
        this.apiController = new ApiController();
        this.menuController = new MenuController(this, this.view);
        this.backgroundUpdater = new BackgroundUpdater(this, apiController);
        this.currentSchedule = new ArrayList<>();
        this.cachedChannels = new ArrayList<>();
    }

    /**
     * Creates and displays the main GUI.
     */
    public void createAndShowGUI() {
        view.initializeFrame();
        menuController.createMenuBar();
        view.createMainPanel();
        view.getFrame().setVisible(true);
    }

    public List<Channel> getCachedChannels(){
        return cachedChannels;
    }

    public void setCurrentSchedule(List<Program> currentSchedule) {
        this.currentSchedule = currentSchedule;
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

        JOptionPane.showMessageDialog(null, helpMessage, "Help", JOptionPane.INFORMATION_MESSAGE);
    }

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

        Channel channel = menuController.getChannelById(channelId);

        if (channel != null && channel.getSchedule().isEmpty()) {
            ScheduleUpdater scheduleUpdater = new ScheduleUpdater(menuController,this,channelId);
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
            // OBS: does not update with timer
            System.out.println("Channel is cached");
            currentSchedule = channel.getSchedule();
            refreshTable();
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
                        System.out.println("lost cachedChannel");
                    }
                }
                System.out.println("===============================");
            }
        });

        //SwingUtilities.invokeLater(this::displayChannelSchedule);
    }

    /**
     * Updates the list of programs for a specific channel.
     *
     * @param channelId The ID of the channel to update programs for.
     */
    public void updateChannelSchedule(int channelId) {
        //TODO: make thread safe
        try {
            List<Program> schedule = apiController.getAllEpisodesInSchedule(channelId);

            SwingUtilities.invokeLater(() -> {
                for (Channel channel : cachedChannels){
                    if(channel.getId() == channelId) {
                        String s = "Update " + channel.getName();
                        backgroundUpdater.printMethod(s);
                        // without this if the items in menu and table gets stacked
                        if (channel.getSchedule().isEmpty()){
                            channel.setSchedule(schedule);
                            menuController.filterAndAddChannel();
                        }
                        currentSchedule = channel.getSchedule();
                    }
                }
            });
        } catch (SocketException e) {
            showErrorDialog("(Network unreachable or other socket-related issues)");
        } catch (UnknownHostException e){
            showErrorDialog("(API host not reachable)");
        } catch (Exception e) {
            // Handle other exceptions
            showErrorDialog("An unknown error occurred");
        }
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
    public Program getProgramById(int episodeId, int programId) {
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
