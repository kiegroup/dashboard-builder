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

public class CacheMonitor {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(CacheMonitor.class);

    private long numHits = 0;
    private long numMisses = 0;
    private long currentCacheOccupation = 0;
    private long numOperations = 0;
    private float averageOccupation = 0;
    private long cacheSize;

    protected CacheMonitor(long cacheSize) {
        this.cacheSize = cacheSize;
        resetStatistics();
    }

    /**
     * Clear statistics
     */
    protected void resetStatistics() {
        numHits = 0;
        numMisses = 0;
        numOperations = 0;
        currentCacheOccupation = 0;
        averageOccupation = 0;
    }

    public CacheStatistics getCacheStatistics() {
        return new CacheStatistics() {
            public float getHitRate() {
                if (numHits + numMisses == 0) return 0;
                return (100.0f * numHits) / (numHits + numMisses);
            }

            public float getAverageOccupation() {
                return averageOccupation;
            }

            public float getAverageOccupationRate() {
                return getAverageOccupation() * 100.0f / cacheSize;
            }

            public long getCacheSize() {
                return cacheSize;
            }

            public long getNumOperations() {
                return numOperations;
            }

            public long getNumReads() {
                return numHits + numMisses;
            }
        };
    }

    public synchronized void elementAdded() {
        currentCacheOccupation++;
        numOperations++;
        averageOccupation += ((currentCacheOccupation * 1.0f) / numOperations);
    }

    public synchronized void elementEvicted() {
        currentCacheOccupation--;
        numOperations++;
        averageOccupation = (((numOperations - 1.0f) / (numOperations)) * (averageOccupation)) + ((currentCacheOccupation * 1.0f) / numOperations);
    }

    public synchronized void allElementsEvicted() {
        currentCacheOccupation = 0;
    }

    public synchronized void registerCacheHit() {
        numHits++;
    }

    public synchronized void registerCacheMiss() {
        numMisses++;
    }
}