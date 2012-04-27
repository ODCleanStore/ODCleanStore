package cz.cuni.mff.odcleanstore.engine.ws.user.output;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;

/**
 * Returns a representation of a query result serialized to the TriG format.
 * (See http://www4.wiwiss.fu-berlin.de/bizer/TriG/ .)
 * @author Jan Michelfeit
 */
public class TriGFormatter implements QueryResultFormatter {
	@Override
	public Representation format(final NamedGraphSet result) {
		WriterRepresentation representation = new WriterRepresentation(MediaType.APPLICATION_RDF_TRIG) {
			@Override
			public void write(Writer writer) throws IOException {
				// TODO: baseURI ?
				result.write(writer, "TRIG", "" /* baseURI */);
			};
		};
		representation.setCharacterSet(CharacterSet.UTF_8);
		return representation;
	}

}
