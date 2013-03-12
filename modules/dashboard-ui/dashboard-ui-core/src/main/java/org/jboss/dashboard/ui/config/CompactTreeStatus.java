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

import java.util.ArrayList;
import java.util.Iterator;


public class CompactTreeStatus extends DefaultTreeStatus implements TreeStatus {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(CompactTreeStatus.class.getName());


    public boolean expand(TreeNode node) {
        String path = node.getPath();
        ArrayList removablePaths = new ArrayList();
        for (Iterator it = getExpandedPaths().iterator(); it.hasNext();) {
            String treePath = (String) it.next();
            if (path.indexOf(treePath) == -1) {
                removablePaths.add(treePath);
            }
        }
        getExpandedPaths().removeAll(removablePaths);
        boolean added = getExpandedPaths().add(path);
        boolean expanded = false;
        if (added) {
            expanded = node.onExpand();
            if (isExpandImpliesEdit()) {
                edit(node);
            }
        }
        return expanded;
    }
}
