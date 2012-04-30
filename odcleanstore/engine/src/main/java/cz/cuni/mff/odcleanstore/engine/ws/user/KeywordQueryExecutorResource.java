/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.user;

import org.restlet.data.Form;
import org.restlet.representation.Representation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.queryexecution.QueryConstraintSpec;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecution;
import cz.cuni.mff.odcleanstore.queryexecution.QueryResult;

/**
 * @author jermanp
 * 
 */
public class KeywordQueryExecutorResource extends QueryExecutorResourceBase {

	@Override
	protected Representation execute(Form form) {
		try {

			String keyword = form.getFirst("find").getValue();

			QueryExecution queryExecution = new QueryExecution(Engine.CLEAN_DATABASE_ENDPOINT);
			final QueryResult result = queryExecution.findKeyword(keyword, new QueryConstraintSpec(),
					new AggregationSpec());

			if (result == null)
				return return404();

			return getFormatter().format(result);
		} catch (Exception e) {
			return return404();
		}
	}
}