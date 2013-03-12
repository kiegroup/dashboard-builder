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
package org.jboss.dashboard.database.cache;

import org.jboss.dashboard.factory.BasicFactoryElement;

/**
 * This class represents a cache element with ehcache style, but intended to be used by all types of caches
 */
public class CacheElement extends BasicFactoryElement {
    /**
     * log
     */
    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(CacheElement.class.getName());

    /**
     * This is a required attribute for cache elements only. The defaultCache does not have a name attribute.
     * Legal values are any legal String value. Names must be unique. Ehcache will throw a CacheException if
     * an attempt is made to create two caches with the same name.
     * <p/>
     * Hibernate users should use the fully qualified class name of the DomainObject being cached.
     * There are different rules for other types of Hibernate cache.
     * <p/>
     * For more on Hibernate naming see  <a href="http://ehcache.sourceforge.net/documentation/#mozTocId116611">Using ehcache as a Hibernate Plugin</a>
     */
    private String name;

    /**
     * This is a required attribute.
     * <p/>
     * Legal values are integers between 0 and Integer.MAX_VALUE.
     * <p/>
     * It is the maximum number of elements to store in the MemoryStore. It is strongly recommended for performance
     * reasons that this value is at least 1. If not a warning will be issued at Cache creation time.
     */
    private int maxElementsInMemory = 30;

    /**
     * This is a required attribute.
     * <p/>
     * Whether or not the cache is eternal. An eternal cache does not expire its elements.
     * <p/>
     * Legal values are "true" or "false".
     */
    private boolean eternal = true;

    /**
     * This is a required attribute.
     * <p/>
     * Legal values are "true" or "false".
     * <p/>
     * Whether or not to use the DiskStore when the number of Elements has exceeded the maxElementsInMemory
     * of the MemoryStore.
     * <p/>
     * Entries to be removed from the MemoryStore, when it overflows, are determined using a least recently
     * used algorithm ("LRU"). Used means inserted or accessed. If false, the LRU Element is discarded.
     * If true, it is transferred to the DiskStore.
     */
    private boolean overflowToDisk = false;

    /**
     * This is an optional attribute.
     * <p/>
     * Legal values are integers between 0 and Integer.MAX_VALUE.
     * <p/>
     * It is the number of seconds that an Element should live since it was last used. Used means inserted or accessed.
     * <p/>
     * 0 has a special meaning, which is not to check the Element for time to idle, i.e. it will idle forever.
     * <p/>
     * The default value is 0.
     */
    private long timeToIdleSeconds = 60 * 60 * 8; // 8 hours

    /**
     * This is an optional attribute.
     * <p/>
     * Legal values are integers between 0 and Integer.MAX_VALUE.
     * <p/>
     * It is the number of seconds that an Element should live since it was created. Created means inserted into a cache using the Cache.put method.
     * <p/>
     * 0 has a special meaning, which is not to check the Element for time to live, i.e. it will live forever.
     * <p/>
     * The default value is 0.
     */
    private long timeToLiveSeconds = 60 * 60 * 24; // 1 day

    /**
     * This is an optional attribute.
     * <p/>
     * Legal values are "true" or "false".
     * <p/>
     * Whether or not the DiskStore should be persisted between CacheManager shutdowns and Virtual Machine restarts.
     */
    private boolean diskPersistent = false;

    /**
     * This is an optional attribute.
     * <p/>
     * Legal values are integers between 0 and Integer.MAX_VALUE.
     * <p/>
     * It is how long to the disk expiry thread should sleep between successive runs. Setting this value too low
     * could cause performance problems. A setting of 0 is not recommended. It will cause 100% cpu load.
     * <p/>
     * The default value is 120 seconds.
     */
    private long diskExpiryThreadIntervalSeconds = 1200;

    /**
     * Policy would be enforced upon reaching the maxElementsInMemory limit.
     * <p/>
     * Default policy is Least Recently Used (specified as LRU). Other policies available:
     * - First In First Out (specified as FIFO) and Least Frequently Used (specified as LFU)
     */
    private String memoryStoreEvictionPolicy = "FIFO";
    private String diskCachePath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxElementsInMemory() {
        return maxElementsInMemory;
    }

    public void setMaxElementsInMemory(int maxElementsInMemory) {
        this.maxElementsInMemory = maxElementsInMemory;
    }

    public boolean isEternal() {
        return eternal;
    }

    public void setEternal(boolean eternal) {
        this.eternal = eternal;
    }

    public boolean isOverflowToDisk() {
        return overflowToDisk;
    }

    public void setOverflowToDisk(boolean overflowToDisk) {
        this.overflowToDisk = overflowToDisk;
    }

    public long getTimeToIdleSeconds() {
        return timeToIdleSeconds;
    }

    public void setTimeToIdleSeconds(long timeToIdleSeconds) {
        this.timeToIdleSeconds = timeToIdleSeconds;
    }

    public long getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public void setTimeToLiveSeconds(long timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }

    public boolean isDiskPersistent() {
        return diskPersistent;
    }

    public void setDiskPersistent(boolean diskPersistent) {
        this.diskPersistent = diskPersistent;
    }

    public long getDiskExpiryThreadIntervalSeconds() {
        return diskExpiryThreadIntervalSeconds;
    }

    public void setDiskExpiryThreadIntervalSeconds(long diskExpiryThreadIntervalSeconds) {
        this.diskExpiryThreadIntervalSeconds = diskExpiryThreadIntervalSeconds;
    }

    public String getMemoryStoreEvictionPolicy() {
        return memoryStoreEvictionPolicy;
    }

    public void setMemoryStoreEvictionPolicy(String memoryStoreEvictionPolicy) {
        this.memoryStoreEvictionPolicy = memoryStoreEvictionPolicy;
    }

    public void setDiskCachePath(String diskCachePath) {
        this.diskCachePath = diskCachePath;
    }

    public String getDiskCachePath() {
        return diskCachePath;
    }
}
