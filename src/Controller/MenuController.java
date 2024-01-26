package Controller;

import Model.Channel;
import View.RadioInfoUI;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MenuController {

    private final RadioInfoUI view;
    private final ApiController apiController;
    private final GuiController guiController;

    private List<Channel> allChannels = new ArrayList<>();
    private final List<Channel> p1 = new ArrayList<>();
    private final List<Channel> p2 = new ArrayList<>();
    private final List<Channel> p3 = new ArrayList<>();
    private final List<Channel> p4 = new ArrayList<>();
    private final List<Channel> other = new ArrayList<>();

    public MenuController(GuiController guiController, RadioInfoUI view){
        this.view = view;
        this.apiController = new ApiController();
        this.guiController = guiController;
    }

    public List<Channel> getAllChannels(){
        return allChannels;
    }

    public void setAllChannels(List<Channel> allChannels) {
        this.allChannels = allChannels;
    }

    public Channel getChannelById(int channelId){
        for (Channel channel : getAllChannels()) {
            if (channel.getId() == channelId) {
                return channel;
            }
        }
        return null;
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

        view.createMenu(menuBar, "Alternatives", "Update Channels", e -> {
            if(guiController.getCachedChannels().isEmpty()){
                //backgroundUpdater.updateChannelsOnBackground();
                ChannelUpdater channelUpdater = new ChannelUpdater(this, apiController);
                channelUpdater.execute();
            }else {
                //backgroundUpdater.updateChannels(); // This forgets cached channels before button press
                BackgroundUpdater backgroundUpdater = new BackgroundUpdater(guiController, apiController);
                backgroundUpdater.updateCachedSchedules(guiController.getCachedChannels());
            }
        });
        JMenuItem helpMenuItem = new JMenuItem("Help");
        helpMenuItem.addActionListener(e -> guiController.showHelpDialog());
        menuBar.getMenu(0).addSeparator();
        menuBar.getMenu(0).add(helpMenuItem);

        ChannelUpdater channelUpdater = new ChannelUpdater(this, apiController);
        channelUpdater.execute();

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
    public void createChannelMenu(JMenuBar menuBar, String menuName, List<Channel> channels) {
        JMenu channelMenu = new JMenu(menuName);
        for (Channel channel : channels) {
            JMenuItem channelMenuItem = new JMenuItem(channel.getName());
            int id = channel.getId();
            channelMenuItem.addActionListener(e -> guiController.onChannelSelected(id));
            channelMenu.add(channelMenuItem);
        }
        menuBar.add(channelMenu);
    }

}
