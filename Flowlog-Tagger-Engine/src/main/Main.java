package main;

import main.flowlogparser.FlowLogParser;
import main.outputgenerator.OutputGenerator;
import main.tagmapper.TagMapper;

import java.io.IOException;

/**
 * The Main class serves as the entry point for the FlowLog Tagging and Analysis Engine (FLTAE).
 * It initializes the necessary components and orchestrates the flow of processing the lookup table
 * and flow log file.
 */
public class Main {
    /**
     * The main method initializes the main.tagmapper.TagMapper and main.flowlogparser.FlowLogParser, processes the flow log,
     * and generates the output files.
     *
     * @param args Command-line arguments, where args[0] is the path to the lookup file and args[1] is the path to the flow log file.
     *             and arg[2] is protocol-number converter file(since the protocol is received inform of numbers in flowLog file this file
     *            the number to the protocol )
     */
    public static void main(String[] args) {

        if (args.length < 3) {
            System.err.println("Please provide the paths to the lookup file and the flow log file and protocol-number file");
            System.out.println("Usage: java main.Main <lookup_file> <flow_log_file> <protocol_number_file>");
            return;
        }

        String lookupFile = args[0];
        String flowLogFile = args[1];
        String protocolNumbersFile = args[2];
        try {
            TagMapper tagMapper = new TagMapper(lookupFile, protocolNumbersFile);
            FlowLogParser flowLogParser = new FlowLogParser(tagMapper);
            flowLogParser.processFlowLog(flowLogFile);
            OutputGenerator.generateOutput(tagMapper, flowLogParser);
            System.out.println("Processing completed. Check the output directory for results.");
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }
}

