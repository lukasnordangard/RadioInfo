package Model;

import java.util.LinkedList;

public class Channel {
    private final int id;
    private final String name;
    private final String imageURL;
    private final LinkedList<Program> schedule;

    public Channel(int id, String name, String imageURL) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        this.schedule = new LinkedList<>();
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

    public LinkedList<Program> getSchedule() {
        return schedule;
    }
}
