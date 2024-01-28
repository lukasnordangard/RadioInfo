package Controller;

import Model.Channel;
import View.RadioInfoUI;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class responsible for constructing and managing the menu bar.
 */
public class MenuController {

    // Attributes
    private List<Channel> allChannels = new ArrayList<>();
    private final List<Channel> p1 = new ArrayList<>();
    private final List<Channel> p2 = new ArrayList<>();
    private final List<Channel> p3 = new ArrayList<>();
    private final List<Channel> p4 = new ArrayList<>();
    private final List<Channel> other = new ArrayList<>();
    private final RadioInfoUI view;
    private final GuiController guiController;
    private int lastSelectedChannel = -1;

    /**
     * Constructor method that initializes MenuController.
     */
    public MenuController(GuiController guiController, RadioInfoUI view){
        this.view = view;
        this.guiController = guiController;
    }

    public int getLastSelectedChannel() {
        return lastSelectedChannel;
    }

    /**
     * Gets all of the channels in the menu.
     *
     * @return list of all channels.
     */
    public List<Channel> getAllChannels(){
        return allChannels;
    }

    /**
     * Sets all channels in the menu.
     *
     * @param allChannels list of all channels.
     */
    public void setAllChannels(List<Channel> allChannels) {
        this.allChannels = allChannels;
    }

    /**
     * Clears the lists containing channels for different categories.
     * Clears the lists p1, p2, p3, p4, and other.
     */
    public void clearChannels(){
        p1.clear();
        p2.clear();
        p3.clear();
        p4.clear();
        other.clear();
    }

    /**
     * Filters and adds a channel to the appropriate list based on its name.
     */
    public void filterAndAddChannel() {
        for (Channel channel : allChannels) {
            String channelName = channel.getName();
            if (channelName.contains("P1")) {
                p1.add(channel);
            } else if (channelName.contains("P2")) {
                p2.add(channel);
            } else if (channelName.contains("P3")) {
                p3.add(channel);
            } else if (channelName.contains("P4")) {
                p4.add(channel);
            } else {
                other.add(channel);
            }
        }
    }

    /**
     * Creates the menu bar for the main GUI.
     */
    public void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        view.createMenu(menuBar, "File", "Help", e -> guiController.showHelpDialog());
        JMenuItem helpMenuItem = new JMenuItem("Exit");
        helpMenuItem.addActionListener(e -> System.exit(0));
        menuBar.getMenu(0).addSeparator();
        menuBar.getMenu(0).add(helpMenuItem);

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
    private void createChannelMenus(JMenuBar menuBar){
        createChannelMenu(menuBar, "P1", p1);
        createChannelMenu(menuBar, "P2", p2);
        createChannelMenu(menuBar, "P3", p3);
        createChannelMenu(menuBar, "P4", p4);
        createChannelMenu(menuBar, "Other", other);
    }

    /**
     * Creates a channel menu with items for each channel.
     *
     * @param menuBar   The main menu bar.
     * @param menuName  The name of the channel menu.
     * @param channels  The list of channels to be displayed in the menu.
     */
    private void createChannelMenu(JMenuBar menuBar, String menuName, List<Channel> channels) {
        JMenu channelMenu = new JMenu(menuName);
        for (Channel channel : channels) {
            JMenuItem channelMenuItem = new JMenuItem(channel.getName());
            int id = channel.getId();
            channelMenuItem.addActionListener(e -> {
                lastSelectedChannel = id;
                guiController.onChannelSelected(id);
            });
            channelMenu.add(channelMenuItem);
        }
        menuBar.add(channelMenu);
    }

}
