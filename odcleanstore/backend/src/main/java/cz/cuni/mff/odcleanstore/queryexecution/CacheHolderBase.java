package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;

/**
 * Holder for a cached value, caching the value for the given lifetime.
 * This class is thread-safe.
 * @param <T> type of the cached value
 * @author Jan Michelfeit
 */
/*package*/abstract class CacheHolderBase<T> {
    /** Lifetime of the cached value in milliseconds. */
    private final long cacheLifetime;

    /** The cached value. */
    private T cachedValue;

    /** Last time the cached value was refreshed from the database. -1 means never. */
    private volatile long lastRefreshTime = -1;

    /**
     * Creates a new instance.
     * @param cacheLifetime lifetime of the cached value
     */
    public CacheHolderBase(long cacheLifetime) {
        this.cacheLifetime = cacheLifetime;
    }

    /**
     * Returns the cached value; if the value hasn't been loaded yet, load it and store to cache.
     * @return the cached value
     * @throws DatabaseException database error
     */
    public T getCachedValue() throws DatabaseException {
        if (System.currentTimeMillis() - lastRefreshTime > cacheLifetime) {
            // The double-checked locking idiom should work here because we test volatile lastRefreshTime
            // CHECKSTYLE:OFF
            synchronized (this) {
                if (System.currentTimeMillis() - lastRefreshTime > cacheLifetime) {
                    cachedValue = loadCachedValue();
                    lastRefreshTime = System.currentTimeMillis();
                }
            }
            // CHECKSTYLE:ON
        }
        return cachedValue;
    }

    /**
     * Load the value to be cached.
     * @return the value to be cached
     * @throws DatabaseException database error
     */
    protected abstract T loadCachedValue() throws DatabaseException;
}
