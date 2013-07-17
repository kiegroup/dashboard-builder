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
package org.jboss.dashboard.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.dashboard.factory.BasicFactoryElement;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.security.Permission;
import java.util.HashMap;
import java.util.Map;

@SessionScoped
public class SecurityCache implements Serializable {

    private static transient Logger log = LoggerFactory.getLogger(SecurityCache.class.getName());

    private boolean cacheEnabled = true;
    private Map cacheMappings = new HashMap();

    private static long cacheReads = 0;
    private static long cacheHits = 0;

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public Boolean getValue(Permission perm, String action) {
        cacheReads++;
        Map m = (Map) cacheMappings.get(perm);
        Boolean b = null;
        if (m == null) {
            if (log.isDebugEnabled()) log.debug("Security cache miss: " + perm);
        } else {
            b = (Boolean) m.get(action);
            if (b == null) {
                if (log.isDebugEnabled()) log.debug("Security cache miss: " + perm);
            } else {
                cacheHits++;
                if (log.isDebugEnabled()) log.debug("Security cache hit: " + perm);
            }
        }
        if (log.isDebugEnabled())
            log.debug("Cache success rate: " + (cacheHits * 100 / cacheReads) + "%");
        return b;
    }

    public void setValue(Permission perm, String action, boolean b) {
        Map m = (Map) cacheMappings.get(perm);
        if (m == null) cacheMappings.put(perm, m = new HashMap());
        m.put(action, (b) ? Boolean.TRUE : Boolean.FALSE);
    }

    public void clear() {
        if (log.isDebugEnabled()) {
            log.debug("Clearing security cache.");
        }
        cacheMappings.clear();
    }

    public static long getCacheHits() {
        return cacheHits;
    }

    public static long getCacheReads() {
        return cacheReads;
    }
}
