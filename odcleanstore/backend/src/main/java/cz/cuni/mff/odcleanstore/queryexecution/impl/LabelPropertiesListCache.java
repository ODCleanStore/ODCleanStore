package cz.cuni.mff.odcleanstore.queryexecution.impl;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.queryexecution.EnumQueryError;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecutionException;
import cz.cuni.mff.odcleanstore.shared.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides access to the list of label properties formatted as a string for use in a query, with caching.
 * This class is thread-safe.
 * @author Jan Michelfeit
 */
public class LabelPropertiesListCache extends CacheHolderBase<String> {
    private static final Logger LOG = LoggerFactory.getLogger(LabelPropertiesListCache.class);

    /** Lifetime of the cached value in milliseconds. */
    private static final long CACHE_LIFETIME = 5 * Utils.TIME_UNIT_60 * Utils.MILLISECONDS;

    /** Database connection settings. */
    private final JDBCConnectionCredentials connectionCredentials;

    /** Prefix mappings. */
    private final PrefixMappingCache prefixMappingCache;

    /**
     * Create a new instance.
     * @param connectionCredentials connection settings
     * @param prefixMappingCache cached prefix mapping
     */
    public LabelPropertiesListCache(
            JDBCConnectionCredentials connectionCredentials, PrefixMappingCache prefixMappingCache) {

        super(CACHE_LIFETIME);
        this.connectionCredentials = connectionCredentials;
        this.prefixMappingCache = prefixMappingCache;
    }

    @Override
    protected String loadCachedValue() throws QueryExecutionException {
        List<String> labelProperties = new ArrayList<String>();

        VirtuosoConnectionWrapper connection = null;
        WrappedResultSet resultSet = null;
        try {
            connection = VirtuosoConnectionWrapper.createConnection(connectionCredentials);
            resultSet = connection.executeSelect("SELECT property FROM DB.ODCLEANSTORE.QE_LABEL_PROPERTIES");
            PrefixMapping prefixMapping = prefixMappingCache.getCachedValue();
            while (resultSet.next()) {
                String property = resultSet.getNString(1);
                if (Utils.isPrefixedName(property)) {
                    labelProperties.add(prefixMapping.expandPrefix(property));
                } else {
                    labelProperties.add(property);
                }
            }

            LOG.info("Loaded {} label properties.", labelProperties.size());
            if (labelProperties.isEmpty()) {
                throw new QueryExecutionException(EnumQueryError.QUERY_EXECUTION_SETTINGS_INVALID,
                        "There must be at least one label property defined");
            }

            StringBuilder sb = new StringBuilder();
            for (String property : labelProperties) {
                sb.append('<');
                sb.append(property);
                sb.append(">, ");
            }
            return sb.substring(0, sb.length() - 2);

        } catch (DatabaseException e) {
            throw new QueryExecutionException(EnumQueryError.DATABASE_ERROR, e);
        } catch (SQLException e) {
            throw new QueryExecutionException(EnumQueryError.DATABASE_ERROR, e);
        } finally {
            if (resultSet != null) {
                resultSet.closeQuietly();
            }
            if (connection != null) {
                connection.closeQuietly();
            }
        }
    }
}