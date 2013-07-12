/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.impl.util;

import java.util.Arrays;

/**
 * A wrapper for an array growing as needed in the same way as {@link java.util.ArrayList}. 
 * Custom implementation is used because {@link java.util.ArrayList} doesn't allow access to internal storage array.
 * @param <T> type of array elements
 * @author Jan Michelfeit
 */
public class GrowingArray<T> {
    /** See {@link #MAX_ARRAY_SIZE}. */
    private static final int MEMORY_RESERVE = 8;
    
    private static final int DEFAULT_INITIAL_CAPACITY = 8;
    
    /** The maximum size of array to allocate - see {@link java.util.ArrayList} for explanation. */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - MEMORY_RESERVE;
    private T[] arr;
    private int size;

    /** 
     * Creates a new instance with the given initial capacity.
     * @param initialCapacity initial capacity of the underlying array
     */
    @SuppressWarnings("unchecked")
    public GrowingArray(int initialCapacity) {
        this.arr = (T[]) new Object[initialCapacity];
    }

    /**
     * Creates a new instance.
     */
    public GrowingArray() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Adds an element to the end of the array, resizing the array if neccesary.
     * @param element element to insert
     */
    public void add(T element) {
        ensureSize(size + 1);
        arr[size++] = element;
    }
    
    /**
     * Returns the underlying array containing stored elements.
     * @return the wrapped underlying array
     */
    public T[] getArray() {
        return arr;
    }

    private void ensureSize(int minSize) {
        if (minSize - arr.length <= 0) {
            return;
        }
        if (minSize < 0) {
            throw new OutOfMemoryError(); // overflow
        }
        int oldCapacity = arr.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minSize < 0) {
            newCapacity = minSize;
        }
        if (newCapacity - MAX_ARRAY_SIZE > 0) {
            newCapacity = (minSize > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
        }

        arr = Arrays.copyOf(arr, newCapacity);
    }
}
