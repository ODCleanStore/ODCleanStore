/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.user;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.queryexecution.QueryConstraintSpec;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecution;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;

/**
 * @author jermanp
 * 
 */
public class UriQueryExecutorResource extends QueryExecutorResourceBase {

	protected Representation execute(Form form) {
		try {

			String uri = form.getFirst("find").getValue();

			QueryExecution queryExecution = new QueryExecution(Engine.CLEAN_DATABASE_ENDPOINT);
			final NamedGraphSet result = queryExecution.findURI(uri, new QueryConstraintSpec(), new AggregationSpec());

			if (result == null)
				return return404();

			WriterRepresentation representation = new WriterRepresentation(MediaType.APPLICATION_RDF_TRIG) {

				@Override
				public void write(Writer writer) throws IOException {
					// TODO: baseURI ?
					result.write(writer, "TRIG", "" /* baseURI */);
				};
			};

			representation.setCharacterSet(CharacterSet.UTF_8);
			return representation;

		} catch (Exception e) {
			return return404();
		}
	}
}
