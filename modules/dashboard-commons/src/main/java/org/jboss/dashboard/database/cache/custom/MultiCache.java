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

import java.io.Serializable;

/**
 * A wrapper cache type that notifies the multicast cache manager.
 */
public class MultiCache implements ObjectCache {

    /**
     * The underlying cache
     */
    private ObjectCache cache;

    /**
     * The cache manager.
     */
    private MultiCacheManager manager;

    public MultiCache(ObjectCache cache, MultiCacheManager manager) {
        this.cache = cache;
        this.manager = manager;
    }

    public String getType() {
        return cache.getType();
    }

    public void setType(String type) {
        cache.setType(type);
        // Register this type with the manager
        manager.addCache(this);
    }

    public void put(Serializable key, Object object) {
        if (cache.get(key) != null) {
            clear(key);
        }
        cache.put(key, object);
    }

    public Object get(Serializable key) {
        return cache.get(key);
    }

    public Object clear(Serializable key) {
        Object returnValue = cache.clear(key);
        return returnValue;
    }

    public void clearAll() {
        cache.clearAll();
    }

    public void doClearAll() {
        cache.clearAll();
    }
}