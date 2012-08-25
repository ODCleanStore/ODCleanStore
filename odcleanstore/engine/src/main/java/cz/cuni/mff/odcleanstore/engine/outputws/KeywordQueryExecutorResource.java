package cz.cuni.mff.odcleanstore.engine.outputws;

import org.restlet.representation.Representation;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.queryexecution.BasicQueryResult;
import cz.cuni.mff.odcleanstore.queryexecution.QueryConstraintSpec;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecution;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecutionException;

/**
 * @author Petr Jerman
 */
public class KeywordQueryExecutorResource extends QueryExecutorResourceBase {

    @Override
    protected Representation execute() throws QueryExecutionException, ResultEmptyException {
        String keyword = getFormValue("kw");
        AggregationSpec aggregationSpec = getAggregationSpec();
        JDBCConnectionCredentials connectionCredentials =
                ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials();
        QueryExecution queryExecution = new QueryExecution(connectionCredentials, ConfigLoader.getConfig());
        final BasicQueryResult result = queryExecution.findKeyword(
                keyword, new QueryConstraintSpec(), aggregationSpec);

        if (result == null) {
            throw new ResultEmptyException("Result is empty");
        }

        return getFormatter(ConfigLoader.getConfig().getOutputWSGroup()).format(result, getRequestReference());
    }
}
