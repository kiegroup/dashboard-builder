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
package org.jboss.dashboard.workspace.export;

import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.security.UIPermission;
import org.jboss.dashboard.ui.resources.GraphicElement;
import org.jboss.dashboard.workspace.*;

import java.security.Principal;

/**
 * Interface for workspace export visitors.
 */
public interface WorkspaceVisitor {

    public Object visitWorkspace(Workspace workspace) throws Exception;

    public Object visitSection(Section section) throws Exception;

    public Object visitPanelInstance(PanelInstance instance) throws Exception;

    public Object visitPanel(Panel panel) throws Exception;

    public Object visitGraphicElement(GraphicElement resource) throws Exception;

    public Object visitPanelParameter(PanelParameter param) throws Exception;

    public Object visitWorkspaceParameter(WorkspaceParameter param) throws Exception;

    public Object visitPermission(UIPermission perm, Principal relatedPrincipal) throws Exception;

    public Object endVisit() throws Exception;
}
