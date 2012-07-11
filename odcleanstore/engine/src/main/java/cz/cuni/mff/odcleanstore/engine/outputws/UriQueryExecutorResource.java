package cz.cuni.mff.odcleanstore.engine.outputws;

import org.restlet.representation.Representation;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.queryexecution.BasicQueryResult;
import cz.cuni.mff.odcleanstore.queryexecution.QueryConstraintSpec;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecution;

/**
 *  @author Petr Jerman
 */
public class UriQueryExecutorResource extends QueryExecutorResourceBase {

	protected Representation execute() {
		try {
			String uri = getFormValue("uri");
			AggregationSpec aggregationSpec = getAggregationSpec();
			JDBCConnectionCredentials connectionCredentials = 
					ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials();
			QueryExecution queryExecution = new QueryExecution(connectionCredentials, ConfigLoader.getConfig());
			final BasicQueryResult result = queryExecution.findURI(uri, new QueryConstraintSpec(), aggregationSpec);

			if (result == null)
				return return404();

			return getFormatter(ConfigLoader.getConfig().getOutputWSGroup()).format(result, getRequestReference());
		} catch (Exception e) {
			return return404();
		}
	}
}
