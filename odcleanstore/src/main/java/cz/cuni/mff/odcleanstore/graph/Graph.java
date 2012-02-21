package cz.cuni.mff.odcleanstore.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Jan Michelfeit
 */
public class Graph implements Iterable<Triple>  {
    private Collection<Triple> triples;
    
    public Graph() {
        this.triples = new LinkedList<Triple>();
    }
    
    public void add(Triple triple) {
        triples.add(triple);
    }

    @Override
    public Iterator<Triple> iterator() {
        return triples.iterator();
    }
    
    public int size() {
        return triples.size();
    }
}
