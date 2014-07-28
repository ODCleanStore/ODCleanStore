package cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils;

import java.util.Iterator;

/**
* Wrapper for iterator with limited size.
*/
public class FixedSizeIterator<T> implements Iterator<T> {
    private Iterator<T> it;
    private int size;
    private int i = 0;

    FixedSizeIterator(Iterator<T> iterator, int size) {
        this.it = iterator;
        this.size = size;
    }

    @Override
    public boolean hasNext() {
        return i < size;
    }

    @Override
    public T next() {
        i++;
        return it.next();
    }

    @Override
    public void remove() {
        it.remove();
    }
}
