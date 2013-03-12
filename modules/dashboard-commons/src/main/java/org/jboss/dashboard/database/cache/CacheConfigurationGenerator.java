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
 * This component creates the xml cache configuration for a given type of cache provider. Some managers may
 * configure the cache programmatically, some others may generate an xml and place it in a path to be read.
 */
public abstract class CacheConfigurationGenerator extends BasicFactoryElement {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(CacheConfigurationGenerator.class.getName());

    /**
     * Create the cache configuration, either by generating an XML and placing it in the correct place or
     * by programmatically configuring whatever is needed.
     *
     * @param cacheConfigurationManager The generic configuration manager where detailed cache options might be configured.
     */
    public abstract void initializeCacheConfig(CacheConfigurationManager cacheConfigurationManager);


    /**
     * Determine the type of cache usage to use
     *
     * @return
     */
    public abstract String getCacheUsage();


    public boolean createCustomCacheRegion(String cacheRegionName) {
        return false;
        //TODO overwrite this in providers that support it
    }

    public boolean freeAllCache() {
        return false;
        //TODO overwrite this in providers that support it
    }
}
