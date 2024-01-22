package Model;

import java.util.List;

/**
 * This is a model class that represent a basic structure for storing
 * information about a radio channel.
 */
public class Channel {

    // Attributes
    private final int id;
    private final String name;
    private final List<Program> programList = null;

    /**
     * Constructor method that constructs the channel model.
     *
     * @param id    The unique identifier of the channel.
     * @param name  The name of the channel.
     */
    public Channel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Gets the ID of the channel.
     *
     * @return The ID of the channel.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the name of the channel.
     *
     * @return The name of the channel.
     */
    public String getName() {
        return name;
    }
}
