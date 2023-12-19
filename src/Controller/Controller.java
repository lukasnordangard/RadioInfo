package Controller;

import Model.Channel;
import Model.Program;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public List<Program> getProgramList(LocalDateTime startTime) {
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



    public void getChannelFromAPI(int id) {
        StringBuilder response = new StringBuilder();
        try{
            String apiUrl = "https://api.sr.se/api/v2/channels/" + id;
            URL url = new URL(apiUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null){
                response.append(line);
            }
            reader.close();

            List<String> imageUrls = parseXmlResponse(response.toString());

            // Print the image URLs
            for (String ur : imageUrls) {
                System.out.println("\nImage URL: " + ur);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static List<String> parseXmlResponse(String xmlString) {
        List<String> imageUrls = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse the XML string
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));

            // Get a list of "image" elements
            NodeList imageNodes = doc.getElementsByTagName("image");

            for (int i = 0; i < imageNodes.getLength(); i++) {
                Element imageElement = (Element) imageNodes.item(i);
                String imageUrl = imageElement.getTextContent().trim();
                imageUrls.add(imageUrl);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageUrls;
    }

}
