package cz.cuni.mff.odcleanstore.queryexecution.impl;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictClusterFilter;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.CRUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ResolutionFunctionUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.XMLGregorianCalendarComparator;
import cz.cuni.mff.odcleanstore.core.ODCSUtils;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.XMLGregorianCalendar;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A {@link ConflictClusterFilter} for ODCS filtering data which has been obsoleted by a newer
 * version of the respective named graph.
 * @author Jan Michelfeit
 */
public class ODCSUpdateConflictClusterFilter implements ConflictClusterFilter {
    private static final Logger LOG = LoggerFactory.getLogger(ODCSUpdateConflictClusterFilter.class);
    private Model lastMetadata = null;
    private boolean lastHasPotentialObsoletedVersions = false;

    /**
     * Removes duplicate triples that are remaining from older versions of the
     * same named graph.
     * A triple from named graph A is removed iff
     * <ul>
     * <li>(1) it is identical to another triple from a different named graph B,</li>
     * <li>(2) named graph A has an older stored date than named graph B,</li>
     * <li>(3) named graphs A and B have the same update tag.</li>
     * <li>(4) named graphs A and B have the same data sources,</li>
     * <li>(5) named graphs A and B were inserted by the same user.</li>
     * </ul>
     *
     * Works in time O(n log n log m) where n is the size of conflict cluster, m the number of metadata quads.
     *
     * @param conflictCluster quads in the conflict cluster; the quads are sorted by object and graph name
     * @param crContext context object for the conflict resolution (containing resolution settings, additional metadata etc.)
     * @return filtered quads
     */
    @Override
    public List<Statement> filter(List<Statement> conflictCluster, CRContext crContext) {
        if (!hasPotentialObsoletedVersions(crContext.getMetadata())) {
            return conflictCluster;
        }
        // Sort quads by object, update tag, data sources and time (time in *reverse order*).
        // Since for every comparison we search metadata in logarithmic time,
        // sorting has time complexity O(n log n log m)
        Statement[] result = conflictCluster.toArray(new Statement[0]);
        Arrays.sort(result, new ObjectUpdateSourceStoredComparator(crContext));

        // Remove unwanted quads in one pass
        Model metadata = crContext.getMetadata();
        int lastInsertIdx = 0;
        for (int currIdx = 1; currIdx < result.length; currIdx++) {
            Statement currStatement = result[currIdx];
            Statement lastStatement = result[lastInsertIdx];
            //Value currObject = currStatement.getObject();
            Value lastObject = lastStatement.getObject();
            Resource currContext = currStatement.getContext();
            Resource lastContext = lastStatement.getContext();

            if (lastContext != null
                    // TODO: + update tag
                    && currContext != null
                    && !currContext.equals(lastContext)
                    && CRUtils.sameValues(currStatement.getObject(), lastObject) // (1) holds
                    && isBeforeAndNotNull(getInsertedAt(currContext, metadata), getInsertedAt(lastContext, metadata)) // (2) holds
                    && equalsAndNotNull(getInsertedBy(currContext, metadata), getInsertedBy(lastContext, metadata)) // (5) holds
                    && equalsAndNotNull(getSources(currContext, metadata), getSources(lastContext, metadata))) { // (4) holds
                LOG.trace("Filtered a triple from an outdated named graph {}.", currContext);
                continue;
            } else {
                lastInsertIdx++;
                result[lastInsertIdx] = currStatement;
            }
        }
        if (lastInsertIdx == result.length - 1) {
            return Arrays.asList(result);
        } else {
            return Arrays.asList(Arrays.copyOf(result, lastInsertIdx + 1));
        }
    }


    /**
     * Check whether metadata contain two named graphs where one may be an updated of the other.
     * This is a only a heuristic based on source metadata.
     * @param metadata metadata
     * @return false if metadata contain no graph which could be an update of another present graph
     */
    private boolean hasPotentialObsoletedVersions(Model metadata) {
        // In the current implementation of ConflictResolverImpl, metadata object is shared for
        // all conflict clusters in one run of conflict resolution. Therefore if the metadata given
        // are the same object as during the last call, we don't check for potential obsoleted versions again
        // but use value from the last call with these metadata.
        if (lastMetadata == metadata) { // intentionally reference comparison
            return lastHasPotentialObsoletedVersions;
        }
        lastMetadata = metadata;
        lastHasPotentialObsoletedVersions = hasPotentialObsoletedVersionsInternal(metadata);
        return lastHasPotentialObsoletedVersions;
    }

    /**
     * Implementation of {@link #hasPotentialObsoletedVersions(Model)}.
     * @param metadata metadata
     * @return false if metadata contain no graph which could be an update of another present graph
     */
    private boolean hasPotentialObsoletedVersionsInternal(Model metadata) {
        Set<String> updateTags = new HashSet<String>();
        Set<Integer> sourceHashesSet = new HashSet<Integer>();

        for (Resource subject : metadata.subjects()) {
            if (!metadata.contains(subject, ODCS.INSERTED_AT, null)
                    || !metadata.contains(subject, ODCS.INSERTED_BY, null)
                    || !metadata.contains(subject, ODCS.SOURCE, null)) {
                // If any of the tested properties is null, the named graph cannot be marked as an update
                continue;
            }

            Iterator<Statement> updateTagIt = metadata.filter(subject, ODCS.UPDATE_TAG, null).iterator();
            if (updateTagIt.hasNext()) {
                String updateTag = updateTagIt.next().getObject().stringValue();
                if (updateTags.contains(updateTag)) {
                    // Occurrence of named graphs sharing the same update tag
                    return true;
                } else {
                    updateTags.add(updateTag);
                }
            }

            Set<Value> sources = metadata.filter(subject, ODCS.SOURCE, null).objects();
            Integer sourcesHash = sources.hashCode();
            if (sourceHashesSet.contains(sourcesHash)) {
                // Occurrence of named graphs sharing a common data source (heuristic based on hashCode())
                return true;
            } else {
                sourceHashesSet.add(sourcesHash);
            }
        }
        return false;
    }

    private Set<Value> getSources(Resource context, Model metadata) {
        return metadata.filter(context, ODCS.SOURCE, null).objects();
    }

    private Value getInsertedBy(Resource context, Model metadata) {
        return ODCSUtils.getSingleObjectValue(context, ODCS.INSERTED_BY, metadata);
    }

    private Value getInsertedAt(Resource context, Model metadata) {
        return ODCSUtils.getSingleObjectValue(context, ODCS.INSERTED_AT, metadata);
    }

    private boolean equalsAndNotNull(Object firstValue, Object secondValue) {
        if (firstValue == null || secondValue == null) {
            return false;
        }
        return firstValue.equals(secondValue);
    }

    private boolean isBeforeAndNotNull(Value firstDate, Value secondDate) {
        if (firstDate == null || secondDate == null) {
            return false;
        }
        return compareAsDates(firstDate, secondDate) < 0;
    }

    private int compareAsDates(Value object1, Value object2) {
        XMLGregorianCalendar value1 = ResolutionFunctionUtils.convertToCalendarSilent(object1);
        XMLGregorianCalendar value2 = ResolutionFunctionUtils.convertToCalendarSilent(object2);
        return XMLGregorianCalendarComparator.getInstance().compare(value1, value2);
    }
}
