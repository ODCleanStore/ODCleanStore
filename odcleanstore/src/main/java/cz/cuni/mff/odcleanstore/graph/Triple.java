package cz.cuni.mff.odcleanstore.graph;

import java.io.Serializable;

/**
 * ... .
 * Immutable.
 * Invariant: predicate is always an URI resource.
 */
public class Triple implements Serializable {
    private TripleItem subject;
    private TripleItem predicate;
    private TripleItem object;
    
    public Triple(TripleItem subject, TripleItem predicate, TripleItem object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }
    
    public TripleItem getSubject() {
        return subject;
    }
    
    public TripleItem getPredicate() {
        return predicate;
    }
    
    public TripleItem getObject() {
        return object;
    }
    
    @Override
    public String toString() {
        return subject.toString() 
                + " @" + predicate.toString() 
                + " " + object.toString();
    }
}