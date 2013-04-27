/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators;

import java.util.Iterator;

import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.EnumLiteralType;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ResolutionFunctionUtils;

/**
 * @author Jan Michelfeit
 */
public class MetadataValueComparator implements BestSelectedComparator<Resource> {
    private URI predicateURI;
    
    public MetadataValueComparator(URI predicateURI) {
        this.predicateURI = predicateURI;
    }
    
    @Override
    public boolean accept(Resource context, CRContext crContext) {
        if (predicateURI == null || context == null) {
            return false;
        }

        Value metadataValue = getMetadataValue(context, predicateURI, crContext.getMetadata());
        return metadataValue != null && metadataValue instanceof Literal;
    }

    @Override
    public int compare(Resource context1, Resource context2, CRContext crContext) {
        Value metadataValue1 = getMetadataValue(context1, predicateURI, crContext.getMetadata());
        Value metadataValue2 = getMetadataValue(context2, predicateURI, crContext.getMetadata());

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
    
    private Value getMetadataValue(Resource context, URI predicateURI, Model metadata) {
        Iterator<Statement> metadataIt = metadata.filter(context, predicateURI, null).iterator();
        if (metadataIt.hasNext()) {
            return metadataIt.next().getObject();
        } else {
            return null;
        }
    }
}
