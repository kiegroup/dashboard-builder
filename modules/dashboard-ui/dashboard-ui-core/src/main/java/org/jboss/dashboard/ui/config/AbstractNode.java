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

import org.jboss.dashboard.factory.BasicFactoryElement;
import org.jboss.dashboard.factory.Factory;

import java.util.*;

public abstract class AbstractNode extends BasicFactoryElement implements TreeNode {

    private TreeNode[] subnodes;
    private TreeNode parent;
    private Tree tree;
    private String iconId = null;
    private String iconCategory = "config";
    private String editURI = "/configuration/tree/editPages/" + this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1) + "Edit.jsp";
    private boolean editURIAjaxCompatible = true;
    private String getUriType = "JSP";
    private boolean last = false;
    private boolean editable = true;
    private boolean expandible = true;
    private Properties nodeAttributes = (Properties) Factory.lookup("org.jboss.dashboard.ui.config.treeNodes.NodeAttributes");

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean b) {
        this.editable = b;
    }

    public boolean isExpandible() {
        return expandible;
    }

    public void setExpandible(boolean expandible) {
        this.expandible = expandible;
    }

    public boolean isEditURIAjaxCompatible() {                  
        return editURIAjaxCompatible;
    }

    public void setEditURIAjaxCompatible(boolean editURIAjaxCompatible) {
        this.editURIAjaxCompatible = editURIAjaxCompatible;
    }

    public Map getName() {
        return getI18nPropertiesMap("name");
    }

    public Map getDescription() {
        return getI18nPropertiesMap("description");
    }

    protected Map getI18nPropertiesMap(String name) {
        Properties m = new Properties();
        String propertyPreffix = getClass().getName() + "." + name + ".";
        for (Enumeration en = nodeAttributes.keys(); en.hasMoreElements();) {
            String propName = (String) en.nextElement();
            if (propName.startsWith(propertyPreffix)) {
                String lang = propName.substring(propertyPreffix.length());
                m.setProperty(lang, nodeAttributes.getProperty(propName));
            }
        }
        return m;
    }

    public String getPath() {
        TreeNode parent = getParent();
        return parent == null ? getId() : parent.getPath() + "/" + getId();
    }

    public TreeNode getSubNodeByPath(String path) {
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
    }

    protected TreeNode getSubnodeById(String id) {
        if (subnodes != null)
            for (int i = 0; i < subnodes.length; i++) {
                TreeNode subnode = subnodes[i];
                if (subnode.getId().equals(id))
                    return subnode;
            }
        return listChildrenById(id);
    }

    public int getLevel() {
        int level = 0;
        TreeNode parent = this.getParent();
        while (parent != null) {
            level++;
            parent = parent.getParent();
        }
        return level;
    }


    public boolean isLastChild() {
        return last;
    }

    protected void setLast(boolean b) {
        last = b;
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    public String getIconCategory() {
        return iconCategory;
    }

    public void setIconCategory(String iconCategory) {
        this.iconCategory = iconCategory;
    }

    public String getEditURI() {
        return editURI;
    }

    public void setEditURI(String editURI) {
        this.editURI = editURI;
    }

    public String getGetUriType() {
        return getUriType;
    }

    public void setGetUriType(String getUriType) {
        this.getUriType = getUriType;
    }

    public TreeNode[] getSubnodes() {
        return subnodes;
    }

    public void setSubnodes(TreeNode[] subnodes) {
        this.subnodes = subnodes;
        if (subnodes != null)
            for (int i = 0; i < subnodes.length; i++) {
                AbstractNode subnode = (AbstractNode) subnodes[i];
                subnode.setParent(this);
            }
    }

    public TreeNode getParent() {
        return parent;
    }

    public Tree getTree() {
        return tree;
    }

    public boolean isLeaf() {
        // Node is a leaf if has no children.
        if (!isExpandible()) return true;
        if (subnodes != null && subnodes.length > 0) return false;
        return !hasDynamicChildren();
    }

    public void setTree(Tree tree) {
        this.tree = tree;
        if (subnodes != null)
            for (int i = 0; i < subnodes.length; i++) {
                AbstractNode subnode = (AbstractNode) subnodes[i];
                subnode.setTree(tree);
            }

    }

    public boolean onSelect() {
        return true;
    }

    public boolean onUnselect() {
        return true;
    }

    public boolean onEdit() {
        return true;
    }

    public boolean onUnedit() {
        return true;
    }

    public boolean onExpand() {
        return true;
    }

    public boolean onCollapse() {
        return true;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public final List getChildren() {
        List children = new ArrayList();
        if (subnodes != null && subnodes.length > 0) {
            children.addAll(Arrays.asList(subnodes));
        }
        List dynamicChildren = listChildren();
        if (dynamicChildren != null)
            children.addAll(dynamicChildren);
        int max = children.size();
        for (int i = 0; i < max; i++) {
            AbstractNode node = (AbstractNode) children.get(i);
            node.setLast(i == max - 1);
        }
        return children;
    }


    public TreeNode getAncestor(String ancestorNodeClassName) {
        TreeNode workingNode = this.getParent();
        if (workingNode == null) return null;
        while (workingNode != null) {
            if (workingNode.getClass().getName().equals(ancestorNodeClassName)) return workingNode;
            workingNode = workingNode.getParent();
        }
        return null;
    }

    public TreeNode getAncestorForLevel(int ancestorLevel) {
        // Ensure ancestor level is valid.
        int thisLevel = this.getLevel();
        if (ancestorLevel < 0 || ancestorLevel >= thisLevel) return null;

        // Search back in the tree the ancestor for that level.
        TreeNode ancestor = this.getParent();
        while (ancestor.getLevel() > ancestorLevel) ancestor = ancestor.getParent();
        return ancestor;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof TreeNode)) return false;
        TreeNode other = (TreeNode) obj;
        return getPath().equals(other.getPath());
    }

    public String toString() {
        return super.toString() + " Id:" + getId();
    }

    /**
     * Get the children list for this node
     *
     * @return the dynamic list of children for this node.
     */
    protected List listChildren() {
        return null;
    }

    /**
     * Get a children node by its id. Default implementation just iterates listChildren() elements.
     * But subnodes may overrride it to provide faster implementations.
     *
     * @param id
     * @return a children node by its id, or null if it doesn't exist
     */
    protected TreeNode listChildrenById(String id) {
        List l = listChildren();
        if (l != null)
            for (int i = 0; i < l.size(); i++) {
                TreeNode node = (TreeNode) l.get(i);
                if (id.equals(node.getId())) {
                    return node;
                }
            }
        return null;
    }

    protected boolean hasDynamicChildren() {
        List l = listChildren();
        return l != null && !l.isEmpty();
    }
}
