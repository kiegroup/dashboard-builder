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

import org.jboss.dashboard.database.cache.custom.map.SynchronizedLRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;

/**
 * Cache implementation that uses the Least Recently Used algorithm.
 * This algorithm provides good cache hit frequency.
 */
public class LRUCache implements StatisticsObjectCache {

    Log log = LogFactory.getLog(this.getClass());
    private CacheMonitor cacheMonitor;

    //-------------------------------------------------------------------------
    // Inner classes
    //-------------------------------------------------------------------------

    public class ListeningLRUMap extends SynchronizedLRUMap {

        public ListeningLRUMap(int size) {
            super(size);
        }

        /**
         * The listener for objects automatically removed from the cache.
         */
        private LRUCacheListener listener = null;

        public void setListener(LRUCacheListener l) {
            listener = l;
        }

        protected void processRemovedLRU(Object key, Object value) {
            if (listener != null) {
                listener.objectRemoved((Serializable) key, value);
            }
        }
    }

    //-------------------------------------------------------------------------
    // Fields
    //-------------------------------------------------------------------------

    private String type;

    /**
     * The maximum number of objects that can be cached.
     */
    private int size;

    /**
     * A reference to underlying ListeningLRUMap
     */
    private ListeningLRUMap cache;

    public LRUCache(boolean monitorizeCaches, int size) {
        cache = new ListeningLRUMap(size);
        if (monitorizeCaches)
            this.cacheMonitor = new CacheMonitor(size);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (log.isDebugEnabled()) log.debug("Cache type set to '" + type + "'.");
        this.type = type;
    }

    public void put(Serializable key,
                    Object object) {
        cache.put(key, object);
        if (cacheMonitor != null)
            cacheMonitor.elementAdded();
        if (log.isDebugEnabled()) log.debug("Put " + type + " #" + key + " in to cache.");
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
        if (log.isDebugEnabled()) log.debug("Cleared entire " + type + " cache.");
        cache.clear();
        if (cacheMonitor != null) {
            cacheMonitor.allElementsEvicted();
        }
    }

    public void setListener(LRUCacheListener l) {
        cache.setListener(l);
    }

    public CacheStatistics getCacheStatistics() {
        if (cacheMonitor != null)
            return cacheMonitor.getCacheStatistics();
        return null;
    }

}