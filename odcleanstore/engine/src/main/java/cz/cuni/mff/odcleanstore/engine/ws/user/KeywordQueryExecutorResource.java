/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.user;

import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.data.QuadCollection;
import cz.cuni.mff.odcleanstore.queryexecution.KeywordQueryExecutor;
import cz.cuni.mff.odcleanstore.queryexecution.QueryConstraintSpec;
import de.fuberlin.wiwiss.ng4j.Quad;

/**
 * @author jermanp
 * 
 */
public class KeywordQueryExecutorResource extends ServerResource {

	@Get
	public StringRepresentation executeGet() {
		Form form = this.getQuery();
		return execute(form);
	}

	@Post
	public StringRepresentation executePost(Representation entity) {
		Form form = new Form(entity);
		return execute(form);
	}

	private StringRepresentation execute(Form form) {
		try {
			String keyword = form.getFirst("keyword").getValue();

			KeywordQueryExecutor kqe = new KeywordQueryExecutor(null);
			QuadCollection qc = kqe.findKeyword(keyword, new QueryConstraintSpec(), new AggregationSpec());

			StringBuilder sb = new StringBuilder();
			for (Quad q : qc) {
				sb.append(q.toString());
				sb.append(".\n");
			}

			return new StringRepresentation(sb.toString(), MediaType.APPLICATION_RDF_TURTLE, Language.ALL,
					CharacterSet.UTF_8);
		} catch (Exception e) {
			return new StringRepresentation("NENY TEDKA", MediaType.TEXT_PLAIN, Language.ALL, CharacterSet.UTF_8);
		}
	}
}