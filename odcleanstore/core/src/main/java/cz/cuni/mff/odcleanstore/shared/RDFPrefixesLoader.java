package cz.cuni.mff.odcleanstore.shared;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.data.RDFprefix;

/**
 * Common class for loading RDF prefixes from Virtuoso DB.
 *
 * @author tomas.soukup
 */
public class RDFPrefixesLoader {
	private static final Logger LOG = LoggerFactory.getLogger(RDFPrefixesLoader.class);
	
	/**
	 * Loads RDF prefixes from Virtuoso DB.
	 *
	 * @param connectionCredentials endpoint to Virtuoso DB
	 * @return list of loaded prefixes
	 * @throws DatabaseException database error occurred
	 */
	public static List<RDFprefix> loadPrefixes(JDBCConnectionCredentials connectionCredentials) throws DatabaseException {
		LOG.info("Loading RDF prefixes from: {}", connectionCredentials.getConnectionString());
		VirtuosoConnectionWrapper connection = null;
		List<RDFprefix> prefixList = new ArrayList<RDFprefix>();
		try {
			connection = VirtuosoConnectionWrapper.createConnection(connectionCredentials);
			WrappedResultSet resultSet = connection.executeSelect("select * from DB.DBA.SYS_XML_PERSISTENT_NS_DECL");
			while (resultSet.next()) {
				RDFprefix prefix = new RDFprefix(resultSet.getString("NS_PREFIX"), resultSet.getString("NS_URL"));
				prefixList.add(prefix);
			}
			resultSet.closeQuietly();
        } catch (SQLException e) {
            throw new QueryException(e);
        } finally {
			connection.closeQuietly();
		}
		LOG.info("Loaded {} prefixes.", prefixList.size());
		return prefixList;
	}
}
