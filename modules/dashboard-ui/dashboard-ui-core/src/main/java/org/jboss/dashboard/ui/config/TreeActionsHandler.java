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

import org.jboss.dashboard.ui.components.HandlerFactoryElement;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.responses.DoNothingResponse;
import org.jboss.dashboard.ui.controller.responses.ShowCurrentScreenResponse;

import javax.servlet.RequestDispatcher;
import java.io.IOException;

public class TreeActionsHandler extends HandlerFactoryElement {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TreeActionsHandler.class.getName());
    private TreeStatus treeStatus;
    private Tree tree;

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

    public CommandResponse actionNavigateTo(CommandRequest request) {
        String path = request.getParameter("path");
        TreeNode node = tree.getNodeByPath(path);
        if (node == null) {
            log.error("Node with path " + path + " does not exist.");
            return new ShowCurrentScreenResponse();
        }
        treeStatus.edit(node);
        return new ShowCurrentScreenResponse();
    }

    public CommandResponse actionExpandOrCollapse(CommandRequest request) {
        String path = request.getParameter("path");
        TreeNode node = tree.getNodeByPath(path);
        if (node == null) {
            log.error("Node with path " + path + " does not exist.");
            return new ShowCurrentScreenResponse();
        }
        if (treeStatus.isExpanded(node)) {
            treeStatus.collapse(node);
        } else {
            treeStatus.expand(node);
        }
        return new ShowCurrentScreenResponse();
    }

    public CommandResponse actionShowEditPage(CommandRequest request) {
        TreeNode treeNode = treeStatus.getLastEditedNode(getTree());
        String editPage = treeNode.getEditURI();
        RequestDispatcher rd = request.getRequestObject().getRequestDispatcher(editPage);
        try {
            rd.include(request.getRequestObject(), request.getResponseObject());
        } catch (Exception e) {
            log.error("Error including page: " + editPage, e);
            try {
                request.getResponseObject().sendError(500);
            } catch (IOException e1) {
                log.error("Error:", e1);
            }
        }
        return new DoNothingResponse();
    }

    public CommandResponse actionShowTreePage(CommandRequest request) {
        RequestDispatcher rd = request.getRequestObject().getRequestDispatcher("/configuration/tree/tree.jsp");
        try {
            rd.include(request.getRequestObject(), request.getResponseObject());
        } catch (Exception e) {
            log.error("Error:", e);
            try {
                request.getResponseObject().sendError(500);
            } catch (IOException e1) {
                log.error("Error:", e1);
            }
        }
        return new DoNothingResponse();
    }
}
