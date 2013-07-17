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
 * Initializes thread context so that Component lookups can be performed.
 */
public class ComponentsContextManager {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ComponentsContextManager.class.getName());

    private static ThreadLocal thr = new ThreadLocal();
    private static Hashtable availableContexts = new Hashtable();

    static {
        availableContexts.put(Component.SCOPE_GLOBAL, new GlobalComponentsStorage());
        availableContexts.put(Component.SCOPE_VOLATILE, new VolatileComponentsStorage());
    }

    /**
     * Adds a component storage resolver
     *
     * @param scope   Scope to be resolved
     * @param storage Storage that will resolve the components
     */
    public static void addComponentStorage(String scope, ComponentsStorage storage) {
        availableContexts.put(scope, storage);
    }

    /**
     * Prepares current thread for a set of Factory operations.
     */
    public static void startContext() {
        if (thr.get() == null) {
            thr.set(createLookupHelper());
        } else {
            Exception e = new Exception();
            log.error("Error starting context. Context already started, and nested contexts are not supported.", e);
        }
    }

    /**
     * @return true if context is started.
     */
    public static boolean isContextStarted() {
        return thr.get() != null;
    }

    protected static LookupHelper createLookupHelper() {
        LookupHelper helper = new LookupHelper();
        Set contexts = availableContexts.keySet();
        for (Iterator iterator = contexts.iterator(); iterator.hasNext();) {
            String scope = (String) iterator.next();
            ComponentsStorage storage = (ComponentsStorage) availableContexts.get(scope);
            helper.addStorageResolver(scope, storage);
        }
        return helper;
    }

    public static LookupHelper getLookupHelper() {
        LookupHelper helper = (LookupHelper) thr.get();
        if (helper == null) {
            log.error("Cannot perform Factory operations outside a valid context. " +
                    "You must enclose all your Factory operations within a Factory.doWork() operation.");
        }
        return helper;
    }

    /**
     * Clears current Thread
     */
    public static void clearContext() {
        if (thr.get() != null) {
            LookupHelper helper = (LookupHelper) thr.get();
            helper.clear();
            thr.set(null);
        } else {
            log.error("Error clearing context. Context was not started.");
        }
    }
}
