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
import org.apache.commons.lang.StringUtils;

import java.util.Properties;

public class CacheConfigurationManager extends BasicFactoryElement {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(CacheConfigurationManager.class.getName());

    private Properties cacheConfigurationGenerators = new Properties();
    private Properties hibernateProperties;
    private CacheOptions cacheOptions;
    private CacheConfigurationGenerator configGenerator;

    public CacheConfigurationGenerator getConfigGenerator() {
        return configGenerator;
    }

    public void setConfigGenerator(CacheConfigurationGenerator configGenerator) {
        this.configGenerator = configGenerator;
    }

    public Properties getCacheConfigurationGenerators() {
        return cacheConfigurationGenerators;
    }

    public void setCacheConfigurationGenerators(Properties cacheConfigurationGenerators) {
        this.cacheConfigurationGenerators = cacheConfigurationGenerators;
    }

    public CacheOptions getCacheOptions() {
        return cacheOptions;
    }

    public void setCacheOptions(CacheOptions cacheOptions) {
        this.cacheOptions = cacheOptions;
    }

    public Properties getHibernateProperties() {
        return hibernateProperties;
    }

    public void setHibernateProperties(Properties hibernateProperties) {
        this.hibernateProperties = hibernateProperties;
    }

    /**
     * Starts the cache configuration
     */
    public void initializeCaches() {
        String use2ndLevelCache = hibernateProperties.getProperty("hibernate.cache.use_second_level_cache");
        if ("true".equals(use2ndLevelCache)) {
            String cacheProviderFullName = hibernateProperties.getProperty("hibernate.cache.provider_class", hibernateProperties.getProperty("hibernate.cache.region.factory_class"));
            if (!StringUtils.isEmpty(cacheProviderFullName)) {
                int lastDot = cacheProviderFullName.lastIndexOf('.');
                if (lastDot > 0) {
                    String providerName = cacheProviderFullName.substring(lastDot + 1);
                    String cacheGeneratorPath = cacheConfigurationGenerators.getProperty(providerName);
                    if (StringUtils.isEmpty(cacheGeneratorPath)) {
                        log.warn("No cache config generator for " + providerName);
                    } else {
                        configGenerator = (CacheConfigurationGenerator) factoryLookup(cacheGeneratorPath);
                        configGenerator.initializeCacheConfig(this);
                    }
                }
            } else {
                log.warn("Second level cache activated but no hibernate.cache.provider_class or hibernate.cache.region.factory_class parameter specified.");
            }
        }
    }

    /**
     * Create a custom cache region for an existing cache
     *
     * @param cacheRegionName cache name to create
     * @return true if the urnderlying cache supported cache creation
     */
    public boolean createCustomCacheRegion(String cacheRegionName) {
        return configGenerator != null && configGenerator.createCustomCacheRegion(cacheRegionName);
    }

    /**
     * Clears all objects stored in the cache
     *
     * @return true if it is supported
     */
    public boolean freeAllCache() {
        return configGenerator.freeAllCache();
    }


}
