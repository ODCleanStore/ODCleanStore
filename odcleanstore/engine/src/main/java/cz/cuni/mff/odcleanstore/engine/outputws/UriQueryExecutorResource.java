package cz.cuni.mff.odcleanstore.engine.outputws;

import org.restlet.representation.Representation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.queryexecution.BasicQueryResult;
import cz.cuni.mff.odcleanstore.queryexecution.QueryConstraintSpec;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecution;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecutionException;

/**
 * ServerResource for URI query.
 * @author Petr Jerman
 */
public class UriQueryExecutorResource extends QueryExecutorResourceBase {

    private static final Logger LOG = LoggerFactory.getLogger(UriQueryExecutorResource.class);
    
    @Override
    protected Representation execute() throws QueryExecutionException, ResultEmptyException {
        String uri = getFormValue("uri");
        AggregationSpec aggregationSpec = getAggregationSpec();
        JDBCConnectionCredentials connectionCredentials =
                ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials();
        QueryExecution queryExecution = new QueryExecution(connectionCredentials, ConfigLoader.getConfig());
        final BasicQueryResult result = queryExecution.findURI(uri, new QueryConstraintSpec(), aggregationSpec);

        if (result == null) {
            LOG.error("Query result is empty");
            throw new ResultEmptyException("Result is empty");
        }

        return getFormatter(ConfigLoader.getConfig().getOutputWSGroup()).format(result, getRequestReference());
    }
}
