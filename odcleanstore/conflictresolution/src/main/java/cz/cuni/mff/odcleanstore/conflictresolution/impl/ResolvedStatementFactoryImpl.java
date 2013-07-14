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
import org.openrdf.model.impl.ValueFactoryImpl;

import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatementFactory;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.SimpleUriGenerator;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

/**
 * Basic implementation of {@link ResolvedStatementFactory} placing resolved quads in
 * unique named graphs with the given prefix.
 * @author Jan Michelfeit
 */
public class ResolvedStatementFactoryImpl implements ResolvedStatementFactory {
    private static final ValueFactory VALUE_FACTORY = ValueFactoryImpl.getInstance();

    private final UniqueURIGenerator uriGenerator;

    /**
     * Creates a new instance.
     * @param namedGraphURIPrefix prefix of named graphs where resolved quads are placed.
     */
    public ResolvedStatementFactoryImpl(String namedGraphURIPrefix) {
        this.uriGenerator = new SimpleUriGenerator(namedGraphURIPrefix);
    }

    @Override
    public ResolvedStatement create(Resource subject, URI predicate, Value object,
            double quality, Collection<Resource> sourceGraphNames) {

        URI context = VALUE_FACTORY.createURI(uriGenerator.nextURI());
        Statement statement = VALUE_FACTORY.createStatement(subject, predicate, object, context);
        return new ResolvedStatementImpl(statement, quality, sourceGraphNames);
    }

    @Override
    public ValueFactory getValueFactory() {
        return VALUE_FACTORY;
    }

}
