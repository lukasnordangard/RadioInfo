package Model;

import java.util.ArrayList;
import java.util.List;

public class Channel {
    private final int id;
    private final String name;
    private final String imageURL;
    private final List<Program> schedule;

    public Channel(int id, String name, String imageURL) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        this.schedule = new ArrayList<>();
    }

    public void addProgram(Program program) {
        schedule.add(program);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public List<Program> getSchedule() {
        return schedule;
    }
}
