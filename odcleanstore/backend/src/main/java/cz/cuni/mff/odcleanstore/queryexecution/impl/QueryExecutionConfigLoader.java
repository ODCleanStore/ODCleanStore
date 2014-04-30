package cz.cuni.mff.odcleanstore.queryexecution.impl;

import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolutionPolicy;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumCardinality;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolutionStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.ConflictResolutionPolicyImpl;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.ResolutionStrategyImpl;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionFactory;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.core.ODCSUtils;
import cz.cuni.mff.odcleanstore.queryexecution.EnumQueryError;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecutionException;
import cz.cuni.mff.odcleanstore.shared.ODCSErrorCodes;

import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * DAO class that loads default aggregation settings for use in
 * {@link cz.cuni.mff.odcleanstore.queryexecution.QueryExecution}.
 * @author Jan Michelfeit
 *
 */
/*package*/class QueryExecutionConfigLoader {
    private static final Logger LOG = LoggerFactory.getLogger(QueryExecutionConfigLoader.class);

    private static final String DEFAULT_VALUE = "DEFAULT";
    private static final String MANYVALUED_TRUE = "YES";
    private static final String MANYVALUED_FALSE = "NO";

    /** Database connection settings. */
    private final JDBCConnectionCredentials connectionCredentials;

    /**
     * Creates a new instance.
     * @param connectionCredentials database connection settings
     */
    public QueryExecutionConfigLoader(JDBCConnectionCredentials connectionCredentials) {
        this.connectionCredentials = connectionCredentials;
    }

    /**
     * Retrieves the default aggregation settings from the database and returns them as an instance
     * of {@link AggregationSpec}.
     * @param prefixMapping namespace prefix mappings
     * @return default aggregation settings
     * @throws DatabaseException database error
     * @throws QueryExecutionException invalid settings in the database
     */
    public ConflictResolutionPolicy getDefaultSettings(PrefixMapping prefixMapping)
            throws DatabaseException, QueryExecutionException {
        VirtuosoConnectionWrapper connection = null;
        WrappedResultSet resultSet = null;
        try {
            connection = VirtuosoConnectionFactory.createJDBCConnection(connectionCredentials);

            // Get global settings
            ResolutionStrategy defaultStrategy;
            resultSet = connection.executeSelect(
                    "SELECT es.label AS errorStrategy, mt.label as manyvalued, at.label AS aggregation"
                    + "\n FROM DB.ODCLEANSTORE.CR_SETTINGS AS s"
                    + "\n JOIN DB.ODCLEANSTORE.CR_ERROR_STRATEGIES AS es ON (s.defaultErrorStrategyId = es.id)"
                    + "\n JOIN DB.ODCLEANSTORE.CR_AGGREGATION_TYPES AS at ON (s.defaultAggregationTypeId = at.id)"
                    + "\n JOIN DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES AS mt ON (s.defaultMultivalueTypeId = mt.id)");
            if (resultSet.next()) {
                defaultStrategy = new ResolutionStrategyImpl(
                        parseAggregationType(resultSet.getString("aggregation")),
                        parseCardinality(resultSet.getString("manyvalued")),
                        parseErrorStrategy(resultSet.getString("errorStrategy")));
            } else {
                throw new QueryExecutionException(
                        EnumQueryError.DEFAULT_AGGREGATION_SETTINGS_INVALID,
                        ODCSErrorCodes.QE_DEFAULT_CONFIG_ERR,
                        "No default aggregation settings in the database");
            }
            resultSet.closeQuietly();
            resultSet = null;

            // Get property-level settings
            Map<URI, ResolutionStrategy> propertyStrategies = new HashMap<URI, ResolutionStrategy>();
            resultSet = connection.executeSelect("SELECT p.property, mt.label as manyvalued, at.label AS aggregation"
                    + "\n FROM DB.ODCLEANSTORE.CR_PROPERTIES AS p"
                    + "\n JOIN DB.ODCLEANSTORE.CR_AGGREGATION_TYPES AS at ON (p.aggregationTypeId = at.id)"
                    + "\n JOIN DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES AS mt ON (p.multivalueTypeId = mt.id)");

            while (resultSet.next()) {
                String property = resultSet.getString("property");
                ResolutionStrategyImpl strategy = new ResolutionStrategyImpl();
                String resolutionFunctionName = parseAggregationType(resultSet.getString("aggregation"));
                if (!ODCSUtils.isNullOrEmpty(resolutionFunctionName)) {
                    strategy.setResolutionFunctionName(resolutionFunctionName);
                }
                EnumCardinality cardinality = parseCardinality(resultSet.getString("manyvalued"));
                if (cardinality != null) {
                    strategy.setCardinality(cardinality);
                }

                String expandedProperty = prefixMapping.expandPrefix(property);
                propertyStrategies.put(ValueFactoryImpl.getInstance().createURI(expandedProperty), strategy);
            }

            return new ConflictResolutionPolicyImpl(defaultStrategy, propertyStrategies);
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
     * @throws QueryExecutionException the given value does not represent an aggregation type
     */
    private String parseAggregationType(String value) throws QueryExecutionException {
        if (DEFAULT_VALUE.equals(value)) {
            return null;
        }
        return value;
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
                    ODCSErrorCodes.QE_DEFAULT_CONFIG_ES_ERR,
                    "Invalid value for aggregation error strategy '" + value + "' in the database",
                    e);
        }
    }

    /**
     * Parse the given value to a boolean.
     * @param value value of a manyvalued setting from the database
     * @return true iff represents true in the database; null means propagate default value
     * @throws QueryExecutionException the given value is not valid for manyvalued
     */
    private EnumCardinality parseCardinality(String value) throws QueryExecutionException {
        if (MANYVALUED_TRUE.equals(value)) {
            return EnumCardinality.MANYVALUED;
        } else if (MANYVALUED_FALSE.equals(value)) {
            return EnumCardinality.SINGLEVALUED;
        } else if (DEFAULT_VALUE.equals(value)) {
            return null;
        } else {
            throw new QueryExecutionException(EnumQueryError.DEFAULT_AGGREGATION_SETTINGS_INVALID,
                    ODCSErrorCodes.QE_DEFAULT_CONFIG_MANYVALUED_ERR,
                    "Invalid value for manyvalued '" + value + "' in the database");
        }
    }
}
