/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.impl.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.AbstractModel;

/**
 * An empty read-only implementation of {@link Model}.
 * Data manipulation methods throw {@link UnsupportedOperationException}.
 * @author Jan Michelfeit
 */
public class EmptyMetadataModel extends AbstractModel {
    private static final long serialVersionUID = 1L;
    private static final EmptyMetadataModel INSTANCE = new EmptyMetadataModel();
    
    private final Set<Statement> emptySet = Collections.emptySet();
    private final Set<Namespace> emptyNamespaces = Collections.emptySet();
    
    /**
     * Returns shared default instance of this class.
     * @return shared default instance of this class
     */
    public static Model getInstance() {
        return INSTANCE;
    }

    @Override
    public Namespace getNamespace(String prefix) {
        return null;
    }

    @Override
    public Set<Namespace> getNamespaces() {
        return emptyNamespaces;
    }

    @Override
    public Namespace setNamespace(String prefix, String name) {
        throw new UnsupportedOperationException("Cannot add namespace");
    }

    @Override
    public void setNamespace(Namespace namespace) {
        throw new UnsupportedOperationException("Cannot add namespace");
    }

    @Override
    public Namespace removeNamespace(String prefix) {
        return null;
    }

    @Override
    public Iterator<Statement> iterator() {
        return emptySet.iterator();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean add(Resource subj, URI pred, Value obj, Resource... contexts) {
        throw new UnsupportedOperationException("All statements are filtered out of view");
    }

    @Override
    public boolean contains(Resource subj, URI pred, Value obj, Resource... contexts) {
        return false;
    }

    @Override
    public Model filter(Resource subj, URI pred, Value obj, Resource... contexts) {
        return this;
    }

    @Override
    public boolean remove(Resource subj, URI pred, Value obj, Resource... contexts) {
        return false;
    }

    @Override
    public void removeTermIteration(Iterator<Statement> iter, Resource subj, URI pred, Value obj,
            Resource... contexts) {
        // remove nothing
    }

}
