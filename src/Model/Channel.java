package Model;

public class Channel {
    private final int id;
    private final String name;

    public Channel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
