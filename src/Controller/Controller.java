package Controller;

import Model.Program;

import java.time.LocalDateTime;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

    public void API(){
        try {
            // Specify the API endpoint URL
            String apiUrl = "https://api.sr.se/api/v2/channels/?format=json&indent=true&pagination=false&sort=name";

            // Create a URL object
            URL url = new URL(apiUrl);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method (GET in this example)
            connection.setRequestMethod("GET");

            // Read the response from the API
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            // Print the API response
            System.out.println("API Response: " + response.toString());

            // Close the connection
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
