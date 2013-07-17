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

import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * The component tree contains all the components definitions loaded (usually) from the etc/factory
 * directory.
 */
public class ComponentsTree {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ComponentsTree.class.getName());
    private List configurationFiles;
    private Map mappings;
    private Map treeMappings;
    private Map aliases;
    private Map reverseAliases;
    private long componentOrderCounter = 0;

    /**
     * @return the next value of the sequence that calculates the order of global components
     */
    public synchronized long getNewOrderCounter() {
        return componentOrderCounter++;
    }

    /**
     * Creates a new tree of components for the given list of files.
     *
     * @param files a List of Files with descriptors.
     */
    public ComponentsTree(List files) {
        configurationFiles = files;
        mappings = new TreeMap();
        aliases = new TreeMap();
        reverseAliases = new TreeMap();
        treeMappings = new TreeMap();
        init();
    }

    protected void init() {
        for (int i = 0; i < configurationFiles.size(); i++) {
            DescriptorFile file = (DescriptorFile) configurationFiles.get(i);
            log.debug("Loading properties file " + file.getSource());
            Properties prop = file.getMappedProperties();
            Component component = (Component) mappings.get(file.getComponentName());
            if (component == null) {
                component = makeComponentInstance(file.getComponentName());
            }
            component.addProperties(prop, file.getSource());
            addToMappings(file.getComponentName(), component);
            /*if (log.isDebugEnabled()) {
                log.debug("Mappings after loading " + file.getSource() + " are " + mappings);
            } */
        }
        for (Iterator it = mappings.keySet().iterator(); it.hasNext();) {
            Component component = (Component) mappings.get(it.next());
            component.validate();
        }
    }

    protected void addToMappings(String name, Component component) {
        mappings.put(name, component);
        if (!StringUtils.isEmpty(component.getAlias())) {
            String previousValue = (String) aliases.get(component.getAlias());
            if (previousValue != null && !previousValue.equals(name)) {
                log.error("Cannot use alias " + component.getAlias() +
                        " for component " + name + ". There is another component for this alias: " +
                        previousValue);
            } else {
                aliases.put(component.getAlias(), name);
                reverseAliases.put(name, component.getAlias());
            }
        }

        Map currentLevel = treeMappings;
        StringTokenizer stk = new StringTokenizer(name, ".");
        while (stk.hasMoreTokens()) {
            String token = stk.nextToken();
            if (stk.hasMoreTokens()) {
                Map level = (Map) currentLevel.get(token);
                if (level == null) {
                    level = new HashMap();
                    currentLevel.put(token, level);
                }
                currentLevel = level;
            } else {
                currentLevel.put(token, component);
            }
        }
    }

    public Component getComponent(String path) {
        return getComponent(path, null);
    }

    public Component getComponent(String path, String relativePath) {
        if (relativePath != null && path.startsWith(".")) {
            path = calculateRelativePath(path, relativePath);
        }
        String alias = (String) aliases.get(path);
        if (alias != null) path = alias;
        return (Component) mappings.get(path);
    }

    protected String calculateRelativePath(String parameter, String currentPath) {
        int initialDotsNum; //Number of starting dots in parameter
        for (initialDotsNum = 0; initialDotsNum < parameter.length() && parameter.charAt(initialDotsNum) == '.'; initialDotsNum++)
            ;
        int lastDotPosition;
        int initialDotsNumCopy = initialDotsNum;
        for (lastDotPosition = currentPath.length() - 1; lastDotPosition > 0 && initialDotsNumCopy > 0; lastDotPosition--) {
            if (currentPath.charAt(lastDotPosition) == '.') initialDotsNumCopy--;
        }
        if (lastDotPosition <= 0) {
            log.error("Cannot calculate path of " + parameter + " relative to " + currentPath);
            return parameter;
        }
        return currentPath.substring(0, lastDotPosition + 1) + parameter.substring(initialDotsNum - 1);
    }

    public Object lookup(String s) throws LookupException {
        return lookup(s, null);
    }


    public Object lookup(String s, String relativeToPath) throws LookupException {
        if (log.isDebugEnabled())
            log.debug("Looking up path " + s);
        Component component = getComponent(s, relativeToPath);
        Object obj = null;
        if (component != null) obj = component.getObject();
        return obj;
    }

    protected Component makeComponentInstance(String name) {
        return new Component(name, this);
    }

    public String toString() {
        return mappings.toString();
    }

    public Map getTreeMappings() {
        return treeMappings;
    }

    public String getAlias(String path, String relativePath) {
        if (relativePath != null && path.startsWith(".")) {
            path = calculateRelativePath(path, relativePath);
        }
        return (String) reverseAliases.get(path);
    }

    public void destroy() {
        // Stop components in reverse order of which they have been created.
        TreeSet sortedSet = new TreeSet(new Comparator() {
            public int compare(Object o1, Object o2) {
                Component c1 = (Component) o1;
                Component c2 = (Component) o2;
                return -new Long(c1.getCreationOrderNumber()).compareTo(new Long(c2.getCreationOrderNumber()));
            }
        });

        for (Iterator it = mappings.keySet().iterator(); it.hasNext();) {
            String key = (String) it.next();
            Component comp = (Component) mappings.get(key);
            if (Component.SCOPE_GLOBAL.equals(comp.getScope())) {
                Object object = null;
                try {
                    object = comp.getTheInstance();
                } catch (LookupException e) {
                    log.error("Error: ", e);
                }
                if (object != null && object instanceof FactoryLifecycle) {
                    sortedSet.add(comp);
                }
            }
        }

        for (Iterator iterator = sortedSet.iterator(); iterator.hasNext();) {
            Component component = (Component) iterator.next();
            Object object = null;
            try {
                object = component.getTheInstance();
            } catch (LookupException e) {
                log.error("Error: ", e);
            }
            if (object != null && object instanceof FactoryLifecycle) {
                FactoryLifecycle ob = (FactoryLifecycle) object;
                try {
                    ob.shutdown();
                } catch (Exception e) {
                    log.error("Error shutting down " + component.getName() + ": ", e);
                }
            }
        }


    }
}
