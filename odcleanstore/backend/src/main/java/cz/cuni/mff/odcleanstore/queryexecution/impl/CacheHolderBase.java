package cz.cuni.mff.odcleanstore.queryexecution.impl;

import cz.cuni.mff.odcleanstore.queryexecution.QueryExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Holder for a cached value, caching the value for the given lifetime.
 * This class is thread-safe.
 * @param <T> type of the cached value
 * @author Jan Michelfeit
 */
/*package*/abstract class CacheHolderBase<T> {
    private static final Logger LOG = LoggerFactory.getLogger(CacheHolderBase.class);

    /** Lifetime of the cached value in milliseconds. Zero means no caching. */
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
     * @throws QueryExecutionException error
     */
    public T getCachedValue() throws QueryExecutionException {
        if (cacheLifetime == 0) {
            return loadCachedValue();
        }
        if (System.currentTimeMillis() - lastRefreshTime > cacheLifetime) {
            // The double-checked locking idiom should work here because we test volatile lastRefreshTime
            // CHECKSTYLE:OFF
            synchronized (this) {
                if (System.currentTimeMillis() - lastRefreshTime > cacheLifetime) {
                    LOG.info("Refreshing {} cache", this.getClass().getSimpleName());
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
     * Access to this method is synchronized.
     * @return the value to be cached
     * @throws QueryExecutionException error
     */
    protected abstract T loadCachedValue() throws QueryExecutionException;
}
