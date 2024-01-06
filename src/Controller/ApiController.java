package Controller;

import Model.Channel;
import Model.Program;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This controller class is responsible for all API communication and uses the
 * model classes to display information in the GUI.
 */
public class ApiController {

    // Lists to categorize channels based on their names
    public List<Channel> p1 = new ArrayList<>();
    public List<Channel> p2 = new ArrayList<>();
    public List<Channel> p3 = new ArrayList<>();
    public List<Channel> p4 = new ArrayList<>();
    public List<Channel> other = new ArrayList<>();
    private final Parser parser = new Parser();

    /**
     * Fetches channel information from the API, categorizes channels, and loads them into lists.
     */
    public void loadChannels() {
        List<Channel> channels = getChannels();

        for (Channel channel : channels) {
            filterAndAddChannel(channel);
        }
    }

    /**
     * Filters and adds a channel to the appropriate list based on its name.
     *
     * @param channel The channel to be categorized and added to a list.
     */
    private void filterAndAddChannel(Channel channel) {
        String channelName = channel.getName();
        if (channelName.contains("P1")) {
            p1.add(channel);
        } else if (channelName.contains("P2")) {
            p2.add(channel);
        } else if (channelName.contains("P3")) {
            p3.add(channel);
        } else if (channelName.contains("P4")) {
            p4.add(channel);
        } else {
            other.add(channel);
        }
    }

    /**
     * Retrieves a list of radio channels from the API.
     *
     * @return A list of Channel objects representing radio channels.
     */
    public List<Channel> getChannels() {
        List<Channel> channels = new ArrayList<>();
        try {
            String apiUrl = "https://api.sr.se/api/v2/channels/?indent=true&pagination=false&sort=name";
            String response = sendGetRequest(apiUrl);
            channels = parser.parseChannels(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channels;
    }

    /**
     * Sends an HTTP GET request to the specified URL and returns the response as a string.
     *
     * @param apiUrl The URL to send the GET request to.
     * @return The response as a string.
     * @throws Exception If an error occurs during the HTTP request.
     */
    private static String sendGetRequest(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return response.toString();
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Retrieves the schedule of programs for a given channel ID from the API.
     *
     * @param channelId The ID of the channel for which to retrieve the schedule.
     * @return A list of Program objects representing the schedule of programs for the channel.
     */
    public List<Program> getSchedule(int channelId) {
        List<Program> programs = new ArrayList<>();
        try {
            String apiUrl = "https://api.sr.se/v2/scheduledepisodes?pagination=false&channelid=" + channelId;
            String response = sendGetRequest(apiUrl);
            programs = parser.parsePrograms(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return programs;
    }

}