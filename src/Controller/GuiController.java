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
    private final ApiController apiCtrl;
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
        this.apiCtrl = new ApiController();
        this.backgroundUpdater = new BackgroundUpdater(this, apiCtrl);
        this.currentSchedule = new ArrayList<>();
        this.cachedChannels = new ArrayList<>();
    }

    /**
     * Creates and displays the main GUI.
     */
    public void createAndShowGUI() {
        view.initializeFrame();
        createMenuBar();
        view.createMainPanel();
        view.getFrame().setVisible(true);
    }

    /**
     * Creates the menu bar for the main GUI.
     */
    public void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        view.createMenu(menuBar, "Alternatives", "Update Channels", e -> {
            if(cachedChannels.isEmpty()){
                backgroundUpdater.updateChannels();
            }else {
                //backgroundUpdater.updateChannels(); // This forgets cached channels before button press
                backgroundUpdater.updateCachedSchedules(cachedChannels);
            }
        });
        JMenuItem helpMenuItem = new JMenuItem("Help");
        helpMenuItem.addActionListener(e -> showHelpDialog());
        menuBar.getMenu(0).addSeparator();
        menuBar.getMenu(0).add(helpMenuItem);

        backgroundUpdater.updateChannels();

        view.getFrame().setJMenuBar(menuBar);
    }

    /**
     * Updates the channel menus in the main GUI's menu bar.
     * Removes existing channel menus and recreates them.
     */
    public void updateChannelMenus() {
        JMenuBar menuBar = view.getFrame().getJMenuBar();

        // Remove existing channel menus
        for (int i = menuBar.getMenuCount() - 1; i >= 0; i--) {
            JMenu menu = menuBar.getMenu(i);
            if (menu.getText().equals("P1") || menu.getText().equals("P2") || menu.getText().equals("P3") ||
                    menu.getText().equals("P4") || menu.getText().equals("Other")) {
                menuBar.remove(i);
            }
        }

        // Recreate channel menus
        createChannelMenus(menuBar);

        // Revalidate and repaint the main GUI frame
        view.getFrame().revalidate();
        view.getFrame().repaint();
    }

    /**
     * Creates menu items for each channel category in the main menu bar.
     *
     * @param menuBar The main menu bar.
     */
    public void createChannelMenus(JMenuBar menuBar){
        createChannelMenu(menuBar, "P1", apiCtrl.getP1());
        createChannelMenu(menuBar, "P2", apiCtrl.getP2());
        createChannelMenu(menuBar, "P3", apiCtrl.getP3());
        createChannelMenu(menuBar, "P4", apiCtrl.getP4());
        createChannelMenu(menuBar, "Other", apiCtrl.getOther());
    }


    /**
     * Creates a channel menu with items for each channel.
     *
     * @param menuBar   The main menu bar.
     * @param menuName  The name of the channel menu.
     * @param channels  The list of channels to be displayed in the menu.
     */
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

        for (Channel channel : apiCtrl.getAllChannels()) {
            if (channel.getId() == channelId) {
                if (channel.getSchedule().isEmpty()) {
                    backgroundUpdater.updateChannelScheduleWithTimer(channelId);

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
            List<Program> schedule = apiCtrl.getAllEpisodesInSchedule(channelId);

            SwingUtilities.invokeLater(() -> {
                for (Channel channel : cachedChannels){
                    if(channel.getId() == channelId) {
                        String s = "Update " + channel.getName();
                        backgroundUpdater.printMethod(s);
                        // without this if the items in menu and table gets stacked
                        if (channel.getSchedule().isEmpty()){
                            channel.setSchedule(schedule);
                            apiCtrl.filterAndAddChannel();
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
