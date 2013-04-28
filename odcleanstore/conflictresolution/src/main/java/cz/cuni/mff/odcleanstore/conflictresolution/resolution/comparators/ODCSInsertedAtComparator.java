package cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators;

import org.openrdf.model.impl.ValueFactoryImpl;

import cz.cuni.mff.odcleanstore.vocabulary.ODCS;

/**
 * @author Jan Michelfeit
 */
public class ODCSInsertedAtComparator extends MetadataValueComparator {
    public ODCSInsertedAtComparator() {
        super(ValueFactoryImpl.getInstance().createURI(ODCS.insertedAt));
    }
}
