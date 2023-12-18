package Model;

import java.time.LocalDateTime;

public class Program {
    private final String name;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public Program(String name, LocalDateTime startTime, LocalDateTime endTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
