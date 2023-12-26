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

public class Controller {

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
        if (channel.getName().contains("P1")) {
            p1.add(channel);
        } else if (channel.getName().contains("P2")) {
            p2.add(channel);
        } else if (channel.getName().contains("P3")) {
            p3.add(channel);
        } else if (channel.getName().contains("P4")) {
            p4.add(channel);
        } else {
            other.add(channel);
        }
    }

    public static List<Channel> getChannels() {
        List<Channel> channels = new ArrayList<>();
        try {
            String url = "https://api.sr.se/api/v2/channels/?indent=true&pagination=false&sort=name";
            String response = sendGetRequest(url);
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

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        connection.disconnect();

        return response.toString();
    }

    private static List<Channel> parseXmlChannels(String xmlString) {
        List<Channel> channels = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));

            NodeList channelNodes = doc.getElementsByTagName("channel");

            for (int i = 0; i < channelNodes.getLength(); i++) {
                Element channelElement = (Element) channelNodes.item(i);
                int channelId = Integer.parseInt(channelElement.getAttribute("id"));
                String channelName = channelElement.getAttribute("name");
                Node imageNode = channelElement.getElementsByTagName("image").item(0);
                String imageURL = (imageNode != null) ? imageNode.getTextContent() : null;

                Channel channel = new Channel(channelId, channelName, imageURL);
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
            String url = "https://api.sr.se/v2/scheduledepisodes?pagination=false&channelid=" + channelId;
            String response = sendGetRequest(url);
            programs = parseXmlPrograms(response);
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
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));

            NodeList scheduleNodes = doc.getElementsByTagName("scheduledepisode");

            for (int i = 0; i < scheduleNodes.getLength(); i++) {
                Element scheduleElement = (Element) scheduleNodes.item(i);
                String name = scheduleElement.getElementsByTagName("title").item(0).getTextContent();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                LocalDateTime startTime = LocalDateTime.parse(scheduleElement.getElementsByTagName("starttimeutc").item(0).getTextContent(), formatter);
                LocalDateTime endTime = LocalDateTime.parse(scheduleElement.getElementsByTagName("endtimeutc").item(0).getTextContent(), formatter);

                Program program = new Program(name, startTime, endTime);
                programs.add(program);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return programs;
    }
}
