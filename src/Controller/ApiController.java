package Controller;

import Model.Channel;
import Model.Program;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class responsible for all API communication and uses the
 * model classes to display information in the GUI.
 */
public class ApiController {

    // Attributes
    private final Parser parser;

    /**
     * Constructor method that initializes ApiController.
     */
    public ApiController(){
        this.parser = new Parser();
    }

    /**
     * Retrieves a list of radio channels from the API.
     *
     * @return              A list of Channel objects representing radio channels.
     * @throws Exception    If an error occurs during the HTTP request.
     */
    public synchronized List<Channel> getChannels() throws Exception {
        List<Channel> channels;
        String apiUrl = "https://api.sr.se/api/v2/channels/?indent=true&pagination=false&sort=name";
        String response = sendGetRequest(apiUrl);

        System.out.println("--Update channels"  + " - (" + Thread.currentThread().getName() + ")");

        channels = parser.parseChannels(response);

        return channels;
    }

    /**
     * Sends an HTTP GET request to the specified URL and returns the response as a string.
     *
     * @param apiUrl        The URL to send the GET request to.
     * @return              The response as a string.
     * @throws Exception    If an error occurs during the HTTP request.
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
     * Retrieves a list of programs for a given channel and date from the API.
     *
     * @param channelId     The ID of the channel.
     * @param date          The date for which to retrieve the schedule.
     * @return              A list of Program objects representing programs.
     * @throws Exception    If an error occurs during the HTTP request.
     */
    private List<Program> getSchedule(int channelId, String date)
            throws Exception {
        List<Program> programs;

        String apiUrl = "https://api.sr.se/v2/scheduledepisodes?pagination=false&channelid=" + channelId + "&date=" + date;
        String response = sendGetRequest(apiUrl);

        programs = parser.parsePrograms(response);

        return programs;
    }

    /**
     * Constructs a list consisting of all episodes in a schedule based on date and time.
     *
     * @param channelId     The id of the channel to the schedule.
     * @return              The list of programs consisting of the complete schedule.
     * @throws Exception    If an error occurs during the HTTP request.
     */
    public List<Program> getAllEpisodesInSchedule(int channelId) throws Exception {
        LocalDate currentDate = java.time.LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        List<Program> dayCurrent = getSchedule(channelId,currentDate.toString());
        List<Program> episodesToReturn = new ArrayList<>(dayCurrent);
        if (currentTime.getHour() >= 12) {
            List<Program> dayAfter = getSchedule(channelId,currentDate.plusDays(1).toString());
            episodesToReturn.addAll(dayAfter);
        } else {
            List<Program> dayBefore = getSchedule(channelId,currentDate.minusDays(1).toString());
            episodesToReturn.addAll(0, dayBefore);
        }
        return episodesToReturn;
    }

    public synchronized List<Channel> updateAllCachedSchedules(List<Channel> cacheCopy) throws Exception {
        for (Channel channel : cacheCopy){

            System.out.println("Update " + channel.getName() + " - (" + Thread.currentThread().getName() + ")");

            List<Program> schedule = getAllEpisodesInSchedule(channel.getId());
            channel.setSchedule(schedule);
        }
        return cacheCopy;
    }
}
