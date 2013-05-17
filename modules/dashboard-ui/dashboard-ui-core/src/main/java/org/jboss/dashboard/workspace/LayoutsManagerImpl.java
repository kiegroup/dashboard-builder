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
package org.jboss.dashboard.workspace;

import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.ui.resources.GraphicElementScopeDescriptor;
import org.jboss.dashboard.ui.resources.Layout;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class LayoutsManagerImpl extends GraphicElementManagerImpl implements LayoutsManager {

    @Inject @Config("WEB-INF/etc/layouts")
    private String layoutsDir;

    public void start() throws Exception {
        super.classToHandle = Layout.class;
        super.baseDir = layoutsDir;
        super.start();
    }

    /**
     * Determine the scope for the element handled by this manager.
     */
    public GraphicElementScopeDescriptor getElementScopeDescriptor() {
        return GraphicElementScopeDescriptor.SECTION_SCOPED;
    }
}
