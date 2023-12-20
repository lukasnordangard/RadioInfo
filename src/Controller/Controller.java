package Controller;

import Model.Channel;
import Model.Program;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class Controller {

    public static List<Program> getProgramList(LocalDateTime startTime) {
        return List.of(
                new Program("Program1", startTime, startTime.plusMinutes(30)),
                new Program("Program2", startTime.plusMinutes(30), startTime.plusHours(1)),
                new Program("Program3", startTime.plusHours(1), startTime.plusHours(2))
        );
    }

    public static List<Channel> getChannels() {
        List<Channel> channels =  new ArrayList<>();
        try {
            // Send GET request to API
            String apiUrl = "https://api.sr.se/api/v2/channels/?indent=true&pagination=false&sort=name";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response from the API
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            // Parse the XML response
            channels = parseXmlChannels(response.toString());

            // Close the connection
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return channels;
    }

    private static List<Channel> parseXmlChannels(String xmlString) {
        List<Channel> channels = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse the XML string
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));

            // Get the list of channel elements
            NodeList channelNodes = doc.getElementsByTagName("channel");

            for (int i = 0; i < channelNodes.getLength(); i++) {
                Element channelElement = (Element) channelNodes.item(i);

                // Extract channel information
                int channelId = Integer.parseInt(channelElement.getAttribute("id"));
                String channelName = channelElement.getAttribute("name");

                // Check if the <image> element exists
                Node imageNode = channelElement.getElementsByTagName("image").item(0);
                String imageURL = (imageNode != null) ? imageNode.getTextContent() : null;

                // Create a new Channel object
                Channel channel = new Channel(channelId, channelName, imageURL);

                // Add the channel to the list
                channels.add(channel);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return channels;
    }

    public static List<Program> getSchedule(int channelId) {
        List<Program> programs =  new ArrayList<>();
        try {
            // Send GET request to API
            String scheduleurl = "https://api.sr.se/v2/scheduledepisodes?pagination=false&channelid=" + channelId;
            URL url = new URL(scheduleurl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response from the API
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            // Parse the XML response
            programs = parseXmlPrograms(response.toString());

            // Close the connection
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return programs;
    }

    private static List<Program> parseXmlPrograms(String xmlString) {
        List<Program> programs = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse the XML string
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));

            // Get the list of scheduleepisode elements
            NodeList scheduleNodes = doc.getElementsByTagName("scheduledepisode");

            for (int i = 0; i < scheduleNodes.getLength(); i++) {
                Element scheduleElement = (Element) scheduleNodes.item(i);

                // Extract program information
                String name = scheduleElement.getElementsByTagName("title").item(0).getTextContent();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                LocalDateTime startTime = LocalDateTime.parse(scheduleElement.getElementsByTagName("starttimeutc").item(0).getTextContent(), formatter);
                LocalDateTime endTime = LocalDateTime.parse(scheduleElement.getElementsByTagName("endtimeutc").item(0).getTextContent(), formatter);

                // Create a new Program object
                Program program = new Program(name, startTime, endTime);

                // Add the program to the list
                programs.add(program);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return programs;
    }

}
