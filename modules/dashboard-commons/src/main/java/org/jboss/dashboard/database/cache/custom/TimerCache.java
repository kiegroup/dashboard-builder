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
 * Cache implementation that times out cached elements.
 * Each item put in the cache is timed out after a specified number of milliseconds (unless removed before the timeout).
 */
public class TimerCache implements StatisticsObjectCache, Runnable {
    private CacheMonitor cacheMonitor;

    //-------------------------------------------------------------------------
    // Inner classes
    //-------------------------------------------------------------------------

    static class TStampObject {
        public long time;
        public Object object;
    }

    private static final Log log = LogFactory.getLog(TimerCache.class);

    //-------------------------------------------------------------------------
    // Fields
    //-------------------------------------------------------------------------

    /**
     * Cache type
     */
    private String type;

    /**
     * The next wake-up deadline for the queue thread.
     */
    private long deadline = Long.MAX_VALUE;

    /**
     * True if the cache-clearing thread should keep running.
     */
    private boolean running = true;

    /**
     * The timer thread that clears cached objects.
     */
    private Thread thread;

    /**
     * The cache timeout (in milliseconds).
     */
    private long timeout;

    /**
     * The map that will store the cache.
     */
    private final SynchronizedLRUMap cache;

    public TimerCache(boolean monitorizeCaches, long timeout, int size) {
        this.timeout = timeout;
        cache = new SynchronizedLRUMap(size);
        thread = new Thread(this);
        thread.start();
        if (monitorizeCaches)
            this.cacheMonitor = new CacheMonitor(size);
    }

    /**
     * Sets a new timeout value-- only do this before using the cache!
     */
    public void setTimeout(long newTimeout) {
        timeout = newTimeout;
    }

    /**
     * Called to stop the timer thread. This should only be called once and the cache should not be used afterwards.
     */
    public synchronized void stop() {
        running = false;
        notify();
    }

    /**
     * Gets the cache type name.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the cache type name.
     */
    public void setType(String type) {
        if (log.isDebugEnabled()) log.debug("Cache type set to '" + type + "'.");
        this.type = type;
        thread.setName("TimerCache-" + type);
    }

    /**
     * Caches an object. If object is <code>null</code>, then any object cached at the given key is removed from the cache.
     */
    public void put(Serializable key,
                    Object object) {
        if (key == null) {
            log.error("Trying to put object in cache with null key. Ignoring");
            return;
        }
        if (object == null) {
            clear(key);
        } else {
            TStampObject tobj = new TStampObject();
            tobj.object = object;
            tobj.time = System.currentTimeMillis() + timeout;
            cache.put(key, tobj);
            setDeadline(tobj.time);
            if (cacheMonitor != null)
                cacheMonitor.elementAdded();
            if (log.isDebugEnabled()) log.debug("Put " + type + " #" + key + " in to cache.");
        }
    }

    public Object get(Serializable key) {
        if (key == null) return null;
        TStampObject tobj = (TStampObject) cache.get(key);
        Object object = null;
        if (tobj != null) {
            if (log.isDebugEnabled()) log.debug("Got " + type + " #" + key + " from cache.");
            object = tobj.object;
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

    //// Timer methods

    protected synchronized void setDeadline(long time) {
        if (time < deadline) {
            deadline = time;
            notify();
        }
    }

    public void run() {
        while (running) {
            try {
                synchronized (this) {
                    long now = System.currentTimeMillis();
                    long waitTime = deadline - now;
                    if (waitTime > 0) {
                        wait(waitTime);
                    }
                }
            } catch (Exception e) {
                log.error(e);
            }
            synchronized (cache) {
                trimQueue(cache);
            }
        }
    }

    protected void trimQueue(SynchronizedLRUMap cache) {
        if (log.isDebugEnabled()) log.debug("Waking up.");
        if (cache.isEmpty()) return;
        Object key = cache.firstKey();
        TStampObject tobj = (TStampObject) cache.get(key);
        if (tobj != null) {
            long now = System.currentTimeMillis();
            while ((tobj != null) && (tobj.time <= now)) {
                cache.remove(key);
                if (log.isDebugEnabled()) log.debug("Timed out object with key '" + key + "'.");
                if (cache.isEmpty()) {
                    key = null;
                    tobj = null;
                } else {
                    key = cache.firstKey();
                    tobj = (TStampObject) cache.get(key);
                }
            }
        }
        if (tobj != null) {
            deadline = tobj.time;
        } else {
            deadline = Long.MAX_VALUE;
        }
        if (log.isDebugEnabled()) log.debug("Sleeping.");
    }

    public CacheStatistics getCacheStatistics() {
        if (cacheMonitor != null)
            return cacheMonitor.getCacheStatistics();
        return null;
    }
}