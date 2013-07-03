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
package org.jboss.dashboard.ui.config.treeNodes;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.factory.Component;
import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.ui.config.AbstractNode;

import java.util.*;

public class FactoryFolderNode extends FactoryNode {

    private Map mappings;

    public Map getMappings() {
        return mappings;
    }

    public void setMappings(Map mappings) {
        this.mappings = mappings;
    }

    public String getId() {
        return getNodeName();
    }

    protected List listChildren() {
        List list = new ArrayList();
        Map mappings = getMappings();
        TreeSet keys = new TreeSet(mappings.keySet());
        for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            Object val = mappings.get(key);
            list.add(getNewSubNode(key, val));
        }
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                if (o1.getClass().equals(o2.getClass())) {
                    FactoryNode node1 = (FactoryNode) o1;
                    FactoryNode node2 = (FactoryNode) o2;
                    return node1.getNodeName().compareTo(node2.getNodeName());
                }
                return o2.getClass().getName().compareTo(o1.getClass().getName());
            }
        });
        return list;
    }

    protected AbstractNode getNewSubNode(String name, Object value) {
        FactoryNode node;
        if (value instanceof Map) {
            node = (FactoryFolderNode) Factory.lookup(FactoryFolderNode.class.getName());
            ((FactoryFolderNode) node).setMappings((Map) value);
        } else {
            node = (FactoryComponentNode) Factory.lookup(FactoryComponentNode.class.getName());
            ((FactoryComponentNode) node).setComponent((Component) value);
        }
        if (getNodeName() != null)
            node.setNodeName(this.getNodeName() + "." + name);
        else
            node.setNodeName(name);
        node.setTree(getTree());
        node.setParent(this);
        return node;
    }


    public boolean isEditable() {
        return false;
    }

    public String getName(Locale l) {
        String nodeShortName = getNodeName();
        int lastIndex = nodeShortName.lastIndexOf(".");
        if (lastIndex != -1) nodeShortName = nodeShortName.substring(1 + lastIndex);
        return nodeShortName;
    }

    public String getDescription(Locale l) {
        return getNodeName();
    }
}