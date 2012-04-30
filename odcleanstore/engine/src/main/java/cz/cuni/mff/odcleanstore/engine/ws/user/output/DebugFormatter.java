package cz.cuni.mff.odcleanstore.engine.ws.user.output;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;

import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.queryexecution.QueryResult;

/**
 * Returns a representation of a query result serialized to the TriG format.
 * (See http://www4.wiwiss.fu-berlin.de/bizer/TriG/ .)
 * 
 * @author Jan Michelfeit
 */
public class DebugFormatter extends ResultFormatterBase {
	@Override
	public Representation format(final QueryResult result) {
		WriterRepresentation representation = new WriterRepresentation(
				MediaType.TEXT_PLAIN) {
			@Override
			public void write(Writer writer) throws IOException {
				writer.write("Query executed in ");
				writer.write(formatExecutionTime(result.getExecutionTime()));
				writer.write("\n\n===============================\n== Query results ==\n");
				for (CRQuad crQuad : result.getResultQuads()) {
					writer.write(crQuad.getQuad().toString());
					writer.write("\n\tQuality: ");
					writer.write(formatScore(crQuad.getQuality()));
					writer.write("; Sources: ");
					boolean first = true;
					for (String sourceURI : crQuad.getSourceNamedGraphURIs()) {
						if (!first) {
							writer.write(", ");
						}
						first = false;
						writer.write(sourceURI);
					}
					writer.write('\n');
				}

				writer.write("\n===============================\n== Metadata ==\n");
				for (NamedGraphMetadata metadata : result.getMetadata().listMetadata()) {
					writer.write(metadata.getNamedGraphURI());
					writer.write('\n');
					if (metadata.getDataSource() != null) {
						writer.write("\tSource: ");
						writer.write(metadata.getDataSource());
						writer.write('\n');
					}
					if (metadata.getStored() != null) {
						writer.write("\tInserted at: ");
						writer.write(formatDate(metadata.getStored()));
						writer.write('\n');
					}
					if (metadata.getScore() != null) {
						writer.write("\tGraph score: ");
						writer.write(metadata.getScore().toString());
						writer.write('\n');
					}
				}
			}
		};
		representation.setCharacterSet(CharacterSet.UTF_8);
		return representation;
	}

}
