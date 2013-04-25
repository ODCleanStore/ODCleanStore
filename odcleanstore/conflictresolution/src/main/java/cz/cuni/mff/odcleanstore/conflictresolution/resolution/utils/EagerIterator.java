/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils;

import java.util.Iterator;

/**
 * @author Jan Michelfeit
 */
public abstract class EagerIterator<T> implements Iterator<T> {
    private T next = null;
    private boolean initialized = false;

    private void checkInitialized() {
        if (!initialized) {
            next = findNext();
            initialized = true;
        }
    }
    
    protected abstract T findNext();
    
    @Override
    public boolean hasNext() {
        checkInitialized();
        return next != null;
    }

    @Override
    public T next() {
        checkInitialized();
        T result = next;
        next = findNext();
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException(); 
    }

}
