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

/**
 * Storage for global components.
 */
public class GlobalComponentsStorage implements ComponentsStorage {
    private static Hashtable components = new Hashtable();

    public void setComponent(String name, Object component) {
        components.put(name, component);
    }

    public Object getComponent(String name) {
        return components.get(name);
    }

    public void clear() {
        //Global components persist forever
    }

    public Object getSynchronizationObject() {
        return "globalScope".intern();
    }
}
