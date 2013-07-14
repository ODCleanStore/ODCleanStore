package cz.cuni.mff.odcleanstore.engine.outputws;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolutionPolicy;
import cz.cuni.mff.odcleanstore.queryexecution.BasicQueryResult;
import cz.cuni.mff.odcleanstore.queryexecution.QueryConstraintSpec;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecutionException;

import org.restlet.representation.Representation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ServerResource for URI query.
 * @author Petr Jerman
 */
public class UriQueryExecutorResource extends QueryExecutorResourceBase {

    private static final Logger LOG = LoggerFactory.getLogger(UriQueryExecutorResource.class);
    
    @Override
    protected Representation execute() throws QueryExecutionException, ResultEmptyException {
        String uri = getFormValue("uri");
        ConflictResolutionPolicy conflictResolutionPolicy = getConflictResolutionPolicy();
        final BasicQueryResult result = getQueryExecution().findURI(
                uri, new QueryConstraintSpec(), conflictResolutionPolicy);

        if (result == null) {
            LOG.error("Query result is empty");
            throw new ResultEmptyException("Result is empty");
        }

        return getFormatter(ConfigLoader.getConfig().getOutputWSGroup()).format(result, getRequestReference());
    }
}
