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
import org.apache.commons.lang.ArrayUtils;

/**
 * Stores generic cache options, to be used by underlying cache provider (if applicable)
 */
public class CacheOptions extends BasicFactoryElement {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(CacheOptions.class.getName());

    private CacheElement defaultCacheElement;

    public CacheElement getDefaultCacheElement() {
        return defaultCacheElement;
    }

    public void setDefaultCacheElement(CacheElement defaultCacheElement) {
        this.defaultCacheElement = defaultCacheElement;
    }

    /**
     * Individual cache elements to create.
     */
    public CacheElement[] cacheElements;
    /**
     * Caches to create specifying only name, using default parameters
     */
    public String[] cacheNamesToCreate;

    /**
     * Return the configuration element for given cache name
     *
     * @param name
     * @return
     */
    public CacheElement getElement(String name) {
        for (CacheElement cacheElement : cacheElements) {
            if (cacheElement.getName().equals(name)) return cacheElement;
        }
        if (ArrayUtils.contains(cacheNamesToCreate, name)) {
            if (log.isDebugEnabled())
                log.debug("Cache \"" + name + "\" was lazily tuned, and may be undersized. Consider manual tuning.");
            return defaultCacheElement;
        }
        log.warn("Unable to find cache configuration for \"" + name + "\". " + "Using defaults. " +
                "You should create a Factory component similar to: \n\n" +
                "\t$class=" + CacheElement.class.getName() + "\n" +
                "\t$scope=global\n" +
                "\tname=" + name + "\n" +
                "\tmaxElementsInMemory=xxxx\n\n" +
                "and add it to the cacheElements property of org.jboss.dashboard.database.cache.CacheOptions component.");
        return defaultCacheElement;
    }
}
