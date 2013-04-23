/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import java.util.Collection;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;

import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatementFactory;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.SimpleUriGenerator;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

/**
 * @author Jan Michelfeit
 */
public class ResolvedStatementFactoryImpl implements ResolvedStatementFactory {
    
    private ValueFactory valueFactory;
    
    private final UniqueURIGenerator uriGenerator;
    
    public ResolvedStatementFactoryImpl(String namedGraphURIPrefix) {
        this.uriGenerator = new SimpleUriGenerator(namedGraphURIPrefix);
    }

    @Override
    public ResolvedStatement create(Resource subject, URI predicate, Value object, double confidence, Collection<Resource> sourceGraphNames) {
        URI context = uriGenerator.nextURI();
        Statement statement = valueFactory.createStatement(subject, predicate, object, context);
        return new ResolvedStatementImpl(statement, confidence, sourceGraphNames);
    }
    
    @Override
    public ValueFactory getValueFactory() {
        return valueFactory;
    }

}
