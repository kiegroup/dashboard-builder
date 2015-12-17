/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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

import java.util.*;

/**
 *
 */
public class PanelSecurity {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PanelSecurity.class.getName());

    /**
     * Stores method names pointing to HashTables of Permission objects.
     */
    protected Hashtable securityData;

    public PanelSecurity() {
        securityData = new Hashtable();
    }

    /**
     * Add a restriction for a method name.
     *
     * @param methodName
     * @param permissionClass
     * @param action
     */
    public synchronized void addMethodPermission(String methodName, Class permissionClass, String action) {
        addMethodPermission(securityData, methodName, permissionClass, action);
    }

    protected void addMethodPermission(Hashtable secData, String methodName, Class permissionClass, String action) {
        Hashtable h = (Hashtable) secData.get(methodName);
        if (h == null) {
            h = new Hashtable();
            secData.put(methodName, h);
        }
        Set s = (Set) h.get(permissionClass);
        if (s == null) {
            s = new HashSet();
            h.put(permissionClass, s);
        }
        s.add(action);
    }

    /**
     * Remove a method permission(s).<br>
     * <p/>
     * <li>removeMethodPermission("actionStart",PanelPermission.class,null). Removes all PanelPermission for method
     * named actionStart.
     * <li>removeMethodPermission(null,PanelPermission.class,"view"). Removes all view permissions for any method.
     *
     * @param methodName      Method whose permission must be removed. Set to null to remove all.
     * @param permissionClass Permission to remove. Set to null to remove all.
     * @param action          Action to remove. Set to null to remove all.
     */
    public synchronized void removeMethodPermission(String methodName, Class permissionClass, String action) {
        Hashtable newSecurityData = new Hashtable();

        for (Enumeration enMethods = securityData.keys(); enMethods.hasMoreElements();) {
            String existingMethodName = (String) enMethods.nextElement();
            Hashtable h = (Hashtable) securityData.get(existingMethodName);
            for (Enumeration enPerms = h.keys(); enPerms.hasMoreElements();) {
                Class existingPermissionClass = (Class) enPerms.nextElement();
                Set s = (Set) h.get(existingPermissionClass);
                for (Iterator it = s.iterator(); it.hasNext();) {
                    String existingAction = (String) it.next();
                    //Check if existingMethodName, existingPermissionClass, existingAction has to be deleted.
                    if (
                            (methodName == null || existingMethodName.equals(methodName)) &&
                                    (permissionClass == null || existingPermissionClass.equals(permissionClass)) &&
                                    (action == null || existingAction.equals(action))
                            )
                        continue;
                    addMethodPermission(newSecurityData, existingMethodName, existingPermissionClass, existingAction);
                }
            }
        }

        securityData = newSecurityData;
    }

    public String toString() {
        return entrySet().toString();
    }

    public class PanelSecurityEntry {
        private String methodName;
        private Class permissionClass;
        private String action;

        protected PanelSecurityEntry(String methodName, Class permissionClass, String action) {
            this.methodName = methodName;
            this.permissionClass = permissionClass;
            this.action = action;
        }

        public String getMethodName() {
            return methodName;
        }

        public Class getPermissionClass() {
            return permissionClass;
        }

        public String getAction() {
            return action;
        }

        public String toString() {
            return methodName + ": " + permissionClass.getName() + " [" + action + "]";
        }
    }

    public Set entrySet() {
        Set entries = new HashSet();
        for (Enumeration enMethods = securityData.keys(); enMethods.hasMoreElements();) {
            String existingMethodName = (String) enMethods.nextElement();
            Hashtable h = (Hashtable) securityData.get(existingMethodName);
            for (Enumeration enPerms = h.keys(); enPerms.hasMoreElements();) {
                Class existingPermissionClass = (Class) enPerms.nextElement();
                Set s = (Set) h.get(existingPermissionClass);
                for (Iterator it = s.iterator(); it.hasNext();) {
                    String existingAction = (String) it.next();
                    entries.add(new PanelSecurityEntry(existingMethodName, existingPermissionClass, existingAction));
                }
            }
        }
        return Collections.unmodifiableSet(entries);
    }

    public Set entrySet(String methodName) {
        Set entries = new HashSet();
        for (Iterator it = entrySet().iterator(); it.hasNext();) {
            PanelSecurityEntry entry = (PanelSecurityEntry) it.next();
            if (entry.getMethodName().equals(methodName))
                entries.add(entry);
        }
        return Collections.unmodifiableSet(entries);
    }

}

