package Controller;

import Model.Channel;
import Model.Program;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ApiController {

    public List<Channel> p1 = new ArrayList<>();
    public List<Channel> p2 = new ArrayList<>();
    public List<Channel> p3 = new ArrayList<>();
    public List<Channel> p4 = new ArrayList<>();
    public List<Channel> other = new ArrayList<>();

    public void loadChannels() {
        List<Channel> channels = getChannels();

        for (Channel channel : channels) {
            filterAndAddChannel(channel);
        }
    }

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

    public static List<Channel> getChannels() {
        List<Channel> channels = new ArrayList<>();
        try {
            String apiUrl = "https://api.sr.se/api/v2/channels/?indent=true&pagination=false&sort=name";
            String response = sendGetRequest(apiUrl);
            channels = parseXmlChannels(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channels;
    }

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

    private static List<Channel> parseXmlChannels(String xmlString) {
        List<Channel> channels = new ArrayList<>();

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));

            NodeList channelNodes = doc.getElementsByTagName("channel");

            for (int i = 0; i < channelNodes.getLength(); i++) {
                Element channelElement = (Element) channelNodes.item(i);
                int channelId = Integer.parseInt(channelElement.getAttribute("id"));
                String channelName = channelElement.getAttribute("name");

                Channel channel = new Channel(channelId, channelName);
                channels.add(channel);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return channels;
    }

    public static List<Program> getSchedule(int channelId) {
        List<Program> programs = new ArrayList<>();
        try {
            String apiUrl = "https://api.sr.se/v2/scheduledepisodes?pagination=false&channelid=" + channelId;
            String response = sendGetRequest(apiUrl);
            programs = parseXmlPrograms(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return programs;
    }

    private static List<Program> parseXmlPrograms(String xmlString) {
        List<Program> programs = new ArrayList<>();

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));

            NodeList scheduleNodes = doc.getElementsByTagName("scheduledepisode");

            for (int i = 0; i < scheduleNodes.getLength(); i++) {
                Element scheduleElement = (Element) scheduleNodes.item(i);

                String name = scheduleElement.getElementsByTagName("title").item(0).getTextContent();
                Node descriptionNode = scheduleElement.getElementsByTagName("description").item(0);
                String description = (descriptionNode != null) ? descriptionNode.getTextContent() : "";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                LocalDateTime startTime = LocalDateTime.parse(scheduleElement.getElementsByTagName("starttimeutc").item(0).getTextContent(), formatter);
                LocalDateTime endTime = LocalDateTime.parse(scheduleElement.getElementsByTagName("endtimeutc").item(0).getTextContent(), formatter);
                int programId = Integer.parseInt(scheduleElement.getElementsByTagName("program").item(0).getAttributes().getNamedItem("id").getTextContent());
                Node imageUrlNode = scheduleElement.getElementsByTagName("imageurl").item(0);
                String imageUrl = (imageUrlNode != null) ? imageUrlNode.getTextContent() : "";

                if (filterProgram(startTime, endTime)) {
                    Program program = new Program(programId, name, description, startTime, endTime, imageUrl);
                    programs.add(program);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return programs;
    }

    private static boolean filterProgram(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime filterStartTime = now.minusHours(12);
        LocalDateTime filterEndTime = now.plusHours(12);
        return (startTime.isAfter(filterStartTime) || startTime.isEqual(filterStartTime)) &&
                (endTime.isBefore(filterEndTime) || endTime.isEqual(filterEndTime));
    }
}
