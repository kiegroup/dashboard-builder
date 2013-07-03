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

import org.jboss.dashboard.factory.Component;
import org.jboss.dashboard.ui.config.components.factory.FactoryComponentHandler;

import java.util.Locale;

public class FactoryComponentNode extends FactoryNode {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FactoryComponentNode.class.getName());

    private Component component;
    private FactoryComponentHandler factoryComponentHandler;

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public FactoryComponentHandler getFactoryComponentHandler() {
        return factoryComponentHandler;
    }

    public void setFactoryComponentHandler(FactoryComponentHandler factoryComponentHandler) {
        this.factoryComponentHandler = factoryComponentHandler;
    }

    public String getId() {
        return getNodeName();
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

    /*
    protected String getFullComponentName(){
        String path = getPath();
        path = path.substring(path.indexOf("/"));
        return path;
    } */

    public boolean onEdit() {
        getFactoryComponentHandler().setFactoryComponentName(getNodeName());
        return true;
    }
}
