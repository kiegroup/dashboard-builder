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
package org.jboss.dashboard.ui.config.formatters;

import org.jboss.dashboard.ui.config.ConfigurationTree;
import org.jboss.dashboard.ui.config.ConfigurationTreeStatus;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.ui.config.Tree;
import org.jboss.dashboard.ui.config.TreeNode;
import org.jboss.dashboard.ui.config.TreeStatus;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;

public class TreeNodeFormatter extends Formatter {

    @Inject
    private Logger log;

    @Inject
    private ConfigurationTreeStatus treeStatus;

    @Inject
    private ConfigurationTree tree;

    public TreeNodeFormatter() {
    }

    public TreeStatus getTreeStatus() {
        return treeStatus;
    }

    public Tree getTree() {
        return tree;
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {
            int nodeIndex = 0;
            TreeNode an = (TreeNode) getParameter("treenode");
            renderFragment("nodeTab");
            if (an != null) {
                if ((treeStatus.isExpanded(an))) {
                    List children = an.getChildren();
                    if (children != null) {
                        Iterator i = children.iterator();
                        while (i.hasNext()) {
                            TreeNode subNode = (TreeNode) i.next();
                            if (treeStatus.isExpanded(subNode)) {
                                if (i.hasNext()) {
                                    setAttribute("expand_path", "branch_contract.gif");
                                    setAttribute("line_path", "line_expand.gif");
                                } else {
                                    setAttribute("expand_path", "branch_contract_01.gif");
                                    setAttribute("line_path", "spacer.png");
                                }
                                setAttribute("expand_action", "collapse-node");
                            } else {
                                if (i.hasNext()) {
                                    setAttribute("expand_path", "branch_expand_02.gif");
                                    setAttribute("line_path", "line_expand.gif");
                                } else {
                                    setAttribute("expand_path", "branch_expand_01.gif");
                                    setAttribute("line_path", "spacer.png");
                                }
                                setAttribute("expand_action", "expand-node");
                            }

                            if (i.hasNext()) {
                                setAttribute("line_path", "line_expand.gif");
                                setAttribute("branchPath", "branch_02.gif");
                            } else {
                                setAttribute("branchPath", "branch_01.gif");
                                setAttribute("line_path", "spacer.png");
                            }
                            setAttribute("isEditable", subNode.isEditable());
                            setAttribute("path_Node", subNode.getPath());
                            setAttribute("id_Node", subNode.getId());
                            setAttribute("name_Node", StringEscapeUtils.ESCAPE_HTML4.translate(StringUtils.defaultString(subNode.getName(getLocale()))));
                            setAttribute("icon_Node", subNode.getIconId());
                            setAttribute("iconNodePath", subNode.getIconCategory());
                            setAttribute("parent_Node", subNode.getParent());
                            setAttribute("node", subNode);
                            setAttribute("level_Node", subNode.getLevel());
                            setAttribute("isEdited", getTreeStatus().isEdited(subNode));
                            setAttribute("nodeIndex",nodeIndex++);
                            renderFragment("subNode");
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            // Error fragment.
            renderFragment("error");
            log.error("Cannot render node.", e);
        }
    }
}
