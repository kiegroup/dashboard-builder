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
package org.jboss.dashboard.ui.config.components.resources;

import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.workspace.GraphicElementManager;
import org.jboss.dashboard.ui.resources.GraphicElement;
import org.jboss.dashboard.ui.resources.GraphicElementPreview;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class ResourcesPropertiesFormatter extends Formatter {

    @Inject
    private transient Logger log;

    @Inject
    private ResourcesPropertiesHandler handler;

    public ResourcesPropertiesHandler getHandler() {
        return handler;
    }

    public void setHandler(ResourcesPropertiesHandler handler) {
        this.handler = handler;
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {
            if (handler.isInserted()) {
                handler.setInserted(false);
                handler.setPreview(false);
            }
            if (handler.isPreview()) {
                servicePreview(httpServletRequest, httpServletResponse);
                return;
            }
            String graphicElement = handler.getResourceType();
            String graphicElementClassName = graphicElement.substring(0, 1).toUpperCase() + graphicElement.substring(1);
            Class graphicElementClass = Class.forName("org.jboss.dashboard.ui.resources." + graphicElementClassName);
            Method managerGetter = graphicElementClass.getMethod("getManager", new Class[]{});
            String type = graphicElement + "s.new" + graphicElement.substring(0, 1).toUpperCase() + graphicElement.substring(1, graphicElement.length());
            setAttribute("type", type);
            renderFragment("outputUploadResourceStart");
            if (handler.getWorkspaceId() == null) {
                renderFragment("outputUploadResourceStartRow");
                setAttribute("graphicElement", graphicElement);
           /*     renderFragment("outputUploadResourceScopeStart");
                for (Iterator it = workspacesManager.getAllWorkspacesIdentifiers().iterator(); it.hasNext();) {
                    String id = (String) it.next();
                    setAttribute("workspaceId", id);
                    setAttribute("workspaceTitle", workspacesManager.getWorkspace(id).getTitle().get(SessionManager.getLang()));
                    renderFragment("outputUploadResourceScope");
                }
                renderFragment("outputUploadResourceScopeEnd");
          */      //renderFragment("outputUploadResourceEndRow");
            }
            //renderFragment("outputUploadResourceStartRow");
            setAttribute("graphicElement", graphicElement);
            setAttribute("error", "");
            for (int i = 0; i < handler.getFieddErrors().size(); i++)
                if (handler.getFieddErrors().get(i).equals(ResourcesPropertiesHandler.FIELD_RESOURCEID)) {
                    setAttribute("error", "skn-error");
                    break;
                } else
                    setAttribute("error", "");
            renderFragment("outputUploadResourceIdentifier");
            //renderFragment("outputUploadResourceEndRow");
            //renderFragment("outputUploadResourceStartRow");
            setAttribute("graphicElement", graphicElement);
            setAttribute("error", "");
            for (int i = 0; i < handler.getFieddErrors().size(); i++)
                if (handler.getFieddErrors().get(i).equals(ResourcesPropertiesHandler.FIELD_FILE)) {
                    setAttribute("error", "skn-error");
                    break;
                } else
                    setAttribute("error", "");
            renderFragment("outputUploadResourceFile");
            renderFragment("outputUploadResourceEndRow");
            setAttribute("graphicElement", graphicElement);
            renderFragment("outputUploadResourceEnd");

            GraphicElementManager manager = (GraphicElementManager) managerGetter.invoke(null, new Object[]{});
            setAttribute("graphicElement", graphicElement);
            setAttribute("graphicElementClassName", graphicElementClassName);
            setAttribute("manager", manager);
            String workspaceId = handler.getWorkspaceId();
            Long sectionId = handler.getSectionId();
            Long panelId = handler.getPanelId();

            GraphicElement[] elements;
            if ((workspaceId == null) & (sectionId == null) & (panelId == null)) elements = manager.getElements();
            else
                elements = manager.getManageableElements(workspaceId, sectionId, panelId);
            setAttribute("elements", elements);
            renderFragment("outputResources");

        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
        }

    }

    public void servicePreview(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String graphicElement = handler.getResourceType();
        String graphicElementClassName = graphicElement.substring(0, 1).toUpperCase() + graphicElement.substring(1);
        setAttribute("graphicElement", graphicElement);
        setAttribute("graphicElementClassName", graphicElementClassName);
        renderFragment("outputPreview");
        GraphicElementPreview preview = (GraphicElementPreview) httpServletRequest.getSession().getAttribute(ResourcesPropertiesHandler.PREVIEW_ATTRIBUTE);
        if ((!((preview.getStatus() >= 1) && (preview.getStatus() <= 4))) && (!handler.isZipHasError()))
            renderFragment("outputPreviewConfirm");
        renderFragment("outputPreviewEnd");
    }
}
