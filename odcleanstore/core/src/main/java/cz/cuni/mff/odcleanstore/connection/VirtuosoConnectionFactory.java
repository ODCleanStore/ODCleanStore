/**
 * 
 */
package cz.cuni.mff.odcleanstore.connection;

import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.shared.ODCSUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Factory class for {@link VirtuosoConnectionWrapper}.
 * @author Jan Michelfeit
 */
public final class VirtuosoConnectionFactory {
    private static final Logger LOG = LoggerFactory.getLogger(VirtuosoConnectionWrapper.class);

    /**
     * Create a new connection and return its wrapper.
     * Should be used only for connection to a Virtuoso instance.
     * @param connectionCredentials connection settings for the SPARQL endpoint
     * @return wrapper of the newly created connection
     * @throws ConnectionException database connection error
     */
    public static VirtuosoConnectionWrapper createJDBCConnection(JDBCConnectionCredentials connectionCredentials)
            throws ConnectionException {

        try {
            Class.forName(ODCSUtils.JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new ConnectionException("Couldn't load Virtuoso jdbc driver", e);
        }
        try {
            LOG.debug("VirtuosoConnectionWrapper: creating connection");
            Connection connection = DriverManager.getConnection(
                    connectionCredentials.getConnectionString(),
                    connectionCredentials.getUsername(),
                    connectionCredentials.getPassword());
            VirtuosoConnectionWrapper wrapper = new JenaVirtuosoConnectionWrapper(connection);
            // disable log by default in order to prevent log size problems; transactions don't work much with SPARQL anyway
            wrapper.adjustTransactionLevel(EnumLogLevel.AUTOCOMMIT);
            return wrapper;
        } catch (SQLException e) {
            throw new ConnectionException(e);
        }
    }
    
    private VirtuosoConnectionFactory() {
        // hide constructor
    }
}
