package Model;

import java.time.LocalDateTime;

/**
 * This is a model class that represent a basic structure for storing
 * information about a radio program.
 */
public class Program {

    // Attributes
    private final int id;
    private final int episodeId;
    private final String title;
    private final String description;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String imageUrl;

    /**
     * Constructor method that constructs the program model.
     *
     * @param id            The unique identifier of the program.
     * @param title         The title of the program.
     * @param description   a description of the program.
     * @param startTime     The start time of the program.
     * @param endTime       The end time of the program.
     * @param imageUrl      a url to the image of the program.
     */
    public Program(int id, int episodeId, String title, String description, LocalDateTime startTime, LocalDateTime endTime, String imageUrl) {
        this.id = id;
        this.episodeId = episodeId;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the ID of the program.
     *
     * @return The ID of the program.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the ID of the program.
     *
     * @return The ID of the program.
     */
    public int getEpisodeId() {
        return episodeId;
    }

    /**
     * Gets the name of the program.
     *
     * @return The name of the program.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the description of the program.
     *
     * @return The description of the program.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the start time of the program.
     *
     * @return The start time of the program.
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Gets the end time of the program.
     *
     * @return The end time of the program.
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Gets the image url of the program.
     *
     * @return The image url of the program.
     */
    public String getImageUrl() {
        return imageUrl;
    }
}
