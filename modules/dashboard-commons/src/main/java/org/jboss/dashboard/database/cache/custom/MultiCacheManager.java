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

import org.jboss.dashboard.factory.BasicFactoryElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the communications between other cache managers.
 */
public class MultiCacheManager extends BasicFactoryElement {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(MultiCacheManager.class.getName());
    protected static Map instances = new HashMap();

    public boolean groupClears = true;
    public long clearIntervalMillis = 1000;
    private List<CacheNotification> cacheNotifications = new ArrayList<CacheNotification>();
    private boolean shuttingDown = false;
    private HashMap<String, MultiCache> caches;

    public MultiCacheManager() {
        caches = new HashMap<String, MultiCache>();
    }

    public void addCache(MultiCache cache) {
        caches.put(cache.getType(), cache);
    }

    public void start() throws Exception {
        super.start();
        if (groupClears) {
            Runnable sendClears = new Runnable() {
                public void run() {
                    do {
                        try {
                            Thread.sleep(clearIntervalMillis);
                        } catch (InterruptedException e) {
                            log.error("Error: ", e);
                        }
                        synchronized (cacheNotifications) {
                            if (!cacheNotifications.isEmpty()) {
                                if (log.isDebugEnabled()) {
                                    log.warn("Sending " + cacheNotifications.size() + " cache clears.");
                                }
                                cacheNotifications.clear();
                            }
                        }
                    } while (!shuttingDown);
                    cacheNotifications = null;
                }
            };
            Thread tflusher = new Thread(sendClears, "Cache clears flusher. Flushes cache invalidation messages.");
            tflusher.setPriority(Thread.MIN_PRIORITY);
            tflusher.setDaemon(false);
            tflusher.start();
        }
    }

    public void shutdown() throws Exception {
        super.shutdown();
        shuttingDown = true;
    }

    /*
    * checks if a manager contains the cache of a certain type
    * @param type of cache
    * @return true if cache exists, false otherwise
    *
    */
    public boolean containsCache(String type) {
        return caches.containsKey(type);
    }

    public void clearAllCaches() {
        for (MultiCache cache : caches.values()) {
            cache.doClearAll();
        }
    }
}
