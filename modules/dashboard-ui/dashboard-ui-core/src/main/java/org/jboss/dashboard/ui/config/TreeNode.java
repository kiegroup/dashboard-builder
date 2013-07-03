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

import java.util.List;
import java.util.Locale;

public interface TreeNode {
    /**
     * @return the Node identifier, unique in the same level in the tree
     */
    String getId();

    /**
     * Node name
     *
     * @return The i18n node name
     */
    String getName(Locale l);

    /**
     * Node description
     *
     * @return The i18n node description
     */
    String getDescription(Locale l);

    /**
     * Icon identifier
     *
     * @return The icon identifier for this node
     */
    String getIconId();

    /**
     * The icon gallery name
     *
     * @return The icon gallery name
     */
    String getIconCategory();

    /**
     * URI of the JSP to include when showing this node
     *
     * @return the URI of the JSP to include when showing this node
     */
    String getEditURI();

    /**
     * Determines if edit URI is Ajax compatible
     *
     * @return true if edit uri can be loaded by ajax, false otherwise.
     */
    boolean isEditURIAjaxCompatible();

    /**
     * This node path, constructed by adding parent identifiers
     *
     * @return This node path.
     */
    String getPath();


    /**
     * Get a subnode by its path
     *
     * @param path
     * @return a subnode by its path, or null if it doesn't exist.
     */
    TreeNode getSubNodeByPath(String path);

    /**
     * This node deep level
     *
     * @return This node deep level
     */
    int getLevel();

    /**
     * Indicates if this node is the last one in his group.
     *
     * @return true if this node is the last one in his group.
     */
    boolean isLastChild();

    /**
     * @return A list of children TreeNodes
     */
    List getChildren();

    /**
     * Get the parent node if it exists
     *
     * @return the parent node if it exists, null otherwise
     */
    TreeNode getParent();//. Devuelve el nodo padre si existe.

    /**
     * @return The tree object where this TreeNode is contained
     */
    Tree getTree();

    /**
     * Called when the node is selected.
     *
     * @return true if the node can actually be selected, false otherwise
     */
    boolean onSelect();

    /**
     * Called when the node is unselected.
     *
     * @return true if the node can actually be unselected, false otherwise
     */
    boolean onUnselect();

    /**
     * Called when the node is edited.
     *
     * @return true if the node can actually be edited, false otherwise
     */
    boolean onEdit();

    /**
     * Called when the node is unedited.
     *
     * @return true if the node can actually be unedited, false otherwise
     */
    boolean onUnedit();

    /**
     * Called when the node is expanded.
     *
     * @return true if the node can actually be expanded, false otherwise
     */
    boolean onExpand();

    /**
     * Called when the node is collapse.
     *
     * @return true if the node can actually be collapsed, false otherwise
     */
    boolean onCollapse();

    /**
     * Determine if this node has no children
     *
     * @return true if node has no children
     */
    boolean isLeaf();

    /**
     * Determine if this node is selectable
     *
     * @return true if node can be selected
     */
    boolean isEditable();

    /**
     * Determine the first ancestor with given class name
     *
     * @param lookfortype class name to look for
     * @return the first ancestor with given class name
     */
    TreeNode getAncestor(String lookfortype);

    /**
     * Get ancestor for given level
     *
     * @param ancestorLevel
     * @return ancestor for given level
     */
    TreeNode getAncestorForLevel(int ancestorLevel);

    /**
     * @return true if node can be expanded
     */
    boolean isExpandible();
}

