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
package org.jboss.dashboard.ui.config.formatters;

import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.ui.config.Tree;
import org.jboss.dashboard.ui.config.TreeNode;
import org.jboss.dashboard.ui.config.TreeStatus;
import org.apache.commons.lang.StringEscapeUtils;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class TreeFormatter extends Formatter {

    private TreeStatus treeStatus;
    private Tree tree;

    public TreeFormatter() {
    }

    /**
     * Header Formatter
     */
    public void getHeaderFormatter() {
    }

    public TreeStatus getTreeStatus() {
        return treeStatus;
    }

    public void setTreeStatus(TreeStatus treeStatus) {
        this.treeStatus = treeStatus;
    }

    public Tree getTree() {
        return tree;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        renderFragment("treeStart");
        List rootNodes = getTree().getRootNodes();
        if (rootNodes != null) {
            for (int i = 0; i < rootNodes.size(); i++) {
                TreeNode mainNode = (TreeNode) rootNodes.get(i);
                if (getTreeStatus().isExpanded(mainNode)) {
                    setAttribute("expand_icon", "ICON_mainnode_minimize");
                    setAttribute("expand_action", "collapse-node");
                } else {
                    setAttribute("expand_icon", "ICON_mainnode_maximize");
                    setAttribute("expand_action", "expand-node");
                }
                setAttribute("path_mainNode", mainNode.getPath());
                setAttribute("id_mainNode", mainNode.getId());
                setAttribute("name_mainNode", StringEscapeUtils.escapeHtml(mainNode.getName(getLocale())));
                setAttribute("icon_mainNode", mainNode.getIconId());
                setAttribute("mainNode", mainNode);
                setAttribute("level_mainNode", mainNode.getLevel());
                renderFragment("mainNode");
            }
        }
        renderFragment("treeEnd");
    }
}
