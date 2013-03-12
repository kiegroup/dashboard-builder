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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;

/**
 * A hybrid cache solution that uses a (presumably small) LRU cache backed by a larger AutoCache.
 * This implementation aims to be a good trade-off between cache hit frequency and agressive memory usage.
 */
public class HybridCache implements StatisticsObjectCache, LRUCacheListener {

    Log log = LogFactory.getLog(this.getClass());

    //-------------------------------------------------------------------------
    // Fields
    //-------------------------------------------------------------------------

    private String type;

    /**
     * The LRU Cache.
     */
    private LRUCache lruCache;

    /**
     * The LRU Cache.
     */
    private AutoCache autoCache;
    private CacheMonitor cacheMonitor;

    public HybridCache(boolean monitorizeCaches, int size) {
        lruCache = new LRUCache(false, size);
        autoCache = new AutoCache(false);
        // Listen for automatic removals of objects
        lruCache.setListener(this);
        if (monitorizeCaches)
            this.cacheMonitor = new CacheMonitor(size);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (log.isDebugEnabled()) log.debug("Cache type set to '" + type + "'.");
        this.type = type;
        lruCache.setType(type);
        autoCache.setType(type);
    }

    /**
     * Called when an object is automatically removed from the LRU cache.
     */
    public void objectRemoved(Serializable key, Object value) {
        // Put the removed object in the auto cache.
        autoCache.put(key, value);
        if (log.isDebugEnabled()) log.debug("Moved " + type + " #" + key + " to the auto cache.");
        if (cacheMonitor != null)
            cacheMonitor.elementEvicted();
    }

    public void put(Serializable key,
                    Object object) {
        lruCache.put(key, object);
        if (cacheMonitor != null)
            cacheMonitor.elementAdded();
        if (log.isDebugEnabled()) log.debug("Put " + type + " #" + key + " in to cache.");
    }

    public Object get(Serializable key) {
        Object object = lruCache.get(key);
        if (object != null) {
            if (log.isDebugEnabled()) log.debug("Got " + type + " #" + key + " from LRU cache.");
        } else {
            object = autoCache.get(key);
            if (object != null) {
                if (log.isDebugEnabled()) log.debug("Got " + type + " #" + key + " from auto cache.");
                // Upgrade to LRU cache
                lruCache.put(key, object);
                autoCache.clear(key);
            }
        }
        if (cacheMonitor != null) {
            if (object != null) {
                cacheMonitor.registerCacheHit();
            } else {
                cacheMonitor.registerCacheMiss();
            }
        }
        return object;
    }

    public Object clear(Serializable key) {
        if (log.isDebugEnabled()) log.debug("Cleared " + type + " #" + key + " from cache.");
        Object o = lruCache.clear(key);
        if (o == null) {
            o = autoCache.clear(key);
            if (o != null) {
                if (cacheMonitor != null) {
                    cacheMonitor.elementEvicted();
                }
            }
        }
        return o;
    }

    public void clearAll() {
        if (log.isDebugEnabled()) log.debug("Cleared entire " + type + " cache.");
        lruCache.clearAll();
        autoCache.clearAll();
        if (cacheMonitor != null) {
            cacheMonitor.allElementsEvicted();
        }
    }

    public CacheStatistics getCacheStatistics() {
        if (cacheMonitor != null)
            return cacheMonitor.getCacheStatistics();
        return null;
    }
}
