package cz.cuni.mff.odcleanstore.engine.db.model;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.engine.db.DbContext;

import java.util.Locale;

/**
 * Class for executing SPARQL queries over dirty database without transactions.
 * 
 * @see SQL
 * @author Petr Jerman
 */
public class DbOdcsContextSparql extends DbContext {

    private static final String ERROR_CREATE_ODCS_CONTEXT = "Error during creating "
            + DbOdcsContextSparql.class.getSimpleName();

    public DbOdcsContextSparql() throws DbOdcsException {
        try {
            setConnection(ConfigLoader.getConfig().getBackendGroup().getDirtyDBJDBCConnectionCredentials());
            execute(SQL.USE_ODCS_SCHEMA);
        } catch (Exception e) {
            throw new DbOdcsException(ERROR_CREATE_ODCS_CONTEXT, e);
        }
    }

    public void insertAttachedGraphLink(String metadataGraphNameh, String dataGraphName, String attachedGraphName)
            throws DbOdcsException {
        try {
            String query = String.format(Locale.ROOT,
                    SQL.INSERT_ATTACHED_GRAPH_LINK,
                    metadataGraphNameh,
                    dataGraphName,
                    attachedGraphName);
            execute(query);
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_INSERT_ATTACHED_GRAPH_LINK, e);
        }
    }
}
