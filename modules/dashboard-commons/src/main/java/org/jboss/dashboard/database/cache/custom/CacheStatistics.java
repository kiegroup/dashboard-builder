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

public interface CacheStatistics {

    /**
     * Determine the hit rate (in percentage)
     *
     * @return the hit rate (between 0 and 100)
     */
    public float getHitRate();

    /**
     * Determine the average occupation of the cache.
     *
     * @return the average occupation
     */
    public float getAverageOccupation();

    /**
     * Determine the average occupation of the cache in percentage.
     *
     * @return the average occupation (between 0 and 100)
     */
    public float getAverageOccupationRate();

    /**
     * Get the cache size
     *
     * @return the cache size.
     */
    public long getCacheSize();

    /**
     * Get the number of puts and gets performed on this cache.
     *
     * @return the number of puts and gets performed on this cache.
     */
    public long getNumOperations();

    /**
     * @return the number of items read out of the cache
     */
    public long getNumReads();
}
