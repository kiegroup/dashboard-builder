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
package org.jboss.dashboard.ui;

import org.jboss.dashboard.ui.components.URLMarkupGenerator;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.workspace.export.ExportManager;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.ui.components.js.JSIncluder;
import org.jboss.dashboard.ui.resources.ResourceManager;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@ApplicationScoped
@Named("UIServices")
public class UIServices {

    public static UIServices lookup() {
        return (UIServices) CDIBeanLocator.getBeanByName("UIServices");
    }

    @Inject
    protected WorkspacesManager workspacesManager;

    @Inject
    protected SectionsManager sectionsManager;

    @Inject
    protected PanelsManager panelsManager;

    @Inject
    protected PanelsProvidersManager panelsProvidersManager;

    @Inject
    protected SkinsManager skinsManager;

    @Inject
    protected LayoutsManager layoutsManager;

    @Inject
    protected EnvelopesManager envelopesManager;

    @Inject
    protected ResourceGalleryManager resourceGalleryManager;

    @Inject
    protected ResourceManager resourceManager;

    @Inject
    protected JSIncluder jsIncluder;

    @Inject
    protected ExportManager exportManager;

    @Inject
    protected CopyManager copyManager;

    @Inject
    protected URLMarkupGenerator urlMarkupGenerator;

    protected GraphicElementManager[] graphicElementManagers;

    @PostConstruct
    protected void init() {
        graphicElementManagers = new GraphicElementManager[] {
                skinsManager, envelopesManager, layoutsManager, resourceGalleryManager};
    }

    public GraphicElementManager[] getGraphicElementManagers() {
        return graphicElementManagers;
    }

    public WorkspacesManager getWorkspacesManager() {
        return workspacesManager;
    }

    public void setWorkspacesManager(WorkspacesManager workspacesManager) {
        this.workspacesManager = workspacesManager;
    }

    public SectionsManager getSectionsManager() {
        return sectionsManager;
    }

    public void setSectionsManager(SectionsManager sectionsManager) {
        this.sectionsManager = sectionsManager;
    }

    public PanelsManager getPanelsManager() {
        return panelsManager;
    }

    public void setPanelsManager(PanelsManager panelsManager) {
        this.panelsManager = panelsManager;
    }

    public PanelsProvidersManager getPanelsProvidersManager() {
        return panelsProvidersManager;
    }

    public void setPanelsProvidersManager(PanelsProvidersManager panelsProvidersManager) {
        this.panelsProvidersManager = panelsProvidersManager;
    }

    public SkinsManager getSkinsManager() {
        return skinsManager;
    }

    public void setSkinsManager(SkinsManager skinsManager) {
        this.skinsManager = skinsManager;
    }

    public LayoutsManager getLayoutsManager() {
        return layoutsManager;
    }

    public void setLayoutsManager(LayoutsManager layoutsManager) {
        this.layoutsManager = layoutsManager;
    }

    public EnvelopesManager getEnvelopesManager() {
        return envelopesManager;
    }

    public void setEnvelopesManager(EnvelopesManager envelopesManager) {
        this.envelopesManager = envelopesManager;
    }

    public ResourceGalleryManager getResourceGalleryManager() {
        return resourceGalleryManager;
    }

    public void setResourceGalleryManager(ResourceGalleryManager resourceGalleryManager) {
        this.resourceGalleryManager = resourceGalleryManager;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public JSIncluder getJsIncluder() {
        return jsIncluder;
    }

    public void setJsIncluder(JSIncluder jsIncluder) {
        this.jsIncluder = jsIncluder;
    }

    public ExportManager getExportManager() {
        return exportManager;
    }

    public void setExportManager(ExportManager exportManager) {
        this.exportManager = exportManager;
    }

    public CopyManager getCopyManager() {
        return copyManager;
    }

    public void setCopyManager(CopyManager copyManager) {
        this.copyManager = copyManager;
    }

    public URLMarkupGenerator getUrlMarkupGenerator() {
        return urlMarkupGenerator;
    }

    public void setUrlMarkupGenerator(URLMarkupGenerator urlMarkupGenerator) {
        this.urlMarkupGenerator = urlMarkupGenerator;
    }
}
