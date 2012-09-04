package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.Utils;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Comparator of {@link Quad Quads} comparing first by objects, second
 * by data source in metadata, third by descending stored date in metadata.
 */
/*package*/class ObjectSourceStoredComparator implements Comparator<Quad> {
    /** Metadata for named graphs occurring in compared quads. */
    private NamedGraphMetadataMap namedGraphMetadata;

    /**
     * @param metadata metadata for named graphs occurring in compared quads; must not be null
     */
    public ObjectSourceStoredComparator(NamedGraphMetadataMap metadata) {
        assert metadata != null;
        this.namedGraphMetadata = metadata;
    }

    @Override
    public int compare(Quad q1, Quad q2) {
        // Compare by object
        int objectComparison = NodeComparator.compare(q1.getObject(), q2.getObject());
        if (objectComparison != 0) {
            return objectComparison;
        }

        // Get metadata
        NamedGraphMetadata metadata1 = namedGraphMetadata.getMetadata(q1.getGraphName());
        NamedGraphMetadata metadata2 = namedGraphMetadata.getMetadata(q2.getGraphName());

        // Compare by data source
        Set<String> dataSources1 = (metadata1 != null) ? metadata1.getSources() : null;
        Set<String> dataSources2 = (metadata2 != null) ? metadata2.getSources() : null;
        int dataSourceComparison = nullProofSetCompare(dataSources1, dataSources2);
        if (dataSourceComparison != 0) {
            return dataSourceComparison;
        }

        // Compare by stored time in *descending order*
        Date stored1 = (metadata1 != null) ? metadata1.getInsertedAt() : null;
        Date stored2 = (metadata2 != null) ? metadata2.getInsertedAt() : null;
        return Utils.nullProofCompare(stored2, stored1); // switched arguments
    }

    private int nullProofSetCompare(Set<String> set1, Set<String> set2) {
        if (set1 != null && set2 != null) {
            return setCompare(set1, set2);
        } else if (set1 != null) {
            return 1;
        } else if (set2 != null) {
            return -1;
        } else {
            return 0;
        }
    }

    private int setCompare(Set<String> set1, Set<String> set2) {
        if (set1.equals(set2)) {
            return 0;
        } else if (set1.size() - set2.size() != 0) {
            return set1.size() - set2.size();
        } else if (set1.size() == 1) {
            return Utils.nullProofCompare(set1.iterator().next(), set2.iterator().next());
        } else { // hopefully, we won't typically get here
            SortedSet<String> sortedSet1 = (set1 instanceof SortedSet<?>)
                    ? (SortedSet<String>) set1
                    : new TreeSet<String>(set1);
            SortedSet<String> sortedSet2 = (set2 instanceof SortedSet<?>)
                    ? (SortedSet<String>) set2
                    : new TreeSet<String>(set2);
            Iterator<String> it1 = sortedSet1.iterator();
            Iterator<String> it2 = sortedSet2.iterator();
            while (it1.hasNext()) {
                int comparison = Utils.nullProofCompare(it1.next(), it2.next());
                if (comparison != 0) {
                    return comparison;
                }
            }
            return 0;
        }
    }
}