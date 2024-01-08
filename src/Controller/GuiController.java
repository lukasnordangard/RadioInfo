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

/**
 * Controller class responsible for managing the graphical user
 * interface (GUI) interactions and updates.
 */
public class GuiController {

    // Attributes
    private final ApiController apiCtrl;
    private final RadioInfoUI gui;
    private final BackgroundUpdater backgroundUpdater;
    private List<Program> programList;

    /**
     * Constructor method that initializes GuiController.
     *
     * @param gui The RadioInfoUI the user interacts with.
     */
    public GuiController(RadioInfoUI gui) {
        this.gui = gui;
        this.apiCtrl = new ApiController();
        this.backgroundUpdater = new BackgroundUpdater(this, apiCtrl);
    }

    /**
     * Retrieves a program based on its ID.
     *
     * @param programId The ID of the program to retrieve.
     * @return The Program object or null if not found.
     */
    public Program getProgramById(int programId) {
        for (Program program : programList) {
            if (program.getId() == programId) {
                return program;
            }
        }
        return null;
    }

    /**
     * Requests a refresh of the GUI table.
     */
    public void refreshTable() {
        SwingUtilities.invokeLater(this::displayChannelSchedule);
    }

    /**
     * Creates and displays the main GUI.
     */
    public void createAndShowGUI() {
        gui.initializeFrame();
        createMenuBar();
        gui.createMainPanel();
        gui.getFrame().setVisible(true);
    }

    /**
     * Creates the menu bar for the main GUI.
     */
    public void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        gui.createMenu(menuBar, "Alternatives", "Update Channels", e -> backgroundUpdater.updateChannels());
        JMenuItem helpMenuItem = new JMenuItem("Help");
        helpMenuItem.addActionListener(e -> showHelpDialog(gui.getFrame()));
        menuBar.getMenu(0).addSeparator();
        menuBar.getMenu(0).add(helpMenuItem);

        backgroundUpdater.updateChannels();

        gui.getFrame().setJMenuBar(menuBar);
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
     * Updates the channel menus in the main GUI's menu bar.
     * Removes existing channel menus and recreates them.
     */
    public void updateChannelMenus() {
        JMenuBar menuBar = gui.getFrame().getJMenuBar();

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
        gui.getFrame().revalidate();
        gui.getFrame().repaint();
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
     *
     * @param frame The parent frame for the dialog.
     */
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
     * Handles the selection of a channel.
     *
     * @param channelId The ID of the selected channel.
     */
    public void onChannelSelected(int channelId) {
        backgroundUpdater.updateProgramsWithTimer(channelId);
    }

    /**
     * Updates the list of programs for a specific channel.
     *
     * @param channelId The ID of the channel to update programs for.
     */
    public void updateProgramList(int channelId) {
        List<Program> programs = apiCtrl.getSchedule(channelId);

        SwingUtilities.invokeLater(() -> programList = programs);
    }

    private final ListSelectionListener listSelectionListener = this::handleListSelectionEvent;

    /**
     * Handles the event of a list selection change.
     *
     * @param e The ListSelectionEvent.
     */
    private void handleListSelectionEvent(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && gui.getTable().isShowing()) {
            int selectedRow = gui.getTable().getSelectedRow();
            if (selectedRow != -1) {
                Program selectedProgram = getProgramBySelectedRow(selectedRow);
                if (selectedProgram != null) {
                    gui.showProgramInfo(selectedProgram);
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
        return (selectedRow >= 0 && selectedRow < programList.size()) ?
                getProgramById(programList.get(selectedRow).getId()) : null;
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
        clearSelectionListeners(gui.getTable().getSelectionModel());
    }

    /**
     * Updates the GUI table with the list of programs.
     */
    private void updateTableWithPrograms() {
        DefaultTableModel model = (DefaultTableModel) gui.getTable().getModel();
        model.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Program program : programList) {
            Object[] rowData = new Object[]{program.getTitle(), program.getStartTime().format(formatter), program.getEndTime().format(formatter)};
            model.addRow(rowData);
        }
    }

    /**
     * Adds a list selection listener to the table.
     */
    private void addTableSelectionListener() {
        gui.getTable().getSelectionModel().addListSelectionListener(listSelectionListener);
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
