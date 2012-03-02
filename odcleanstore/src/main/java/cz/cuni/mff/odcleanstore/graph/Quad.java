package cz.cuni.mff.odcleanstore.graph;

/**
 * Immutable!
 * @author Jan Michelfeit
 */
public class Quad extends Triple {

    private String namedGraphURI;

    public Quad(TripleItem subject, TripleItem predicate, TripleItem object, String namedGraphURI) {
        super(subject, predicate, object);
        this.namedGraphURI = namedGraphURI;
    }

    public Quad(Triple triple, String namedGraphURI) {
        super(triple.getSubject(), triple.getPredicate(), triple.getObject());
        this.namedGraphURI = namedGraphURI;
    }

    public Triple getTriple() {
        return this;
    }

    public String getNamedGraph() {
        return namedGraphURI;
    }

    @Override
    public String toString() {
        return super.toString() + " " + namedGraphURI;
    }
}
