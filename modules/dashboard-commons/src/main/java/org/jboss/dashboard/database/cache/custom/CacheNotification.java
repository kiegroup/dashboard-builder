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
 * The actual object that gets sent to the cluster to indicate that an object
 * needs to be cleared from the cache.
 */
public class CacheNotification implements Serializable {

    private String type;
    private Serializable key;

    public CacheNotification() {
    }

    public CacheNotification(String type,
                             Serializable key) {
        this.type = type;
        this.key = key;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setKey(Serializable key) {
        this.key = key;
    }

    public Serializable getKey() {
        return key;
    }
}