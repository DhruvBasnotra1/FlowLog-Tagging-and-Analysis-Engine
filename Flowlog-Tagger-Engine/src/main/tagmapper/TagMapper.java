package main.tagmapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The TagMapper class is responsible for loading and managing the lookup table,
 * which maps port/protocol combinations to specific tags, as well as loading
 * protocol number to name mappings.
 */
public class TagMapper {
    private final Map<String, String> lookupTable;
    private final Map<String, String> protocolMap;

    /**
     * Constructor that initializes the main.tagmapper.TagMapper with the given lookup file
     * and protocol numbers file.
     *
     * @param lookupFilePath        The path to the lookup CSV file.
     * @param protocolNumbersFilePath The path to the protocol numbers CSV file.
     * @throws IOException If the files cannot be read or parsed correctly.
     */
    public TagMapper(String lookupFilePath, String protocolNumbersFilePath) throws IOException {
        this.lookupTable = new HashMap<>();
        this.protocolMap = new HashMap<>();
        loadLookupTable(lookupFilePath);
        loadProtocolNumbers(protocolNumbersFilePath);
    }

    /**
     * Loads the lookup table from a CSV file, where each row defines a mapping between a port,
     * protocol, and a tag.
     *
     * @param lookupFilePath The path to the lookup CSV file.
     * @throws IOException If the file cannot be read or parsed correctly.
     */
    private void loadLookupTable(String lookupFilePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(lookupFilePath))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] parts = line.split(",");
                if (parts.length != 3) {
                    System.err.println("Invalid format  in lookup file for line " + line);
                    continue;
                }

                String dstPort = parts[0].trim().toLowerCase();
                String protocol = parts[1].trim().toLowerCase();
                String tag = parts[2].trim();

                if (dstPort.isEmpty() || protocol.isEmpty() || tag.isEmpty()) {
                    System.err.println("Missing data at line " + lineNumber + " in lookup file.");
                    continue;
                }

                String key = dstPort + "," + protocol;
                lookupTable.put(key, tag);
            }
        } catch (IOException e) {
            throw new IOException("Failed to load lookup table from " + lookupFilePath, e);
        }
    }

    /**
     * Loads the protocol numbers from a CSV file and maps them to protocol names.
     *
     * @param protocolNumbersFilePath The path to the protocol numbers CSV file.
     * @throws IOException If the file cannot be read or parsed correctly.
     */
    private void loadProtocolNumbers(String protocolNumbersFilePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(protocolNumbersFilePath))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                //The first line contains the headers
                if(lineNumber == 1){
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length  < 2) {
                    System.err.println("Invalid format in protocol numbers file in line" + line);
                    continue;
                }

                String protocolNumber = parts[0].trim();
                String protocolName = parts[1].trim().toLowerCase();

                if (protocolNumber.isEmpty() || protocolName.isEmpty()) {
                    System.err.println("Missing data in protocol numbers file for line" + line);
                    continue;
                }

                //Validates if port is only a positive number
                if(!protocolNumber.matches("\\d+(\\.\\d+)?")){
                   continue;
                }


                protocolMap.put(protocolNumber, protocolName);
            }
        } catch (IOException e) {
            throw new IOException("Failed to load protocol numbers from " + protocolNumbersFilePath, e);
        }
    }

    /**
     * Retrieves the protocol name corresponding to the given protocol number.
     *
     * @param protocolNumber The protocol number to look up.
     * @return The corresponding protocol name if found; otherwise, returns "unknown".
     */
    public String getProtocolName(String protocolNumber) {
        return protocolMap.getOrDefault(protocolNumber, "unknown");
    }

    /**
     * Retrieves the tag corresponding to the given port and protocol combination.
     *
     * @param dstport  The destination port to look up.
     * @param protocol The protocol name to look up.
     * @return The corresponding tag if found; otherwise, returns "Untagged".
     */
    public String getTag(String dstport, String protocol) {
        String key = dstport.trim().toLowerCase() + "," + protocol.trim().toLowerCase();
        return lookupTable.getOrDefault(key, "Untagged");
    }
}

