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
package org.jboss.dashboard.ui.config.components.resources;

import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.ui.components.BeanHandler;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.workspace.GraphicElementManager;
import org.jboss.dashboard.ui.resources.GraphicElement;
import org.jboss.dashboard.ui.resources.GraphicElementPreview;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

@SessionScoped
public class ResourcesPropertiesHandler extends BeanHandler {

    public static final String PREVIEW_ATTRIBUTE = "attrPreview";
    public static final String FIELD_RESOURCEID = "resourceId";
    public static final String FIELD_FILE = "inputFile";

    public static ResourcesPropertiesHandler lookup() {
        return CDIBeanLocator.getBeanByType(ResourcesPropertiesHandler.class);
    }

    @Inject
    private transient Logger log;

    private String resourceType;  //graphicElement
    private String workspaceId;
    private String socpeWorkspaceId;
    private Long sectionId;
    private Long panelId;
    private String resourceId;
    private File file;
    private boolean preview = false;
    private boolean inserted = false;
    private String actionCreate;
    private String actionCancel;
    private List fieddErrors = new ArrayList();
    private boolean zipHasError;

    public boolean isZipHasError() {
        return zipHasError;
    }

    public void setZipHasError(boolean zipHasError) {
        this.zipHasError = zipHasError;
    }

    public List getFieddErrors() {
        return fieddErrors;
    }

    public void setFieddErrors(List fieddErrors) {
        this.fieddErrors = fieddErrors;
    }

    public String getSocpeWorkspaceId() {
        return socpeWorkspaceId;
    }

    public void setSocpeWorkspaceId(String socpeWorkspaceId) {
        this.socpeWorkspaceId = socpeWorkspaceId;
    }

    public boolean isInserted() {
        return inserted;
    }

    public void setInserted(boolean inserted) {
        this.inserted = inserted;
    }

    public String getActionCreate() {
        return actionCreate;
    }

    public void setActionCreate(String actionCreate) {
        this.actionCreate = actionCreate;
    }

    public String getActionCancel() {
        return actionCancel;
    }

    public void setActionCancel(String actionCancel) {
        this.actionCancel = actionCancel;
    }

    public boolean isPreview() {
        return preview;
    }

    public void setPreview(boolean preview) {
        this.preview = preview;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Long getPanelId() {
        return panelId;
    }

    public void setPanelId(Long panelId) {
        this.panelId = panelId;
    }

    public void actionStartPreview(CommandRequest request) {

        preview = false;
        zipHasError = false;

        if (fieddErrors.size() > 0) fieddErrors.clear();

        if ((getFieldErrors().size() > 0)) {
            log.error("Error on form");
            return;
        }

        if ((resourceId == null) || ("".equals(resourceId))) {
            fieddErrors.add(FIELD_RESOURCEID);
            if (!((file == null) || ("".equals(file.toString())))) return;
        }

        if ((file == null) || ("".equals(file.toString()))) {
            fieddErrors.add(FIELD_FILE);
            return;
        }

        if (request.getUploadedFilesCount() > 1) {
            log.error("Number of files uploaded are more than one. It is necessari to upload just one file");
            return;
        }
        if ((resourceType == null) || "".equals(resourceType)) {
            log.error("Error. Graphic element is not correct");
            return;
        }
        preview = true;
        try {
            resourceId = StringEscapeUtils.ESCAPE_HTML4.translate(resourceId);
            String graphicElementClassName = resourceType.substring(0, 1).toUpperCase() + resourceType.substring(1);
            Class graphicElementClass = Class.forName("org.jboss.dashboard.ui.resources." + graphicElementClassName);
            if ("".equals(socpeWorkspaceId)) socpeWorkspaceId = null;
            Method previewGetter = graphicElementClass.getMethod("getPreviewInstance", new Class[]{File.class, String.class, Long.class, Long.class, String.class});
            GraphicElementPreview preview = (GraphicElementPreview) previewGetter.invoke(null, new Object[]{file, (workspaceId == null) ? socpeWorkspaceId : workspaceId, sectionId, panelId, resourceId});
            request.getSessionObject().setAttribute(PREVIEW_ATTRIBUTE, preview);
            actionCancel = null;
            actionCreate = null;
        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
            setErrorOnZipFile();
        }
    }

    public void actionConfirmNewElement(CommandRequest request) {

        if ((actionCreate == null) || (zipHasError)) {
            goToElementsPage();
            return;
        }
        GraphicElementPreview previewElement = (GraphicElementPreview) request.getSessionObject().getAttribute(PREVIEW_ATTRIBUTE);
        if (previewElement.getStatus() != GraphicElementPreview.STATUS_OK) {
            goToElementsPage();
            return;
        }

        try {
            String graphicElementClassName = resourceType.substring(0, 1).toUpperCase() + resourceType.substring(1);
            Class graphicElementClass = Class.forName("org.jboss.dashboard.ui.resources." + graphicElementClassName);
            Method managerGetter = graphicElementClass.getMethod("getManager", new Class[]{});
            GraphicElementManager manager = (GraphicElementManager) managerGetter.invoke(null, new Object[]{});
            GraphicElementPreview preview = (GraphicElementPreview) request.getSessionObject().getAttribute(PREVIEW_ATTRIBUTE);
            GraphicElement element = preview.toElement();
            manager.createOrUpdate(element);
            actionCreate = null;
            actionCancel = null;
            inserted = true;
            resourceId = null;
            file = null;
            //preview and inserted change the value in ResourcePropertiesFormatter
        } catch (Exception e) {
            log.error("Error: " + e.getMessage(), e);
            setErrorOnZipFile();
        }
    }

    public void setErrorOnZipFile() {
        zipHasError = true;
        preview = true;
        actionCancel = null;
        resourceId = null;
        inserted = false;
    }

    public void goToElementsPage() {
        preview = false;
        actionCancel = null;
        resourceId = null;
        inserted = false;
    }
}

