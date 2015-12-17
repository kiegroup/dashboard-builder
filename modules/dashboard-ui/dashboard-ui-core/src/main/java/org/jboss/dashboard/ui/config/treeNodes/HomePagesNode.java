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
package org.jboss.dashboard.ui.config.treeNodes;

import javax.inject.Inject;

import org.jboss.dashboard.ui.config.AbstractNode;
import org.jboss.dashboard.ui.config.components.homePages.HomePagesHandler;
import org.slf4j.Logger;

public class HomePagesNode extends AbstractNode {

    @Inject
    private transient Logger log;

    @Inject
    private HomePagesHandler homePagesHandler;

    public HomePagesHandler getHomePagesHandler() {
        return homePagesHandler;
    }

    public String getId() {
         return "homePagesNode";
    }

    public String getIconId() {
        return "16x16/ico-menu_role.png";
    }

    public boolean onEdit() {
        try {
            String workspaceId = ((WorkspaceNode) getParent()).getWorkspaceId();
            getHomePagesHandler().setWorkspaceId(workspaceId);
        } catch (Exception e) {
            log.error("Error: ", e);
            return false;
        }
        return super.onEdit();
    }
}
