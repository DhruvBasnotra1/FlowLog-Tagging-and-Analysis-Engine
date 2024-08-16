package main.flowlogparser;

import main.tagmapper.TagMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The main.flowlogparser.FlowLogParser class is responsible for reading and processing the flow log file,
 * mapping each log entry to a tag based on the lookup table, and counting occurrences
 * of each tag and port/protocol combination.
 */
public class FlowLogParser {
    private final TagMapper tagMapper;
    private final Map<String, Integer> tagCounts;
    private final Map<String, Integer> portProtocolCounts;

    /**
     * Constructor that initializes the main.flowlogparser.FlowLogParser with a main.tagmapper.TagMapper instance.
     *
     * @param tagMapper The main.tagmapper.TagMapper instance used to map port/protocol combinations to tags.
     */
    public FlowLogParser(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
        this.tagCounts = new HashMap<>();
        this.portProtocolCounts = new HashMap<>();
    }

    /**
     * Processes the flow log file by reading each line, extracting the relevant fields,
     * mapping them to a tag using the main.tagmapper.TagMapper, and updating the count of occurrences
     * for each tag and port/protocol combination.
     *
     * @param flowLogFilePath The path to the flow log file.
     * @throws IOException If the file cannot be read or parsed correctly.
     */
    public void processFlowLog(String flowLogFilePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(flowLogFilePath))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] parts = line.split("\\s+");  // Assuming space-separated fields based on VPC Flow Logs format

                if (parts.length < 2) {  // dstPort and protocol are essential fields
                    System.err.println("Incomplete data in flow log file for line " + line);
                    continue;
                }

                String dstPort = parts[6];  // Assuming dstPort is in the 6th column
                String protocol = parts[7]; // Assuming protocol is in the 7th column
                if(dstPort.equals("-") ||  protocol.equals("-")){
                    System.err.println("Missing data in flow log file for line " + line);
                    continue;
                }
                String protocolName = tagMapper.getProtocolName(protocol);
                if(protocolName.equals("unknown")){
                    System.err.println("Missing protocolName for protocolNumber" + protocol);
                    continue;
                }
                String tag = tagMapper.getTag(dstPort, protocolName);
                tagCounts.put(tag, tagCounts.getOrDefault(tag, 0) + 1);

                String portProtocolKey = dstPort + " " + protocolName;
                portProtocolCounts.put(portProtocolKey, portProtocolCounts.getOrDefault(portProtocolKey, 0) + 1);
            }
        } catch (IOException e) {
            throw new IOException("Failed to process flow log from " + flowLogFilePath, e);
        }
    }

    /**
     * Retrieves the count of occurrences for each tag.
     *
     * @return A map of tags to their respective counts.
     */
    public Map<String, Integer> getTagCounts() {
        return tagCounts;
    }

    /**
     * Retrieves the count of occurrences for each port/protocol combination.
     *
     * @return A map of port/protocol combinations to their respective counts.
     */
    public Map<String, Integer> getPortProtocolCounts() {
        return portProtocolCounts;
    }
}

