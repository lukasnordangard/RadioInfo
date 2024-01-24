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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for parsing and filtering XML data provided by
 * the API to the model classes Channel and Program.
 */
public class Parser {

    /**
     * Constructor method that initializes Parser.
     */
    public Parser() {}

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

                int channelId = parseChannelId(channelElement);
                String channelName = parseChannelName(channelElement);

                // Handle if id is missing
                if (channelId != -1){
                    Channel channel = new Channel(channelId, channelName);
                    channels.add(channel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return channels;
    }

    /**
     * Parses the channel ID from the given channelElement.
     *
     * @param channelElement The XML element representing a radio channel.
     * @return The parsed channel ID.
     */
    private int parseChannelId(Element channelElement) {
        Node idNode = channelElement.getAttributeNode("id");
        return (idNode != null) ? Integer.parseInt(idNode.getTextContent()) : -1;
    }

    /**
     * Parses the channel name from the given channelElement.
     *
     * @param channelElement The XML element representing a radio channel.
     * @return The parsed channel name.
     */
    private String parseChannelName(Element channelElement) {
        Node nameNode = channelElement.getAttributeNode("name");
        return (nameNode != null) ? nameNode.getTextContent() : "MISSING CHANNEL NAME";
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

                int id = parseProgramId(scheduleElement);
                int episodeId = parseEpisodeId(scheduleElement);
                String title = parseProgramTitle(scheduleElement);
                String description = parseProgramDescription(scheduleElement);
                LocalDateTime startTime = parseProgramStartTime(scheduleElement);
                LocalDateTime endTime = parseProgramEndTime(scheduleElement);
                String imageUrl = parseProgramImageUrl(scheduleElement);

                // Filter programs after time and handle if programId or episodeId is missing
                if (filterProgram(startTime, endTime) && (id != -1 || episodeId != -1)) {
                    Program program = new Program(id, episodeId, title, description, startTime, endTime, imageUrl);
                    programs.add(program);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return programs;
    }

    /**
     * Parses the program ID from the given scheduleElement.
     *
     * @param scheduleElement The XML element representing a radio program.
     * @return The parsed program ID.
     */
    private int parseProgramId(Element scheduleElement) {
        Node idNode = scheduleElement.getElementsByTagName("program").item(0).getAttributes().getNamedItem("id");
        return (idNode != null) ? Integer.parseInt(idNode.getTextContent()) : -1;
    }

    private int parseEpisodeId(Element scheduleElement) {
        Node episodeNode = scheduleElement.getElementsByTagName("episodeid").item(0);
        return (episodeNode != null) ? Integer.parseInt(episodeNode.getTextContent()) : -1;
    }

    /**
     * Parses the program title from the given scheduleElement.
     *
     * @param scheduleElement The XML element representing a radio program.
     * @return The parsed program title.
     */
    private String parseProgramTitle(Element scheduleElement) {
        Node titleNode = scheduleElement.getElementsByTagName("title").item(0);
        return (titleNode != null) ? titleNode.getTextContent() : "MISSING PROGRAM TITLE";
    }

    /**
     * Parses the program description from the given scheduleElement.
     *
     * @param scheduleElement The XML element representing a radio program.
     * @return The parsed program description.
     */
    private String parseProgramDescription(Element scheduleElement) {
        Node descriptionNode = scheduleElement.getElementsByTagName("description").item(0);
        return (descriptionNode != null) ? descriptionNode.getTextContent() : "MISSING PROGRAM DESCRIPTION";
    }

    /**
     * Parses the program time from the given scheduleElement based on the specified time key.
     *
     * @param scheduleElement The XML element representing a radio program.
     * @param timeKey         The key for the time element (e.g., "starttimeutc" or "endtimeutc").
     * @return The parsed program time.
     */
    private LocalDateTime parseProgramTime(Element scheduleElement, String timeKey) {
        Node timeNode = scheduleElement.getElementsByTagName(timeKey).item(0);
        if (timeNode != null) {
            String timeString = timeNode.getTextContent();
            ZonedDateTime originalZonedDateTime = ZonedDateTime.parse(timeString);
            ZonedDateTime adjustedZonedDateTime = originalZonedDateTime.plusHours(1);

            // Use ZonedDateTime directly for formatting
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            return LocalDateTime.parse(adjustedZonedDateTime.format(formatter), formatter);
        } else {
            return null;
        }
    }

    /**
     * Parses the program start time from the given scheduleElement.
     *
     * @param scheduleElement The XML element representing a radio program.
     * @return The parsed program start time.
     */
    private LocalDateTime parseProgramStartTime(Element scheduleElement) {
        return parseProgramTime(scheduleElement, "starttimeutc");
    }

    /**
     * Parses the program end time from the given scheduleElement.
     *
     * @param scheduleElement The XML element representing a radio program.
     * @return The parsed program end time.
     */
    private LocalDateTime parseProgramEndTime(Element scheduleElement) {
        return parseProgramTime(scheduleElement, "endtimeutc");
    }

    /**
     * Parses the program image URL from the given scheduleElement.
     *
     * @param scheduleElement The XML element representing a radio program.
     * @return The parsed program image URL.
     */
    private String parseProgramImageUrl(Element scheduleElement) {
        Node imageUrlNode = scheduleElement.getElementsByTagName("imageurl").item(0);
        return (imageUrlNode != null) ? imageUrlNode.getTextContent() : ""; // MISSING PROGRAM IMAGE
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
