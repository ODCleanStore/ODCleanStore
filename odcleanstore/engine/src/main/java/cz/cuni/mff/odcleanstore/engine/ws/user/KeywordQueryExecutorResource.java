/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.user;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.WriterRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.queryexecution.QueryConstraintSpec;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecution;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;

/**
 * @author jermanp
 * 
 */
public class KeywordQueryExecutorResource extends ServerResource {

	@Get
	public Representation executeGet() {
		Form form = this.getQuery();
		return execute(form);
	}

	@Post
	public Representation executePost(Representation entity) {
		Form form = new Form(entity);
		return execute(form);
	}

	private Representation execute(Form form) {
		try {

			String keyword = form.getFirst("keyword").getValue();

			final NamedGraphSet result = QueryExecution.findKeyword(keyword, new QueryConstraintSpec(),
					new AggregationSpec());

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

	private Representation return404() {
		setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		return new StringRepresentation("NENY TEDKA", MediaType.TEXT_PLAIN, Language.ALL, CharacterSet.UTF_8);
	}
}