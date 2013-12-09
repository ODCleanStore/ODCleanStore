package cz.cuni.mff.odcleanstore.queryexecution.impl;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.CRUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.ValueComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.BestSelectedLiteralComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.LiteralComparatorFactory;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.EnumLiteralType;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ResolutionFunctionUtils;
import cz.cuni.mff.odcleanstore.core.ODCSUtils;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Comparator of {@link Statement quads} comparing first by object, second
 * by update tag, third by data sources in metadata, fourth by descending stored date in metadata.
 * @author Jan Michelfeit
 */
/*package*/class ObjectUpdateSourceStoredComparator implements Comparator<Statement> {
    private static final Comparator<Value> VALUE_COMPARATOR = new ValueComparator();

    private CRContext context;

    /**
     * @param context conflict resolution context
     */
    public ObjectUpdateSourceStoredComparator(CRContext context) {
        this.context = context;
    }

    @Override
    public int compare(Statement statement1, Statement statement2) {
        // Compare by object
        int objectComparison = CRUtils.compareValues(statement1.getObject(), statement2.getObject());
        if (objectComparison != 0) {
            return objectComparison;
        }

        // Compare by update tag
        Value updateTag1 = ODCSUtils.getSingleObjectValue(statement1.getContext(), ODCS.UPDATE_TAG, context.getMetadata());
        String updateTagString1 = updateTag1 == null ? null : updateTag1.stringValue();
        Value updateTag2 = ODCSUtils.getSingleObjectValue(statement2.getContext(), ODCS.UPDATE_TAG, context.getMetadata());
        String updateTagString2 = updateTag2 == null ? null : updateTag2.stringValue();
        int updateTagComparison = ODCSUtils.nullProofCompare(updateTagString1, updateTagString2);
        if (updateTagComparison != 0) {
            return updateTagComparison;
        }

        // Compare by data source
        Set<Value> dataSources1 = context.getMetadata().filter(statement1.getContext(), ODCS.SOURCE, null).objects();
        Set<Value> dataSources2 = context.getMetadata().filter(statement2.getContext(), ODCS.SOURCE, null).objects();
        int dataSourceComparison = setCompare(dataSources1, dataSources2);
        if (dataSourceComparison != 0) {
            return dataSourceComparison;
        }

        // Compare by stored time in *descending order*
        return -compareByInsertedAt(statement1.getContext(), statement2.getContext());
    }

    private int compareByInsertedAt(Resource graph1, Resource graph2) {
        Value metadataValue1 = ODCSUtils.getSingleObjectValue(graph1, ODCS.INSERTED_AT, context.getMetadata());
        Value metadataValue2 = ODCSUtils.getSingleObjectValue(graph2, ODCS.INSERTED_AT, context.getMetadata());

        // Check if the metadata is present
        if (metadataValue1 == metadataValue2) {
            return 0;
        } else if (metadataValue1 == null) {
            return -1;
        } else if (metadataValue2 == null) {
            return 1;
        } else if (!(metadataValue1 instanceof Literal)) {
            return 0; // undefined
        }

        // Get proper literal comparator
        EnumLiteralType comparisonType = ResolutionFunctionUtils.getLiteralType((Literal) metadataValue1);
        BestSelectedLiteralComparator comparator = LiteralComparatorFactory.getComparator(comparisonType);

        // Use literal comparator to compare the metadata statements
        boolean accept1 = comparator.accept(metadataValue1, context);
        boolean accept2 = comparator.accept(metadataValue2, context);
        if (accept1 && accept2) {
            return comparator.compare(metadataValue1, metadataValue2, context);
        } else {
            return Boolean.compare(accept1, accept2);
        }
    }

    private int setCompare(Set<Value> set1, Set<Value> set2) {
        if (set1.size() - set2.size() != 0) {
            return set1.size() - set2.size();
        } else if (set1.size() == 1) {
            return ODCSUtils.nullProofCompare(set1.iterator().next(), set2.iterator().next(), VALUE_COMPARATOR);
        } else if (set1.equals(set2)) {
            return 0;
        } else { // hopefully, we won't typically get here
            SortedSet<Value> sortedSet1;
            if (set1 instanceof SortedSet<?>) {
                sortedSet1 = (SortedSet<Value>) set1;
            } else {
                sortedSet1 = new TreeSet<Value>(VALUE_COMPARATOR);
                sortedSet1.addAll(set1);
            }
            SortedSet<Value> sortedSet2;
            if (set2 instanceof SortedSet<?>) {
                sortedSet2 = (SortedSet<Value>) set2;
            } else {
                sortedSet2 = new TreeSet<Value>(VALUE_COMPARATOR);
                sortedSet2.addAll(set2);
            }

            Iterator<Value> it1 = sortedSet1.iterator();
            Iterator<Value> it2 = sortedSet2.iterator();
            while (it1.hasNext()) {
                int comparison = ODCSUtils.nullProofCompare(it1.next(), it2.next(), VALUE_COMPARATOR);
                if (comparison != 0) {
                    return comparison;
                }
            }
            return 0;
        }
    }
}