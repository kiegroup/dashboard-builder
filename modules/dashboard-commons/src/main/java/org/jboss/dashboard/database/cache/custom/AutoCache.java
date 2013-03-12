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

import org.apache.commons.collections.ReferenceMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * This cache implementation uses soft references so that cached objects are
 * automatically garbage collected when needed. Note that this maximizes the
 * cache size at the expense of making no guarantees as to the cache-clearing
 * algorithm used. So, it makes for good memory use at the possible expense of
 * cache hit frequency.
 */
public class AutoCache implements StatisticsObjectCache {

    Log log = LogFactory.getLog(this.getClass());

    //-------------------------------------------------------------------------
    // Fields
    //-------------------------------------------------------------------------

    private String type;
    private Map cache;
    private CacheMonitor cacheMonitor;

    //-------------------------------------------------------------------------
    // Constructors
    //-------------------------------------------------------------------------

    public AutoCache(boolean monitorizeCaches) {
        // Use a synchronized map
        cache = Collections.synchronizedMap(new ReferenceMap());
        if (monitorizeCaches)
            this.cacheMonitor = new CacheMonitor(Long.MAX_VALUE);
    }

    //-------------------------------------------------------------------------
    // Public methods
    //-------------------------------------------------------------------------

    /**
     * Sets the common name of the type of objects to cache.
     */
    public void setType(String cacheType) {
        if (log.isDebugEnabled()) if (log.isDebugEnabled()) log.debug("Cache type set to '" + cacheType + "'.");
        type = cacheType;
    }

    public String getType() {
        return type;
    }

    /**
     * Adds an object to the cache.
     */
    public void put(Serializable key,
                    Object object) {
        cache.put(key, object);
        if (cacheMonitor != null)
            cacheMonitor.elementAdded();
        if (log.isDebugEnabled()) if (log.isDebugEnabled()) log.debug("Put " + type + " #" + key + " in to cache.");
    }

    public Object get(Serializable key) {
        Object object = cache.get(key);
        if (object != null) {
            if (log.isDebugEnabled()) log.debug("Got " + type + " #" + key + " from cache.");
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
        Object o = cache.remove(key);
        if (o != null && cacheMonitor != null) {
            cacheMonitor.elementEvicted();
        }
        return o;
    }

    public void clearAll() {
        // Just make a new one
        if (log.isDebugEnabled()) log.debug("Cleared entire " + type + " cache.");
        cache = Collections.synchronizedMap(new ReferenceMap());
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