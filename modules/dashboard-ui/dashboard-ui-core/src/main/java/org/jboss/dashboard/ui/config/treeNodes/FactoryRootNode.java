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
package org.jboss.dashboard.ui.config.treeNodes;

import org.jboss.dashboard.Application;
import org.jboss.dashboard.security.BackOfficePermission;
import org.jboss.dashboard.users.UserStatus;

import java.util.Map;

public class FactoryRootNode extends FactoryFolderNode {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FactoryRootNode.class.getName());

    public String getId() {
        return "factory";
    }

    public Map getMappings() {
        return Application.lookup().getGlobalFactory().getTree().getTreeMappings();
    }

    public Map getName() {
        return getI18nPropertiesMap("name");
    }

    public Map getDescription() {
        return getI18nPropertiesMap("description");
    }

    public boolean isExpandible() {
        BackOfficePermission editPerm = BackOfficePermission.newInstance(null, BackOfficePermission.ACTION_USE_FACTORY);
        return UserStatus.lookup().hasPermission(editPerm);
    }
}
