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
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    Parser(){

    }

    /**
     * Parses XML data to extract information about radio channels.
     *
     * @param xmlString The XML data to be parsed.
     * @return A list of Channel objects representing radio channels.
     */
    public List<Channel> parseChannels(String xmlString) {
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

    /**
     * Parses XML data to extract information about radio programs.
     *
     * @param xmlString The XML data to be parsed.
     * @return A list of Program objects representing radio programs.
     */
    public List<Program> parsePrograms(String xmlString) {
        List<Program> programs = new ArrayList<>();

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));

            NodeList scheduleNodes = doc.getElementsByTagName("scheduledepisode");

            for (int i = 0; i < scheduleNodes.getLength(); i++) {
                Element scheduleElement = (Element) scheduleNodes.item(i);

                int id = parseId(scheduleElement);
                String name = parseTitle(scheduleElement);
                String description = parseDescription(scheduleElement);
                LocalDateTime startTime = parseStartTime(scheduleElement);
                LocalDateTime endTime = parseEndTime(scheduleElement);
                String imageUrl = parseImageUrl(scheduleElement);

                if (filterProgram(startTime, endTime)) {
                    Program program = new Program(id, name, description, startTime, endTime, imageUrl);
                    programs.add(program);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return programs;
    }

    private int parseId(Element scheduleElement) {
        Node idNode = scheduleElement.getElementsByTagName("program").item(0).getAttributes().getNamedItem("id");
        return (idNode != null) ? Integer.parseInt(idNode.getTextContent()) : Integer.parseInt(null);
    }

    private String parseTitle(Element scheduleElement) {
        Node titleNode = scheduleElement.getElementsByTagName("title").item(0);
        return (titleNode != null) ? titleNode.getTextContent() : "Missing Title";
    }

    private String parseDescription(Element scheduleElement) {
        Node descriptionNode = scheduleElement.getElementsByTagName("description").item(0);
        return (descriptionNode != null) ? descriptionNode.getTextContent() : "Missing Description";
    }

    private LocalDateTime parseStartTime(Element scheduleElement) {
        Node startTimeNode = scheduleElement.getElementsByTagName("starttimeutc").item(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return (startTimeNode != null) ? LocalDateTime.parse(startTimeNode.getTextContent(), formatter) : null;
    }

    private LocalDateTime parseEndTime(Element scheduleElement) {
        Node endTimeNode = scheduleElement.getElementsByTagName("endtimeutc").item(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return (endTimeNode != null) ? LocalDateTime.parse(endTimeNode.getTextContent(), formatter) : null;
    }

    private String parseImageUrl(Element scheduleElement) {
        Node imageUrlNode = scheduleElement.getElementsByTagName("imageurl").item(0);
        return (imageUrlNode != null) ? imageUrlNode.getTextContent() : "";//Missing Image
    }

    /**
     * Filters radio programs based on their start and end times.
     *
     * @param startTime The start time of the program.
     * @param endTime   The end time of the program.
     * @return True if the program should be included; false otherwise.
     */
    private static boolean filterProgram(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime filterStartTime = now.minusHours(12);
        LocalDateTime filterEndTime = now.plusHours(12);
        return (startTime.isAfter(filterStartTime) || startTime.isEqual(filterStartTime)) &&
                (endTime.isBefore(filterEndTime) || endTime.isEqual(filterEndTime));
    }
}
