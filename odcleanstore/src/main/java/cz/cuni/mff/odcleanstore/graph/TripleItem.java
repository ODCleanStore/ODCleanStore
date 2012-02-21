package cz.cuni.mff.odcleanstore.graph;

import java.io.Serializable;

/**
 *
 * Immutable.
 */
public abstract class TripleItem implements Serializable {

    public String getURI() {
        throw new UnsupportedOperationException(this + " is not an URITripleItem");
    }
    
    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
    
    /* package */ TripleItem() {
    }
}
