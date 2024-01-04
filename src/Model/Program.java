package Model;

import java.time.LocalDateTime;

/**
 *
 */
public class Program {
    private final String name;
    private final String description;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String imageUrl;

    public Program(String name, String description, LocalDateTime startTime, LocalDateTime endTime, String imageUrl) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getImageUrl(){
        return imageUrl;
    }
}
