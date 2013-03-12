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
import org.jboss.dashboard.database.cache.CacheElement;
import org.jboss.dashboard.database.cache.CacheOptions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.PrintStream;
import java.util.*;

/**
 * A convenient generator of multicast caches using an underlying LRU or Automatic algorithm.
 */
public class CacheFactory extends BasicFactoryElement {
    Log log = LogFactory.getLog(this.getClass());

    public CacheOptions cacheOptions;
    public MultiCacheManager multiCacheManager;
    private Set<String> customCaches = new TreeSet<String>();
    private boolean monitorizeCaches = true;
    private List<StatisticsObjectCache> createdCaches = new ArrayList<StatisticsObjectCache>();

    public boolean isMonitorizeCaches() {
        return monitorizeCaches;
    }

    public void setMonitorizeCaches(boolean monitorizeCaches) {
        this.monitorizeCaches = monitorizeCaches;
    }

    /**
     * The <i>LRU</i> cache type.
     */
    public static final String TYPE_LRU = "LRU";

    /**
     * The <i>Automatic</i> cache type.
     */
    public static final String TYPE_AUTO = "Auto";

    /**
     * The <i>Timer</i> cache type.
     */
    public static final String TYPE_TIMER = "Timer";

    /**
     * The <i>Hybrid</i> cache type.
     */
    public static final String TYPE_HYBRID = "Hybrid";


    /**
     * The <i>FIFO</i> cache type.
     */
    public static final String TYPE_FIFO = "FIFO";


    //-------------------------------------------------------------------------
    // Constructors
    //-------------------------------------------------------------------------
    public CacheFactory() {
    }

    //-------------------------------------------------------------------------
    // Public methods
    //-------------------------------------------------------------------------
    /**
     * Creates a new cache.
     *
     * @param name a name for the cache. Useful if there will be multiple caches for various objects.
     * @return a new cache, based on configuration.
     */
    public ObjectCache createCache(String name) {
        ObjectCache cache = null;
        MultiCache multi = null;
        //
        // do not create cache, if it already exists
        if (multiCacheManager.containsCache(name)) {
            log.error("Cache of type [" + name + "] already exists.");
        } else {
            CacheElement element = null;
            if (customCaches.contains(name)) {
                element = cacheOptions.getDefaultCacheElement();
            } else {
                element = cacheOptions.getElement(name);
            }
            if (element == null) element = cacheOptions.getDefaultCacheElement();
            try {
                cache = createObjectCache(element, name);
                multi = new MultiCache(cache, multiCacheManager);
                multiCacheManager.addCache(multi);
            } catch (Exception e) {
                log.error("Problem instantiating cache:", e);
            }
        }
        return multi;
    }

    public ObjectCache createObjectCache(CacheElement element, String name) throws Exception {
        StatisticsObjectCache cache;
        if (TYPE_LRU.equals(element.getMemoryStoreEvictionPolicy())) {
            cache = new LRUCache(isMonitorizeCaches(), element.getMaxElementsInMemory());
        } else if (TYPE_AUTO.equals(element.getMemoryStoreEvictionPolicy())) {
            cache = new AutoCache(isMonitorizeCaches());
        } else if (TYPE_TIMER.equals(element.getMemoryStoreEvictionPolicy())) {
            cache = new TimerCache(isMonitorizeCaches(), element.getTimeToLiveSeconds() * 1000, element.getMaxElementsInMemory());
        } else if (TYPE_HYBRID.equals(element.getMemoryStoreEvictionPolicy())) {
            cache = new HybridCache(isMonitorizeCaches(), element.getMaxElementsInMemory());
        } else if (TYPE_FIFO.equals(element.getMemoryStoreEvictionPolicy())) {
            cache = new FIFOCache(isMonitorizeCaches(), element.getMaxElementsInMemory()); // Until we implement it ...
        } else {
            log.warn("Unknown cache type: " + element.getMemoryStoreEvictionPolicy() + ". Defaulting to HybridCache.");
            cache = new HybridCache(isMonitorizeCaches(), element.getMaxElementsInMemory());
        }
        cache.setType(name);
        createdCaches.add(cache);
        return cache;
    }

    /**
     * Get the cache statistics
     *
     * @return the cache statistics
     */
    public Map<String, CacheStatistics> getCacheStatistics() {
        Map<String, CacheStatistics> m = new TreeMap<String, CacheStatistics>();
        for (StatisticsObjectCache cache : createdCaches) {
            CacheStatistics statistics = cache.getCacheStatistics();
            if (statistics != null) {
                m.put(cache.getType(), statistics);
            }
        }
        return m;
    }

    public void createCustomCache(String cacheRegionName) {
        customCaches.add(cacheRegionName);
    }

    /**
     * Print statistics to output
     *
     * @param out output stream to write to
     */
    public void printStatistics(PrintStream out, String separator) {
        Map<String, CacheStatistics> stats = getCacheStatistics();
        Set<String> keys = stats.keySet();
        out.print("cacheName");
        out.print(separator);
        out.print("numOperations");
        out.print(separator);
        out.print("averageOccupation");
        out.print(separator);
        out.print("averageOccupationRate");
        out.print(separator);
        out.print("cacheSize");
        out.print(separator);
        out.print("hitRate");
        out.print(separator);
        out.print("numReads");
        out.println("");

        for (String cacheName : keys) {
            CacheStatistics cacheStats = stats.get(cacheName);
            out.print(cacheName);
            out.print(separator);
            out.print(cacheStats.getNumOperations());
            out.print(separator);
            out.print(cacheStats.getAverageOccupation());
            out.print(separator);
            out.print(cacheStats.getAverageOccupationRate());
            out.print(separator);
            out.print(cacheStats.getCacheSize());
            out.print(separator);
            out.print(cacheStats.getHitRate());
            out.print(separator);
            out.print(cacheStats.getNumReads());
            out.println("");
        }

    }
}
