package cz.cuni.mff.odcleanstore.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import de.fuberlin.wiwiss.ng4j.impl.GraphReaderService;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * Utility used to transform input streams in RDF/XML to TRiG.
 * @author Jakub Daniel
 */
public class MultipleFormatLoader {
    /**
     * Read RDF/XML or TriG in the given input stream to NamedGraphSet.
     * @param input input stream with RDF/XML or TriG
     * @param defaultGraphName base URI for RDF data
     * @return RDF data as NamedGraphSet
     * @throws IOException exception
     */
    public NamedGraphSetImpl load(InputStream input, String defaultGraphName) throws IOException {
        NamedGraphSetImpl namedGraphSet = new NamedGraphSetImpl();

        GraphReaderService reader = new GraphReaderService();

        /**
         * Buffer the file so that it can be parsed multiple times
         */
        int totalLength = 0;
        int totalAllocated = 1;
        int readLength;

        byte[] buffer = new byte[totalAllocated];

        while ((readLength = input.read(buffer, totalLength, totalAllocated - totalLength)) >= 0) {
            totalLength += readLength;

            if (readLength + totalLength == totalAllocated) {
                byte[] newBuffer = new byte[totalAllocated * 2];

                for (int i = 0; i < totalLength; ++i) {
                    newBuffer[i] = buffer[i];
                }

                buffer = newBuffer;

                totalAllocated *= 2;
            }
        }

        ByteArrayInputStream buffered = new ByteArrayInputStream(buffer, 0, totalLength);

        reader.setSourceInputStream(buffered, defaultGraphName);

        try {
            reader.setLanguage("TRIG");
            reader.readInto(namedGraphSet);
        } catch (Exception e) {
            buffered.reset();

            reader.setLanguage("RDF/XML");
            reader.readInto(namedGraphSet);
        }

        return namedGraphSet;
    }
}
