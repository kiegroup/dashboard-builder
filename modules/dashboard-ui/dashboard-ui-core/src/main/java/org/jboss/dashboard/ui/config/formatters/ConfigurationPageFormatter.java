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

import org.jboss.dashboard.ui.config.ConfigurationTree;
import org.jboss.dashboard.ui.config.ConfigurationTreeStatus;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.ui.config.TreeNode;
import org.jboss.dashboard.users.UserStatus;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConfigurationPageFormatter extends Formatter {

    @Inject
    private ConfigurationTree tree;

    @Inject
    private ConfigurationTreeStatus treeStatus;

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        if (!UserStatus.lookup().isAnonymous()) {
            TreeNode editedNode = treeStatus.getLastEditedNode(tree);
            if (editedNode != null) {
                setAttribute("editPage", editedNode.getEditURI());
                setAttribute("description", StringEscapeUtils.ESCAPE_HTML4.translate(editedNode.getDescription(getLocale())));
                setAttribute("ajaxCompatible", editedNode.isEditURIAjaxCompatible());
                setAttribute("path_Node", editedNode.getPath());
                setAttribute("name_Node", StringEscapeUtils.ESCAPE_HTML4.translate(StringUtils.defaultString(editedNode.getName(getLocale()))));
                setAttribute("icon_Node", editedNode.getIconId());
                setAttribute("iconNodePath", editedNode.getIconCategory());
            }
            renderFragment("output");
        } else {
            renderFragment("accessDenied");
        }
    }
}
