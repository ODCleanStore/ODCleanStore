package cz.cuni.mff.odcleanstore.engine.outputws;

import org.restlet.representation.Representation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.queryexecution.QueryConstraintSpec;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecution;
import cz.cuni.mff.odcleanstore.queryexecution.QueryResult;

/**
 *  @author Petr Jerman
 */
public class UriQueryExecutorResource extends QueryExecutorResourceBase {

	protected Representation execute() {
		try {
			String uri = getFormValue("find");
			AggregationSpec aggregationSpec = getAggregationSpec();
			QueryExecution queryExecution = new QueryExecution(Engine.CLEAN_DATABASE_ENDPOINT);
			final QueryResult result = queryExecution.findURI(uri, new QueryConstraintSpec(), aggregationSpec);

			if (result == null)
				return return404();

			return getFormatter().format(result);
		} catch (Exception e) {
			return return404();
		}
	}
}
