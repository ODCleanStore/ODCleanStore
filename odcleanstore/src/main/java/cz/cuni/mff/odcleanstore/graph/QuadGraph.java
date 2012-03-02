package cz.cuni.mff.odcleanstore.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Jan Michelfeit
 */
public class QuadGraph implements Iterable<Quad> {
    private Collection<Quad> quads;

    public QuadGraph() {
        quads = new ArrayList<Quad>();
    }

    public QuadGraph(Collection<Quad> quads) {
        this.quads = quads;
    }

    public void add(Quad quad) {
        quads.add(quad);
    }

    @Override
    public Iterator<Quad> iterator() {
        return quads.iterator();
    }

    public int size() {
        return quads.size();
    }
}
