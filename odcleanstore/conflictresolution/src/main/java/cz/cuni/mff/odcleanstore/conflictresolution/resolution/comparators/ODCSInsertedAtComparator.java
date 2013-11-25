package cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators;

import cz.cuni.mff.odcleanstore.vocabulary.ODCS;

/**
 * Comparator of named graphs based on their value of {@value ODCS#insertedAt} property which
 * contains the insertion time of the graph to ODCleanStore.
 * @author Jan Michelfeit
 */
public class ODCSInsertedAtComparator extends MetadataValueComparator {
    /** Creates a new instance. */
    public ODCSInsertedAtComparator() {
        super(ODCS.INSERTED_AT);
    }
}
