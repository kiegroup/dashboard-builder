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

import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.ui.components.MessagesComponentHandler;
import org.slf4j.Logger;

import java.util.*;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class DefaultTreeStatus implements TreeStatus {

    @Inject
    protected transient Logger log;

    @Inject @Config("true")
    protected boolean editImpliesExpand;

    @Inject @Config("false")
    protected boolean expandImpliesEdit;

    @Inject @Config("false")
    protected boolean allNodesOpen;

    protected Set expandedPaths = new HashSet();
    protected Set selectedPaths = new HashSet();
    protected final Set editedPaths = new HashSet();

    protected String[] initiallyExpandedPaths = {"root"};
    protected String[] initiallyEditedPaths = {"root"};

    public MessagesComponentHandler getMessagesComponentHandler() {
        return MessagesComponentHandler.lookup();
    }

    protected Set getEditedPaths() {
        return editedPaths;
    }

    protected Set getExpandedPaths() {
        return expandedPaths;
    }

    protected boolean isAllNodesOpen() {
        return allNodesOpen;
    }

    protected Set getSelectedPaths() {
        return selectedPaths;
    }

    public boolean isEditImpliesExpand() {
        return editImpliesExpand;
    }

    public void setEditImpliesExpand(boolean editImpliesExpand) {
        this.editImpliesExpand = editImpliesExpand;
    }

    public boolean isExpandImpliesEdit() {
        return expandImpliesEdit;
    }

    public void setExpandImpliesEdit(boolean expandImpliesEdit) {
        this.expandImpliesEdit = expandImpliesEdit;
    }

    public String[] getInitiallyExpandedPaths() {
        return initiallyExpandedPaths;
    }

    public void setInitiallyExpandedPaths(String[] initiallyExpandedPaths) {
        this.initiallyExpandedPaths = initiallyExpandedPaths;
    }

    public String[] getInitiallyEditedPaths() {
        return initiallyEditedPaths;
    }

    public void setInitiallyEditedPaths(String[] initiallyEditedPaths) {
        this.initiallyEditedPaths = initiallyEditedPaths;
    }

    @PostConstruct
    protected void start() {
        if (initiallyExpandedPaths != null)
            expandedPaths.addAll(Arrays.asList(initiallyExpandedPaths));
        if (initiallyEditedPaths != null)
            editedPaths.addAll(Arrays.asList(initiallyEditedPaths));
    }

    public boolean isExpanded(TreeNode node) {
        String path = node.getPath();
        return allNodesOpen || expandedPaths.contains(path);
    }

    public boolean isSelected(TreeNode node) {
        return selectedPaths.contains(node.getPath());
    }

    public boolean isEdited(TreeNode node) {
        return editedPaths.contains(node.getPath());
    }


    public boolean select(TreeNode node) {
        boolean added = selectedPaths.add(node.getPath());
        if (added) {
            return node.onSelect();
        }
        return false;
    }

    public boolean unselect(TreeNode node) {
        boolean removed = selectedPaths.remove(node.getPath());
        if (removed) {
            return node.onUnselect();
        }
        return false;
    }

    public synchronized boolean edit(TreeNode node) {
        // If node is not selectable, get first selectable child
        boolean editingFirstChild = false;
        if (!node.isEditable()) {
            //if (node.isExpandible()) expand(node);
            List children = node.getChildren();
            for (int i = 0; i < children.size(); i++) {
                TreeNode treeNode = (TreeNode) children.get(i);
                if (treeNode.isEditable()) {
                    node = treeNode;
                    editingFirstChild = true;
                    break;
                }
            }
        }
        if (!node.isEditable()) return false;

        boolean added = editedPaths.add(node.getPath());
        boolean edited = false;
        if (added) {
            getMessagesComponentHandler().clearAll();
            node.onEdit();
            synchronized (editedPaths) {
                for (Iterator iterator = editedPaths.iterator(); iterator.hasNext();) {
                    String path = (String) iterator.next();
                    if (!path.equals(node.getPath())) { //Remove any other previously edited node
                        TreeNode currentlyEditedNode = node.getTree().getNodeByPath(path);
                        if (currentlyEditedNode == null) {
                            log.warn("Invalid node path: " + path);
                        } else {
                            currentlyEditedNode.onUnedit();
                        }
                        iterator.remove();
                    }
                } //Guarantee that only one item is edited at the same time.
                //edited = edit(node);
            }
        }
        if (editImpliesExpand && !node.isLeaf() && !editingFirstChild) {
            expand(node);
        }
        return edited;
    }

    public TreeNode getLastEditedNode(Tree tree) {
        TreeNode node = null;
        if (editedPaths != null && editedPaths.size() == 1) {
            node = tree.getNodeByPath((String) editedPaths.iterator().next());
        }
        return (TreeNode) (node == null ? tree.getRootNodes().get(0) : node);
    }

    public boolean unedit(TreeNode node) {
        boolean removed = editedPaths.remove(node.getPath());
        if (removed) {
            return node.onUnedit();
        }
        return false;
    }

    public boolean expand(TreeNode node) {
        boolean added = expandedPaths.add(node.getPath());
        boolean expanded = false;
        if (added) {
            expanded = node.onExpand();
            if (expandImpliesEdit) {
                edit(node);
            }
        }
        return expanded;
    }

    public boolean collapse(TreeNode node) {
        boolean removed = expandedPaths.remove(node.getPath());
        if (removed) {
            return node.onCollapse();
        }
        return false;
    }


    // Memento pattern.
    public Object getMemento() throws Exception {
        Map memento = new HashMap();
        memento.put("editimpliesexpand", (editImpliesExpand) ? Boolean.TRUE : Boolean.FALSE);
        memento.put("expandimpliesedit", (expandImpliesEdit) ? Boolean.TRUE : Boolean.FALSE);
        memento.put("editedpaths", new HashSet(editedPaths));
        memento.put("expandedpaths", new HashSet(expandedPaths));
        memento.put("selectedpaths", new HashSet(selectedPaths));
        return memento;
    }

    public void setMemento(Object obj) throws Exception {
        Map memento = (Map) obj;
        editImpliesExpand = ((Boolean) memento.get("editimpliesexpand")).booleanValue();
        expandImpliesEdit = ((Boolean) memento.get("expandimpliesedit")).booleanValue();
        editedPaths.clear();
        editedPaths.addAll((Set) memento.get("editedpaths"));
        expandedPaths = (Set) memento.get("expandedpaths");
        selectedPaths = (Set) memento.get("selectedpaths");
    }

    /**
     * Clear the treeStatus
     */
    public void clear() {
        expandedPaths.clear();
        selectedPaths.clear();
        editedPaths.clear();
    }

    public void navigateToPath(Tree tree, String path) {
        clear();
        TreeNode[] nodes = tree.getNodesByPath(path);
        for (int i = 0; i < nodes.length; i++) {
            TreeNode node = nodes[i];
            if (node != null) {
                /*if (i == nodes.length - 1) edit(node);
                else expand(node);*/
                if (node.isExpandible()) expand(node);
                else
                    edit(node);
            } else {
                log.warn("Null node " + i + " resolving path " + path);
            }
        }
    }
}
