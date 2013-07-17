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
package org.jboss.dashboard.factory;

import java.util.Hashtable;
import java.util.Set;
import java.util.Iterator;

/**
 * Contains ComponentsStorage objects to resolve lookups.
 */
public class LookupHelper {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LookupHelper.class.getName());

    private Hashtable storages = new Hashtable();

    public void addStorageResolver(String scope, ComponentsStorage storage) {
        storages.put(scope, storage);
    }

    public Object lookupObject(String scope, String name) {
        ComponentsStorage storage = (ComponentsStorage) storages.get(scope);
        if (storage == null) {
            log.error("Cannot resolve components with scope " + scope);
            return null;
        } else {
            return storage.getComponent(name);
        }
    }

    public void storeObject(String scope, String name, Object instance) {
        ComponentsStorage storage = (ComponentsStorage) storages.get(scope);
        if (storage == null) {
            log.error("Cannot store components with scope " + scope);
        } else {
            storage.setComponent(name, instance);
        }
    }

    public void clear() {
        Set scopes = storages.keySet();
        for (Iterator iterator = scopes.iterator(); iterator.hasNext();) {
            String scope = (String) iterator.next();
            ComponentsStorage storage = (ComponentsStorage) storages.get(scope);
            storage.clear();
        }
        storages.clear();
    }

    public Object getSynchronizationObject(String scope) {
        ComponentsStorage storage = (ComponentsStorage) storages.get(scope);
        if (storage == null) {
            log.error("Cannot resolve components with scope " + scope);
            return "";
        } else {
            return storage.getSynchronizationObject();
        }
    }
}
