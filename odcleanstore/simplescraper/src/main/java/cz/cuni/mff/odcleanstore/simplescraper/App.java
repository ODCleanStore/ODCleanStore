package cz.cuni.mff.odcleanstore.simplescraper;

import cz.cuni.mff.odcleanstore.wsclient.Metadata;
import cz.cuni.mff.odcleanstore.wsclient.ODCSService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * Example of usage of odcs-inputclient Java client for ODCleanStore Input Webservice.
 * @author Petr Jerman
 */
public final class App {
    private static final String INPUT_WS_LOCATION = "http://localhost:8088/inputws";
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static final String DEFAULT_ENCODING = "UTF-8";

    private App() {
        // Hide constructor
    }

    public static void main(String[] args) {
        ODCSService sc;
        Properties props = new Properties();
        try {
            if (args.length < 2) {
                printUsageAndExit();
            }

            File metadataPropertyFile = new File(args[0]);
            File payloadFile = new File(args[1]);
            if (!metadataPropertyFile.exists() || !metadataPropertyFile.canRead() || !payloadFile.exists()
                    || !payloadFile.canRead()) {
                printUsageAndExit();
            }
            File provenanceFile = null;
            if (args.length > 2) {
                provenanceFile = new File(args[2]);
                if (!provenanceFile.exists() || !provenanceFile.canRead()) {
                    printUsageAndExit();
                }
            }

            sc = new ODCSService(INPUT_WS_LOCATION);

            props.load(new FileInputStream(metadataPropertyFile));

            String uuidString = props.getProperty("uuid");
            UUID uuid = UUID.fromString(uuidString);
            if (!uuidString.equals(uuid.toString())) {
                throw new Exception("uuid format error");
            }

            Metadata metadata = new Metadata(uuid);
            metadata.setDataBaseUrl(new URI(props.getProperty("databaseurl")));
            metadata.setPipelineName(props.getProperty("pipelineName"));
            metadata.setUpdateTag(props.getProperty("updateTag"));

            addPropertiesToList("publishedby", props, metadata.getPublishers());
            addPropertiesToList("source", props, metadata.getSources());
            addPropertiesToList("license", props, metadata.getLicenses());

            if (provenanceFile != null) {
                String provenancePayload = readFileToString(provenanceFile);
                metadata.setProvenance(provenancePayload);
            }

            System.out.println("Sending data to ODCleanStore Input Websrvice at\n   " + INPUT_WS_LOCATION);
            sc.insert("scraper", "reparcs", metadata, payloadFile, "UTF-8");
            System.out.println("Data has been sent.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addPropertiesToList(String property, Properties props, List<URI> list) throws URISyntaxException {
        String value = props.getProperty(property);
        if (value != null) {
            list.add(new URI(value));
        }
        for (int i = 1; true; i++) {
            value = props.getProperty(property + Integer.toString(i));
            if (value != null) {
                list.add(new URI(value));
            } else {
                break;
            }
        }
    }

    private static void printUsageAndExit() {
        System.out.println("Usage: java -jar simplescraper.jar"
                + " <metadata property file> <RDF payload file> [<RDF provenance metadata file>]");
        System.exit(-1);
    }

    private static String readFileToString(File file) throws IOException {
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

        StringBuilder result = new StringBuilder((int) file.length());

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int n = 0;
        while (-1 != (n = inputStream.read(buffer))) {
            result.append(new String(buffer, 0, n, DEFAULT_ENCODING));
        }
        return result.toString();
    }
}
