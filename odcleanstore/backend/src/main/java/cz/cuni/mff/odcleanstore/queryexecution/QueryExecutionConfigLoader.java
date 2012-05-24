package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationType;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.data.ConnectionCredentials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * DAO class that loads default aggregation settings for use in {@link QueryExecution}.
 * @author Jan Michelfeit
 *
 */
/*package*/class QueryExecutionConfigLoader {
    private static final Logger LOG = LoggerFactory.getLogger(QueryExecutionConfigLoader.class);

    private static final String DEFAULT_AGGREGATION_KEY = "DEFAULT_AGGREGATION";
    private static final String DEFAULT_MULTIVALUE_KEY = "DEFAULT_MULTIVALUE";
    private static final String ERROR_STRATEGY_KEY = "ERROR_STRATEGY";

    private static final String DEFAULT_VALUE = "DEFAULT";
    private static final String MULTIVALUE_TRUE = "YES";
    private static final String MULTIVALUE_FALSE = "NO";

    /** Database connection settings. */
    private final ConnectionCredentials sparqlEndpoint;

    /**
     * Creates a new instance.
     * @param sparqlEndpoint database connection settings
     */
    public QueryExecutionConfigLoader(ConnectionCredentials sparqlEndpoint) {
        this.sparqlEndpoint = sparqlEndpoint;
    }

    /**
     * Retrieves the default aggregation settings from the database and returns them as an instance
     * of {@link AggregationSpec}.
     * @return default aggregation settings
     * @throws DatabaseException database error
     * @throws QueryExecutionException invalid settings in the database
     */
    public AggregationSpec getDefaultSettings() throws DatabaseException, QueryExecutionException {
        AggregationSpec defaultSettings = new AggregationSpec();

        VirtuosoConnectionWrapper connection = null;
        WrappedResultSet resultSet = null;
        try {
            connection = VirtuosoConnectionWrapper.createConnection(sparqlEndpoint);

            // Get global settings
            resultSet = connection.executeSelect("SELECT name, value FROM DB.FRONTEND.CR_SETTINGS");
            while (resultSet.next()) {
                String key = resultSet.getString("name");
                String value = resultSet.getString("value");
                if (DEFAULT_AGGREGATION_KEY.equals(key) && value != null) {
                    defaultSettings.setDefaultAggregation(parseAggregationType(value));
                } else if (DEFAULT_MULTIVALUE_KEY.equals(key) && value != null) {
                    defaultSettings.setDefaultMultivalue(parseMultivalue(value));
                } else if (ERROR_STRATEGY_KEY.equals(key) && value != null) {
                    defaultSettings.setErrorStrategy(parseErrorStrategy(value));
                }
            }
            resultSet.closeQuietly();
            resultSet = null;

            // Get property-level settings
            resultSet = connection.executeSelect("SELECT p.property, mt.label as multivalue, at.label AS aggregation"
                    + "\n FROM DB.FRONTEND.CR_PROPERTIES AS p"
                    + "\n JOIN DB.FRONTEND.CR_AGGREGATION_TYPES AS at ON (p.aggregationTypeId = at.id)"
                    + "\n JOIN DB.FRONTEND.CR_MULTIVALUE_TYPES AS mt ON (p.multivalueTypeId = mt.id)");

            while (resultSet.next()) {
                String property = resultSet.getString("property");
                EnumAggregationType aggregationValue = parseAggregationType(resultSet.getString("aggregation"));
                if (aggregationValue != null) {
                    defaultSettings.getPropertyAggregations().put(property, aggregationValue);
                }
                Boolean multivalueValue = parseMultivalue(resultSet.getString("multivalue"));
                if (multivalueValue != null) {
                    defaultSettings.getPropertyMultivalue().put(property, multivalueValue);
                }
            }

            return defaultSettings;
        } catch (SQLException e) {
            throw new QueryException(e);
        } finally {
            if (resultSet != null) {
                resultSet.closeQuietly();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * Parse the given value to an EnumAggregationType.
     * @param value aggregation method name
     * @return aggregation type; null means propagate default value
     * @throws QueryExecutionException  the given value does not represent an aggregation type
     */
    private EnumAggregationType parseAggregationType(String value) throws QueryExecutionException {
        if (DEFAULT_VALUE.equals(value)) {
            return null;
        }
        try {
            return EnumAggregationType.valueOf(value);
        } catch (IllegalArgumentException e) {
            LOG.error("Invalid value {} in database for aggregation method.", value);
            throw new QueryExecutionException(EnumQueryError.DEFAULT_AGGREGATION_SETTINGS_INVALID,
                    "Invalid value of aggregation type '" + value + "' in the database", e);
        }
    }

    /**
     * Parse the given value to an EnumAggregationErrorStrategy.
     * @param value error strategy as a string
     * @return error strategy
     * @throws QueryExecutionException the given value does not represent an error strategy
     */
    private EnumAggregationErrorStrategy parseErrorStrategy(String value) throws QueryExecutionException {
        try {
            return EnumAggregationErrorStrategy.valueOf(value);
        } catch (IllegalArgumentException e) {
            LOG.error("Invalid value {} in database for aggregation error strategy.", value);
            throw new QueryExecutionException(EnumQueryError.DEFAULT_AGGREGATION_SETTINGS_INVALID,
                    "Invalid value for aggregation error strategy '" + value + "' in the database", e);
        }
    }

    /**
     * Parse the given value to a boolean.
     * @param value value of a multivalue setting from the database
     * @return true iff represents true in the database; null means propagate default value
     * @throws QueryExecutionException the given value is not valid for multivalue
     */
    private Boolean parseMultivalue(String value) throws QueryExecutionException {
        if (MULTIVALUE_TRUE.equals(value)) {
            return true;
        } else if (MULTIVALUE_FALSE.equals(value)) {
            return false;
        } else if (DEFAULT_VALUE.equals(value)) {
            return null;
        } else {
            throw new QueryExecutionException(EnumQueryError.DEFAULT_AGGREGATION_SETTINGS_INVALID,
                    "Invalid value for multivalue '" + value + "' in the database");
        }
    }
}
