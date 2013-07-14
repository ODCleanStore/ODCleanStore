/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils;

import java.util.Iterator;

/**
 * Base class for iterators which can determine availability of the next element only by 
 * attempting to retrieve it. The retrieval of the next element is realized by the abstract
 * method {@link #findNext()} and the element is then cached for subsequent calls of {@link #hasNext()}
 * until {@link #next()} is called, which returns the cached element and caches the next one.  
 * Method {@link #remove()} is not supported.
 * @param <T> type of elements
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
    
    /**
     * Retrieves the next element (and moves the underlying internal pointer).
     * @return the next element in the underlying collection
     */
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
