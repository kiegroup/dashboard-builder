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
package org.jboss.dashboard.ui.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractTree implements Tree {

    public AbstractTree() {
        super();
    }

    public abstract TreeNode[] getMainNodes();


    protected List getPathList(String path) {
        int position = 0;
        ArrayList pathList = new ArrayList();

        while (position != -1) {
            position = path.indexOf("/");
            if (position == -1) {
                pathList.add(path.substring(0));
            } else {
                pathList.add(path.substring(0, position));
                path = path.substring(position + 1);
            }
        }
        return pathList;
    }

    public List getRootNodes() {
        return Arrays.asList(getMainNodes());
    }

    public TreeNode getNodeByPath(String path) {
        int position = path.indexOf("/");
        if (position == -1) {
            return getSubnodeById(path);
        } else {
            String id = path.substring(0, position);
            TreeNode subchild = getSubnodeById(id);
            if (subchild != null)
                return subchild.getSubNodeByPath(path.substring(position + 1));
        }
        return null;

        //List pathList = getPathList(path);
        //return getNodeByPath(null, pathList);
    }

    protected TreeNode getSubnodeById(String id) {
        TreeNode[] mainNodes = getMainNodes();
        for (int i = 0; i < mainNodes.length; i++) {
            TreeNode subnode = mainNodes[i];
            if (subnode.getId().equals(id))
                return subnode;
        }
        return null;
    }

    /**
     * Get all TreeNodes given their path. If it doesn't exist, return empty array
     *
     * @param path Tree path to resolve
     * @return array with all nodes contained in this path
     */
    public TreeNode[] getNodesByPath(String path) {
        List pathList = getPathList(path);
        List treeNodes = new ArrayList();
        TreeNode previousNode = null;

        for (int i = 0; i < pathList.size(); i++) {
            List subList = pathList.subList(i, i + 1);
            previousNode = getNodeByPath(previousNode, subList);
            treeNodes.add(previousNode);
        }
        return (TreeNode[]) treeNodes.toArray(new TreeNode[treeNodes.size()]);
    }

    private TreeNode getNodeByPath(TreeNode parent, List pathList) {
        List baseNodes = new ArrayList();
        if (parent != null)
            baseNodes.addAll(parent.getChildren());
        else
            baseNodes.addAll(Arrays.asList(getMainNodes()));
        String firstPath = (String) pathList.get(0);
        for (int i = 0; i < baseNodes.size(); i++) {
            TreeNode node = (TreeNode) baseNodes.get(i);
            if (node.getId().equals(firstPath)) {
                if (pathList.size() == 1) {
                    return node;
                } else {
                    pathList.remove(0);
                    return getNodeByPath(node, pathList);
                }
            }
        }
        return null;
    }
}
