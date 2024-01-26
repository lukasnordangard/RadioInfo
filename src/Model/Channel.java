package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a model class that represent a basic structure for storing
 * information about a radio channel.
 */
public class Channel {

    // Attributes
    private final int id;
    private final String name;
    private final List<Program> schedule;

    /**
     * Constructor method that constructs the channel model.
     *
     * @param id    The unique identifier of the channel.
     * @param name  The name of the channel.
     */
    public Channel(int id, String name) {
        this.id = id;
        this.name = name;
        this.schedule = new ArrayList<>();
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

    /**
     * Gets the schedule of the channel.
     *
     * @return The channel schedule.
     */
    public List<Program> getSchedule() {
        return schedule;
    }

    /**
     * Sets the schedule of the channel.
     *
     * @param schedule The channel schedule.
     */
    public void setSchedule(List<Program> schedule){
        this.schedule.clear();
        for (Program episode : schedule) {
            addEpisodeToSchedule(episode);
        }
    }

    /**
     * Adds one episode to the schedule of the channel.
     *
     * @param program The episode to be added.
     */
    public void addEpisodeToSchedule(Program program) {
        this.schedule.add(program);
    }
}
