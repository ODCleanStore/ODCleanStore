package cz.cuni.mff.odcleanstore.data;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fuberlin.wiwiss.ng4j.impl.GraphReaderService;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * Utility used to transform input streams in RDF/XML to TRiG.
 * @author Jakub Daniel
 */
public class MultipleFormatLoader {
	private static final Logger LOG = LoggerFactory.getLogger(MultipleFormatLoader.class);

    /**
     * Read RDF/XML or TriG in the given input stream to NamedGraphSet.
     * @param source input string with RDF/XML or TriG
     * @param defaultGraphName base URI for RDF data
     * @return RDF data as NamedGraphSet
     * @throws IOException exception
     */
    public NamedGraphSetImpl load(String source, String defaultGraphName) throws IOException {
        NamedGraphSetImpl namedGraphSet = new NamedGraphSetImpl();
        
        String[] formats = {"RDF/XML", "TTL", "N3", "TRIG"};
        
        for (String format : formats) {
        	if (attemptToReadInto(source, defaultGraphName, namedGraphSet, format)) {
        		return namedGraphSet;
        	}
        }

        throw new IOException("Could not interpret input source.");
    }
    
    private boolean attemptToReadInto(String source, String defaultGraphName, NamedGraphSetImpl namedGraphSet, String format) {
        try {
        	GraphReaderService reader = new GraphReaderService();
        	reader.setSourceString(source, defaultGraphName);
            reader.setLanguage(format);
            reader.readInto(namedGraphSet);
            
            return true;
        } catch (Exception e) {
        	LOG.warn("Attempt to interpret " + format + " failed: " + e.getMessage());
        	return false;
        }
    }
}
