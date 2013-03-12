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
 * Generic caching mechanism.
 */
public interface ObjectCache {

    /**
     * Gets the common name of the type of objects to cache.
     */
    public String getType();

    /**
     * Sets the common name of the type of objects to cache.
     */
    public void setType(String type);

    /**
     * Adds an object to the cache.
     */
    public void put(Serializable key,
                    Object object);

    /**
     * Gets an object from the cache by key, or returns null if that object is
     * not cached.
     */
    public Object get(Serializable key);

    /**
     * Clears an object from the cache by key.
     */
    public Object clear(Serializable key);

    /**
     * Clears the entire cache.
     */
    public void clearAll();
}