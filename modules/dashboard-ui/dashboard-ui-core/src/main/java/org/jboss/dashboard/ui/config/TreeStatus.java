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

public interface TreeStatus {
    /**
     * Indicates if a node is currently expanded
     *
     * @param t treenode to check
     * @return true if it is expanded
     */
    boolean isExpanded(TreeNode t);

    /**
     * Indicates if a node is currently selected
     *
     * @param t treenode to check
     * @return true if it is selected
     */
    boolean isSelected(TreeNode t);

    /**
     * Indicates if a node is currently expanded
     *
     * @param t treenode to check
     * @return true if it is expanded
     */
    boolean isEdited(TreeNode t);

    /**
     * Mark given node as selected
     *
     * @param t treenode to check
     * @return true if select succeeds
     */
    boolean select(TreeNode t);

    /**
     * Mark given node as unselected
     *
     * @param t treenode to unselect
     * @return true if unselect succeeds
     */
    boolean unselect(TreeNode t);

    /**
     * Mark given node as edited
     *
     * @param t treenode to edit
     * @return true if edit succeeds
     */
    boolean edit(TreeNode t);

    /**
     * Mark given node as unedited
     *
     * @param t treenode to unedit
     * @return true if unedit succeeds
     */
    boolean unedit(TreeNode t);

    /**
     * Mark given node as expanded
     *
     * @param t treenode to expand
     * @return true if expand succeeds
     */
    boolean expand(TreeNode t);

    /**
     * Mark given node as collapsed
     *
     * @param t treenode to collapse
     * @return true if collapse succeeds
     */
    boolean collapse(TreeNode t);

    /**
     * Get a memento for this object
     *
     * @return
     * @throws Exception
     */
    Object getMemento() throws Exception;

    /**
     * Set a memento for this object
     *
     * @param obj
     * @throws Exception
     */
    void setMemento(Object obj) throws Exception;

    /**
     * Get the edited node.
     *
     * @param tree Tree where the node must belong to
     * @return The edited node, or null if there isn't any.
     */
    TreeNode getLastEditedNode(Tree tree);

    /**
     * Clear the treeStatus
     */
    void clear();

    public void navigateToPath(Tree tree, String path);
}
