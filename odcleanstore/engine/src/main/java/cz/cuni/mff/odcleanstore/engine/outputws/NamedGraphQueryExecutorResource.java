package cz.cuni.mff.odcleanstore.engine.outputws;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.queryexecution.BasicQueryResult;
import cz.cuni.mff.odcleanstore.queryexecution.QueryConstraintSpec;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecution;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecutionException;

import org.restlet.representation.Representation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ServerResource for named graph query.
 * @author Jan Michelfeit
 */
public class NamedGraphQueryExecutorResource extends QueryExecutorResourceBase {
    
    private static final Logger LOG = LoggerFactory.getLogger(NamedGraphQueryExecutorResource.class);

    @Override
    protected Representation execute() throws QueryExecutionException, ResultEmptyException {
        String namedGraphURI = getFormValue("uri");
        AggregationSpec aggregationSpec = getAggregationSpec();
        JDBCConnectionCredentials connectionCredentials =
                ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials();
        QueryExecution queryExecution = new QueryExecution(connectionCredentials, ConfigLoader.getConfig());
        BasicQueryResult result = queryExecution.findNamedGraph(namedGraphURI, new QueryConstraintSpec(), aggregationSpec);

        if (result == null) {
            LOG.error("Query result is empty");
            throw new ResultEmptyException("Result is empty");
        }

        return getFormatter(ConfigLoader.getConfig().getOutputWSGroup()).format(result, getRequestReference());
    }
}
