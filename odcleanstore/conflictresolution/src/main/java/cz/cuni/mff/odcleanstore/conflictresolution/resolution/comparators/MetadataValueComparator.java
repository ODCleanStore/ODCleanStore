/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.EnumLiteralType;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ResolutionFunctionUtils;
import cz.cuni.mff.odcleanstore.shared.ODCSUtils;

/**
 * Comparator of named graph by a value of their property, given in the constructor, in given metadata.
 * I.e. for two named graphs ?g1, ?g2, the comparator would compare values ?v1, ?v2 such that triples
 * ?g1 &lt;predicateURI&gt; ?v1 and ?g2 &lt;predicateURI&gt; ?v2 are present in the metadata.
 * If there are multiple values for the predicate, only the first value returned by metadata model will be used.
 * @author Jan Michelfeit
 */
public class MetadataValueComparator implements BestSelectedComparator<Resource> {
    private URI predicateURI;
    
    /**
     * @param predicateURI the property whose values will be compared for the compared named graphs
     */
    public MetadataValueComparator(URI predicateURI) {
        this.predicateURI = predicateURI;
    }
    
    @Override
    public boolean accept(Resource context, CRContext crContext) {
        if (predicateURI == null || context == null) {
            return false;
        }

        Value metadataValue = ODCSUtils.getSingleObjectValue(context, predicateURI, crContext.getMetadata());
        return metadataValue != null && metadataValue instanceof Literal;
    }

    @Override
    public int compare(Resource context1, Resource context2, CRContext crContext) {
        Value metadataValue1 = ODCSUtils.getSingleObjectValue(context1, predicateURI, crContext.getMetadata());
        Value metadataValue2 = ODCSUtils.getSingleObjectValue(context2, predicateURI, crContext.getMetadata());

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
        boolean accept1 = comparator.accept(metadataValue1, crContext);
        boolean accept2 = comparator.accept(metadataValue2, crContext);
        if (accept1 && accept2) {
            return comparator.compare(metadataValue1, metadataValue2, crContext);
        } else {
            return Boolean.compare(accept1, accept2);
        }
    }
}
