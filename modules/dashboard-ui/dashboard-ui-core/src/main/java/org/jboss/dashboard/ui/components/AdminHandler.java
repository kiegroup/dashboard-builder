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
package org.jboss.dashboard.ui.components;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.responses.ShowCurrentScreenResponse;
import org.jboss.dashboard.ui.controller.responses.ShowScreenResponse;
import org.jboss.dashboard.ui.utils.forms.FormStatus;
import org.jboss.dashboard.workspace.GraphicElementManager;
import org.jboss.dashboard.ui.controller.responses.ShowPopupPage;
import org.jboss.dashboard.ui.utils.forms.SimpleFormHandler;
import org.jboss.dashboard.ui.resources.GraphicElement;
import org.jboss.dashboard.ui.resources.GraphicElementPreview;

import java.io.File;
import java.lang.reflect.Method;

public class AdminHandler extends HandlerFactoryElement {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AdminHandler.class.getName());

    public static final String PREVIEW_ATTRIBUTE = "attrPreview";


    /**
     * Change workspace for a given element
     */
    public CommandResponse actionChangeScopeForElement(CommandRequest request) throws Exception {
        String graphicElement = request.getParameter("graphicElement");
        if (graphicElement == null || "".equals(graphicElement)) {
            log.error("Missing required parameter: graphicElement.");
            return new ShowCurrentScreenResponse();
        }
        String graphicElementClassName = graphicElement.substring(0, 1).toUpperCase() + graphicElement.substring(1);
        Class graphicElementClass = Class.forName("org.jboss.dashboard.ui.resources." + graphicElementClassName);
        Method managerGetter = graphicElementClass.getMethod("getManager", new Class[]{});
        GraphicElementManager manager = (GraphicElementManager) managerGetter.invoke(null, new Object[]{});
        FormStatus status = SessionManager.getCurrentFormStatus();
        status.clear();

        String elementId = request.getParameter(graphicElement + "Id");
        String workspaceId = request.getParameter("workspaceId");
        String sectionIdParam = request.getParameter("sectionId");
        String panelIdParam = request.getParameter("panelId");
        log.debug("Changing scope to " + workspaceId + "->" + sectionIdParam + "->" + panelIdParam);
        workspaceId = "".equals(workspaceId) ? null : workspaceId;
        Long sectionId = "".equals(sectionIdParam) || sectionIdParam == null ? null : new Long(sectionIdParam);
        Long panelId = "".equals(panelIdParam) || panelIdParam == null ? null : new Long(panelIdParam);
        if (workspaceId == null) {
            sectionId = null;
            panelId = null;
        }
        if (sectionId == null && manager.getElementScopeDescriptor().isAllowedPanel())
            panelId = null;

        if (elementId != null) {
            final GraphicElement element = manager.getElementByDbid(elementId);
            if (element != null) {
                log.debug("Find " + graphicElement + " with id=" + element.getId() + " and workspace=" + workspaceId + " and section=" + sectionId + " and panel=" + panelId);
                GraphicElement existingElement = manager.getElement(element.getId(), workspaceId, sectionId, panelId);
                if (existingElement != null) {
                    log.warn("Refusing change " + graphicElement + " workspace, as it would match an existing one.");
                    status.addMessage("ui.admin.workarea." + graphicElement + "s.cannotChangeScope");
                } else {
                    element.clearDeploymentFiles();
                    element.setWorkspaceId(workspaceId);
                    element.setSectionId(sectionId);
                    element.setPanelId(panelId);
                    manager.createOrUpdate(element);
                }
            } else {
                log.error(graphicElementClass + " not found, id=" + elementId);
            }
        } else {
            log.error("Not all parameters informed: " + graphicElement + "Id.");
        }
        return new ShowCurrentScreenResponse();
    }

    /**
     * Delete an element
     */
    public CommandResponse actionDeleteElement(CommandRequest request) throws Exception {
        String graphicElement = request.getParameter("graphicElement");
        if (graphicElement == null || "".equals(graphicElement)) {
            log.error("Missing required parameter: graphicElement.");
            return new ShowCurrentScreenResponse();
        }
        String graphicElementClassName = graphicElement.substring(0, 1).toUpperCase() + graphicElement.substring(1);
        Class graphicElementClass = Class.forName("org.jboss.dashboard.ui.resources." + graphicElementClassName);
        Method managerGetter = graphicElementClass.getMethod("getManager", new Class[]{});
        GraphicElementManager manager = (GraphicElementManager) managerGetter.invoke(null, new Object[]{});
        String id = request.getParameter(graphicElement + "Id");
        GraphicElement element = manager.getElementByDbid(id);
        if (element != null)
            manager.delete(element);
        else
            log.error("Cannot delete element");
        return new ShowCurrentScreenResponse();
    }

    /**
     * Create a preview, and redirect to preview screen
     */
    public static CommandResponse actionPreviewNewElement(CommandRequest request) throws Exception {
        log.debug("actionPreviewNewElement");
        FormStatus status = SessionManager.getCurrentFormStatus();
        SimpleFormHandler handler = new SimpleFormHandler(status);
        status.clear();
        String graphicElement = request.getParameter("graphicElement");
        if (graphicElement == null || "".equals(graphicElement)) {
            log.error("Missing required parameter: graphicElement.");
            return new ShowCurrentScreenResponse();
        }
        String graphicElementClassName = graphicElement.substring(0, 1).toUpperCase() + graphicElement.substring(1);
        Class graphicElementClass = Class.forName("org.jboss.dashboard.ui.resources." + graphicElementClassName);

        String workspaceId = handler.validateString(request, "workspaceId", false);
        String sectionId = handler.validateString(request, "sectionId", false);
        String panelId = handler.validateString(request, "panelId", false);
        String id = handler.validateString(request, graphicElement + "Id", true);
        if (!isValidId(id)) {
            status.addWrongField(graphicElement + "Id");
        }
        try {
            if (request.getUploadedFilesCount() == 1) {
                if (status.isValidated()) {
                    log.debug("Creating preview.");
                    try {
                        Method previewGetter = graphicElementClass.getMethod("getPreviewInstance", new Class[]{File.class, String.class, Long.class, Long.class, String.class});
                        GraphicElementPreview preview = (GraphicElementPreview) previewGetter.invoke(null, new Object[]{request.getFilesByParamName().get("file"), workspaceId, sectionId == null ? null : new Long(sectionId), panelId == null ? null : new Long(panelId), id});
                        request.getSessionObject().setAttribute(PREVIEW_ATTRIBUTE, preview);
                        log.debug("Created preview " + preview);
                    } catch (Exception e) {
                        log.error("Error making preview: ", e);
                    }
                }
            } else {
                log.debug("Status is not valid. Number of files is not 1, it is " + request.getUploadedFilesCount());
                status.addWrongField("zipFile");
            }
        } catch (Exception e) {
            status.addError(e.getMessage());
        }
        if (status.isValidated()) {
            String previewPage = request.getParameter("previewPage");
            if (previewPage != null) {
                return new ShowScreenResponse(previewPage);
            }
        }
        return new ShowCurrentScreenResponse();
    }

    /**
     * Confirm creation of an Element
     */
    public static CommandResponse actionConfirmNewElement(CommandRequest request) throws Exception {
        String graphicElement = request.getParameter("graphicElement");
        if (graphicElement == null || "".equals(graphicElement)) {
            log.error("Missing required parameter: graphicElement.");
            return new ShowCurrentScreenResponse();
        }
        String graphicElementClassName = graphicElement.substring(0, 1).toUpperCase() + graphicElement.substring(1);
        Class graphicElementClass = Class.forName("org.jboss.dashboard.ui.resources." + graphicElementClassName);
        Method managerGetter = graphicElementClass.getMethod("getManager", new Class[]{});
        GraphicElementManager manager = (GraphicElementManager) managerGetter.invoke(null, new Object[]{});
        GraphicElementPreview preview = (GraphicElementPreview) request.getSessionObject().getAttribute(PREVIEW_ATTRIBUTE);
        final GraphicElement element = preview.toElement();
        manager.createOrUpdate(element);
        FormStatus status = SessionManager.getCurrentFormStatus();
        status.clear();
        String successPage = request.getParameter("successPage");
        if (successPage != null) {
            return new ShowScreenResponse(successPage);
        }
        return new ShowCurrentScreenResponse();
    }


    /**
     * Change the skin to use in admin mode
     */
    public CommandResponse actionChangeAdminSkin(CommandRequest request) throws Exception {
        String id = request.getParameter("skinDbid");
        request.getSessionObject().setAttribute("adminSkinToUse", id);
        return new ShowCurrentScreenResponse();
    }

    /**
     * Cancel operation, show current screen
     */
    public CommandResponse actionCancel(CommandRequest request) throws Exception {
        return new ShowCurrentScreenResponse();
    }

    /**
     * Change the language to use in admin mode
     */
    public CommandResponse actionChangeLanguage(CommandRequest request) throws Exception {
        String lang = request.getParameter("language");
        if (lang != null && lang.trim().length() > 0) {
            LocaleManager.lookup().setCurrentLang(lang);
        }
        return new ShowCurrentScreenResponse();
    }

    protected static boolean isValidId(String s) {
        if (s == null || "".equals(s))
            return false;
        if (s.indexOf("/") != -1)
            return false;
        if (s.indexOf("\\") != -1)
            return false;
        if (s.indexOf(".") != -1)
            return false;
        return true;
    }


    public CommandResponse actionPreviewGraphicElement(CommandRequest request) throws Exception {
        String graphicElement = request.getParameter("elementCategoryName"); //skin, envelope, ...
        String previewPage = request.getParameter("previewPage"); // previews/skin ...
        String elementId = request.getParameter("elementId");

        String graphicElementClassName = graphicElement.substring(0, 1).toUpperCase() + graphicElement.substring(1);
        Class graphicElementClass = Class.forName("org.jboss.dashboard.ui.resources." + graphicElementClassName);
        Method managerGetter = graphicElementClass.getMethod("getManager", new Class[]{});
        GraphicElementManager manager = (GraphicElementManager) managerGetter.invoke(null, new Object[]{});

        GraphicElement element = manager.getElementByDbid(elementId);

        request.getRequestObject().setAttribute("previewElement", element);
        request.getRequestObject().setAttribute("fullPageJsp", "/admin/" + previewPage);
        return new ShowScreenResponse("/admin/" + previewPage);
    }

    public CommandResponse actionPreviewConfigComponent(CommandRequest request) throws Exception {
        return new ShowPopupPage("/admin/previews/configurationComponent.jsp");
    }

}
