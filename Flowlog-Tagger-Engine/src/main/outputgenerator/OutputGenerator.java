package main.outputgenerator;

import main.flowlogparser.FlowLogParser;
import main.tagmapper.TagMapper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * The OutputGenerator class is responsible for writing the results of the flow log
 * processing to output files, including the counts of tags and port/protocol combinations.
 */
public class OutputGenerator {
    /**
     * Generates the output files based on the processed flow log data.
     *
     * @param tagMapper      The TagMapper instance used (not directly used in this method but passed for context).
     * @param flowLogParser  The FlowLogParser instance containing the processed data.
     * @throws IOException If there is an issue writing the output files.
     */
    public static void generateOutput(TagMapper tagMapper, FlowLogParser flowLogParser) throws IOException {
        try {
            generateTagCountsOutput(flowLogParser.getTagCounts());
            generatePortProtocolCountsOutput(flowLogParser.getPortProtocolCounts());
            System.out.println("Output files generated successfully.");
        } catch (IOException e) {
            throw new IOException("Failed to generate output files.", e);
        }
    }

    /**
     * Writes the count of each tag to an output file.
     *
     * @param tagCounts A map of tags to their respective counts.
     * @throws IOException If there is an issue writing the file.
     */
    private static void generateTagCountsOutput(Map<String, Integer> tagCounts) throws IOException {
        try (FileWriter writer = new FileWriter("tag_counts.txt")) {
            writer.write("Tag Counts:\n\n");
            writer.write("Tag\t\tCount\n");

            for (Map.Entry<String, Integer> entry : tagCounts.entrySet()) {
                writer.write(entry.getKey() + "\t\t" + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            throw new IOException("Failed to write tag counts output.", e);
        }
    }

    /**
     * Writes the count of each port/protocol combination to an output file.
     *
     * @param portProtocolCounts A map of port/protocol combinations to their respective counts.
     * @throws IOException If there is an issue writing the file.
     */
    private static void generatePortProtocolCountsOutput(Map<String, Integer> portProtocolCounts) throws IOException {
        try (FileWriter writer = new FileWriter("port_protocol_counts.txt")) {
            writer.write("Port/Protocol Combination Counts:\n\n");
            writer.write("Port\tProtocol\tCount\n");

            for (Map.Entry<String, Integer> entry : portProtocolCounts.entrySet()) {
                String[] parts = entry.getKey().split(" ");
                writer.write(parts[0] + "\t" + parts[1] + "\t\t" + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            throw new IOException("Failed to write port/protocol counts output.", e);
        }
    }
}
