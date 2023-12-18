package Controller;

import Model.Program;

import java.time.LocalDateTime;
import java.util.List;

public class Controller {
    public List<String> getChannelList() {
        return List.of("Channel1", "Channel2", "Channel3");
    }

    public List<Program> getProgramList(LocalDateTime startTime) {
        return List.of(
                new Program("Program1", startTime, startTime.plusMinutes(30)),
                new Program("Program2", startTime.plusMinutes(30), startTime.plusHours(1)),
                new Program("Program3", startTime.plusHours(1), startTime.plusHours(2))
        );
    }
}
