package cz.cuni.mff.odcleanstore.data;

import java.io.IOException;

import de.fuberlin.wiwiss.ng4j.impl.GraphReaderService;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * Utility used to transform input streams in RDF/XML to TRiG.
 * @author Jakub Daniel
 */
public class MultipleFormatLoader {
    /**
     * Read RDF/XML or TriG in the given input stream to NamedGraphSet.
     * @param input input string with RDF/XML or TriG
     * @param defaultGraphName base URI for RDF data
     * @return RDF data as NamedGraphSet
     * @throws IOException exception
     */
    public NamedGraphSetImpl load(String input, String defaultGraphName) throws IOException {
        NamedGraphSetImpl namedGraphSet = new NamedGraphSetImpl();

        GraphReaderService reader = new GraphReaderService();

        reader.setSourceString(input, defaultGraphName);

        try {
            reader.setLanguage("TRIG");
            reader.readInto(namedGraphSet);
        } catch (Exception e) {
            reader.setLanguage("RDF/XML");
            reader.readInto(namedGraphSet);
        }

        return namedGraphSet;
    }
}
