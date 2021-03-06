package cz.cuni.mff.odcleanstore.conflictresolution.quality;

import java.util.Collection;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;

/**
 * A dummy implementation of {@link FQualityCalculator} returning always {@value #VALUE_QUALITY}.
 * @author Jan Michelfeit
 */
public class DummyFQualityCalculator implements FQualityCalculator, MediatingFQualityCalculator,
        DecidingFQualityCalculator {
    /** The value always returned by {@link #getFQuality()}. */ 
    public static final double VALUE_QUALITY = 1;

    @Override
    public double getFQuality(Value value, Collection<Statement> conflictingStatements, Collection<Resource> sources,
            CRContext crContext) {
        return VALUE_QUALITY;
    }
}
