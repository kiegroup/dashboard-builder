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
package org.jboss.dashboard.database.cache.providers;

import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.database.cache.custom.*;
import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.CacheProvider;

import java.util.Properties;

/**
 * Support for CustomCache replicated cache. CustomCache does not support
 * locking, so strict "read-write" semantics are unsupported.
 *
 */
public class CustomCacheProvider implements CacheProvider {

    private CacheFactory factory;

    public Cache buildCache(String regionName, Properties properties) throws CacheException {
        ObjectCache cache = factory.createCache(regionName);
        if (cache == null) {
            throw new CacheException("CustomCache did not create a cache: " + regionName);
        }
        return new CustomCache(cache, regionName);
    }

    public long nextTimestamp() {
        return System.currentTimeMillis() / 100;
    }

    /**
     * Callback to perform any necessary initialization of the underlying cache implementation
     * during SessionFactory construction.
     *
     * @param properties current configuration settings.
     */
    public void start(Properties properties) throws CacheException {
        factory = (CacheFactory) Factory.lookup("org.jboss.dashboard.database.cache.custom.CacheFactory");
    }

    /**
     * Callback to perform any necessary cleanup of the underlying cache implementation
     * during SessionFactory.close().
     */
    public void stop() {
    }

    public boolean isMinimalPutsEnabledByDefault() {
        return true;
    }

}