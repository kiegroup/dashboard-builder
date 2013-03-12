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
package org.jboss.dashboard.ui.config;

import org.jboss.dashboard.ui.config.treeNodes.RootNode;

public class ConfigurationTree extends AbstractTree {

    private RootNode[] rootNodeArray;

    public RootNode[] getRootNodeArray() {
        return rootNodeArray;
    }

    public void setRootNodeArray(RootNode[] rootNodeArray) {
        this.rootNodeArray = rootNodeArray;
    }

    public void start() throws Exception {
        super.start();
        if (rootNodeArray != null) {
            for (int i = 0; i < rootNodeArray.length; i++) {
                RootNode rootNode = rootNodeArray[i];
                rootNode.setTree(this);
            }
        }
    }

    public TreeNode[] getMainNodes() {
        return rootNodeArray;
    }
}
