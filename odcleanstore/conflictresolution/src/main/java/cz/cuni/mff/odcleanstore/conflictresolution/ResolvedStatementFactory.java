/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Collection;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;

/**
 * @author Jan Michelfeit
 */
public interface ResolvedStatementFactory {
    ResolvedStatement create(Resource subject, URI predicate, Value object, double confidence, Collection<Resource> sources);
    ValueFactory getValueFactory();
}
