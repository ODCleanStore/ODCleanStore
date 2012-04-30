/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.user;

import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import cz.cuni.mff.odcleanstore.engine.ws.user.output.HTMLFormatter;
import cz.cuni.mff.odcleanstore.engine.ws.user.output.QueryResultFormatter;

/**
 * @author jermanp
 * 
 */
public abstract class QueryExecutorResourceBase extends ServerResource {

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

	protected abstract Representation execute(Form form);
	
	/**
	 * Returns an appropriate formatter of the result.
	 * @return formatter of query result
	 * TODO: choose a formatter according to user preferences (URL query variable, Accept request header)
	 */
	protected QueryResultFormatter getFormatter() {
		//return new TriGFormatter(); 
		return new HTMLFormatter();
	}

	protected Representation return404() {
		setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		return new StringRepresentation("NENY TEDKA", MediaType.TEXT_PLAIN, Language.ALL, CharacterSet.UTF_8);
	}
}