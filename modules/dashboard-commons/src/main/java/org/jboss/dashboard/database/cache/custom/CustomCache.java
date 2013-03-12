/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.dashboard.database.cache.custom;

import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;

import java.io.Serializable;
import java.util.Map;

public class CustomCache implements Cache {

    private final ObjectCache cache;
    private final String regionName;

    public CustomCache(ObjectCache cache, String regionName) {
        this.cache = cache;
        this.regionName = regionName;
    }

    /**
     * Get an item from the cache
     *
     * @param key
     * @return the cached object or <tt>null</tt>
     * @throws CacheException
     */
    public Object get(Object key) throws CacheException {
        if (key instanceof Serializable) {
            return cache.get((Serializable) key);
        } else {
            throw new CacheException("Keys must implement Serializable");
        }
    }

    public Object read(Object key) throws CacheException {
        return get(key);
    }

    /**
     * Add an item to the cache
     *
     * @param key
     * @param value
     * @throws CacheException
     */
    public void update(Object key, Object value) throws CacheException {
        put(key, value);
    }

    /**
     * Add an item to the cache
     *
     * @param key
     * @param value
     * @throws CacheException
     */
    public void put(Object key, Object value) throws CacheException {
        if (key instanceof Serializable) {
            cache.put((Serializable) key, value);
        } else {
            throw new CacheException("Keys must implement Serializable");
        }
    }

    /**
     * Remove an item from the cache
     */
    public void remove(Object key) throws CacheException {
        if (key instanceof Serializable) {
            cache.clear((Serializable) key);
        } else {
            throw new CacheException("Keys must implement Serializable");
        }
    }

    /**
     * Clear the cache
     */
    public void clear() throws CacheException {
        cache.clearAll();
    }

    /**
     * Clean up
     */
    public void destroy() throws CacheException {
        cache.clearAll();
    }

    /**
     * If this is a clustered cache, lock the item
     */
    public void lock(Object key) throws CacheException {
        throw new UnsupportedOperationException("CustomCache does not support locking (use nonstrict-read-write)");
    }

    /**
     * If this is a clustered cache, unlock the item
     */
    public void unlock(Object key) throws CacheException {
        throw new UnsupportedOperationException("CustomCache does not support locking (use nonstrict-read-write)");
    }

    /**
     * Generate a (coarse) timestamp
     */
    public long nextTimestamp() {
        return System.currentTimeMillis() / 100;
    }

    /**
     * Get a reasonable "lock timeout"
     */
    public int getTimeout() {
        return 600;
    }

    public String getRegionName() {
        return regionName;
    }

    public long getSizeInMemory() {
        return -1;
    }

    public long getElementCountInMemory() {
        return -1;
    }

    public long getElementCountOnDisk() {
        return -1;
    }

    public Map toMap() {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        return "CustomCache(" + regionName + ')';
    }
}
