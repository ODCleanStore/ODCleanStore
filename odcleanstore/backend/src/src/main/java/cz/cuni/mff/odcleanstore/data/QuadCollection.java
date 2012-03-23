package cz.cuni.mff.odcleanstore.data;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A simple collection of {@link Quad Quads}.
 * @author Jan Michelfeit
 */
public class QuadCollection implements Collection<Quad> {
    private Collection<Quad> quads;

    /**
     * Initialize instance with an empty collection of quads.
     */
    public QuadCollection() {
        quads = new ArrayList<Quad>();
    }

    /**
     * Initialize instance with the given collection of quads.
     * @param quads collection of quads
     */
    public QuadCollection(Collection<Quad> quads) {
        this.quads = quads;
    }

    @Override
    public boolean add(Quad quad) {
        return quads.add(quad);
    }

    @Override
    public Iterator<Quad> iterator() {
        return quads.iterator();
    }

    @Override
    public int size() {
        return quads.size();
    }

    @Override
    public boolean addAll(Collection<? extends Quad> c) {
        return quads.addAll(c);
    }

    @Override
    public void clear() {
        quads.clear();

    }

    @Override
    public boolean contains(Object o) {
        return quads.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return quads.containsAll(c);
    }

    @Override
    public boolean isEmpty() {
        return quads.isEmpty();
    }

    @Override
    public boolean remove(Object o) {
        return quads.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return quads.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return quads.retainAll(c);
    }

    @Override
    public Object[] toArray() {
        return quads.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return quads.toArray(a);
    }
}
