/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.impl.util;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Jan Michelfeit
 */
public class GrowingArray<T> {
    /** The maximum size of array to allocate - see {@link ArrayList} for explanation. */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    private T[] arr;
    private int size;

    @SuppressWarnings("unchecked")
    public GrowingArray(int initialCapacity) {
        this.arr = (T[]) new Object[initialCapacity];
    }

    public GrowingArray() {
        this(8);
    }

    public void add(T element) {
        ensureSize(size + 1);
        arr[size++] = element;
    }
    
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
