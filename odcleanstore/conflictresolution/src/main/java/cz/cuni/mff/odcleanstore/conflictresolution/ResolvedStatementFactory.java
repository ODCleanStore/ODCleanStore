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
 * Factory for {@link ResolvedStatement resolved quad} objects.
 * @author Jan Michelfeit
 */
public interface ResolvedStatementFactory {
    /**
     * Returns a new {@link ResolvedStatement resolved quad} for the given RDF triple to be wrapped.
     * @param subject wrapped RDF triple subject
     * @param predicate wrapped RDF triple predicate
     * @param object wrapped RDF triple object
     * @param fQuality quality ("F-quality") of the wrapped triple
     * @param sources set of named graph URIs of graphs the wrapped triple was selected or derived from.
     * @return resolved quad
     */
    ResolvedStatement create(Resource subject, URI predicate, Value object, double fQuality, Collection<Resource> sources);
    
    /**
     * Returns a {@link Value RDF node} factory associated with this resolved quad factory.
     * @return a {@link ValueFactory} object associated with this resolved quad factory
     */
    ValueFactory getValueFactory();
}
