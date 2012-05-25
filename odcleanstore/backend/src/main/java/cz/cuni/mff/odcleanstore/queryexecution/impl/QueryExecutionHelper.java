package cz.cuni.mff.odcleanstore.queryexecution.impl;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationType;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecutionException;
import cz.cuni.mff.odcleanstore.shared.Utils;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Helper class for Query Execution.
 * @author Jan Michelfeit
 */
public final class QueryExecutionHelper {
    /**
     * Expands prefixed names in the given aggregation settings to full URIs.
     * @param aggregationSpec aggregation settings where property names are expanded
     * @param prefixMapping prefix mapping used for expansion
     * @return new aggregation settings
     * @throws QueryExecutionException database error
     */
    public static AggregationSpec expandPropertyNames(AggregationSpec aggregationSpec, PrefixMapping prefixMapping)
            throws QueryExecutionException {

        if (aggregationSpec.getPropertyAggregations().isEmpty() && aggregationSpec.getPropertyMultivalue().isEmpty()) {
            return aggregationSpec;
        }
        AggregationSpec result = aggregationSpec.shallowClone();

        Map<String, EnumAggregationType> newPropertyAggregations = new TreeMap<String, EnumAggregationType>();
        for (Entry<String, EnumAggregationType> entry : aggregationSpec.getPropertyAggregations().entrySet()) {
            String property = entry.getKey();
            if (Utils.isPrefixedName(property)) {
                newPropertyAggregations.put(prefixMapping.expandPrefix(property), entry.getValue());
            } else {
                newPropertyAggregations.put(property, entry.getValue());
            }
        }
        result.setPropertyAggregations(newPropertyAggregations);

        Map<String, Boolean> newPropertyMultivalue = new TreeMap<String, Boolean>();
        for (Entry<String, Boolean> entry : aggregationSpec.getPropertyMultivalue().entrySet()) {
            String property = entry.getKey();
            if (Utils.isPrefixedName(property)) {
                newPropertyMultivalue.put(prefixMapping.expandPrefix(property), entry.getValue());
            } else {
                newPropertyMultivalue.put(property, entry.getValue());
            }
        }
        result.setPropertyMultivalue(newPropertyMultivalue);

        return result;
    }

    /** Hide constructor for a utility class. */
    private QueryExecutionHelper() {
    }
}
