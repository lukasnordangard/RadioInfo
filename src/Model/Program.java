package Model;

import java.time.LocalDateTime;

public class Program {
    private final int episodeId;
    private final int programId;
    private final String name;
    private final String description;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String imageUrl;

    public Program(int episodeId, int programId, String name, String description, LocalDateTime startTime, LocalDateTime endTime, String imageUrl) {
        this.episodeId = episodeId;
        this.programId = programId;
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.imageUrl = imageUrl;
    }

    public int getEpisodeId() { return episodeId; }

    public int getProgramId() { return programId; }

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
}
