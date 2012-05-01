/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.user;

import org.restlet.data.Form;
import org.restlet.representation.Representation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationType;
import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.queryexecution.QueryConstraintSpec;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecution;
import cz.cuni.mff.odcleanstore.queryexecution.QueryResult;

/**
 * @author jermanp
 * 
 */
public class UriQueryExecutorResource extends QueryExecutorResourceBase {

	protected Representation execute(Form form) {
		try {
			AggregationSpec as = new AggregationSpec();

			String uri = form.getFirst("find").getValue();

			String at = form.getFirst("at").getValue();
			EnumAggregationType eat = EnumAggregationType.valueOf(at);
			as.setDefaultAggregation(eat);

			QueryExecution queryExecution = new QueryExecution(
					Engine.CLEAN_DATABASE_ENDPOINT);
			final QueryResult result = queryExecution.findURI(uri,
					new QueryConstraintSpec(), as);

			if (result == null)
				return return404();

			return getFormatter().format(result);
		} catch (Exception e) {
			return return404();
		}
	}
}
