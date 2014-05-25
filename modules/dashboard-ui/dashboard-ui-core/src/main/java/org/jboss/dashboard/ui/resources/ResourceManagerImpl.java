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
package org.jboss.dashboard.ui.resources;

import org.jboss.dashboard.annotation.Priority;
import org.jboss.dashboard.annotation.Startable;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.ui.config.components.resources.ResourcesPropertiesHandler;
import org.jboss.dashboard.workspace.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Properties;
import org.jboss.dashboard.LocaleManager;

@ApplicationScoped
public class ResourceManagerImpl implements ResourceManager, Startable {

    private static transient Logger log = LoggerFactory.getLogger(ResourceManagerImpl.class.getName());

    @Inject @Config("")
    protected Properties fileMappings;

    @Inject @Config("skin=/skins," +
                    "skinPreview=/skinPreview," +
                    "envelope=/envelopes," +
                    "envelopePreview=/envelopePreview," +
                    "layout=/layouts," +
                    "layoutPreview=/layoutPreview")
    protected Properties mappings = new Properties();

    public Priority getPriority() {
        return Priority.HIGH;
    }

    public void start() throws Exception {
        log.debug("Start");
        Enumeration mappingRoots = mappings.propertyNames();
        while (mappingRoots.hasMoreElements()) {
            String root = (String) mappingRoots.nextElement();
            String prefix = mappings.getProperty(root);
            fileMappings.setProperty(root.trim().replace('\\', '/'), prefix.trim().replace('\\', '/'));
        }
    }

    public Resource getResource(String path) throws Exception {
        return getResource(path, true);
    }

    public Properties getMappings() {
        return mappings;
    }

    public void setMappings(Properties mappings) {
        this.mappings = mappings;
    }

    public Properties getFileMappings() {
        return fileMappings;
    }

    public Resource getResource(String path, boolean useDefaults) throws Exception {
        ResourceName resName = null;
        try {
            resName = ResourceName.getInstance(path);
        } catch (Exception e) {
            log.debug("Error parsing name " + path);
        }
        if (resName == null) {
            log.warn("Cannot accept resource name: " + path);
            return null;
        }
        if (log.isDebugEnabled()) log.debug("Resource Name is " + resName);

        ResourceHolder holder = null;
        String resourceName = Character.toUpperCase(resName.getCategory().charAt(0)) + resName.getCategory().substring(1);
        String methodName = "get" + resourceName;
        GraphicElementManager manager = null;
        try {
            log.debug("Invoking method getManager() on class " + resName.getResourceClass());
            Method managerGetter = resName.getResourceClass().getMethod("getManager", new Class[0]);
            manager = (GraphicElementManager) managerGetter.invoke(null, null);
        } catch (Exception e) {
            log.debug("Resource " + resName.getResourceClass() + " has no manager, so it will be a preview: " + e);
        }
        if (manager == null) {
            //Element has no manager => it is just a preview!! So, resource name does not actually matter, because there
            //is only one preview in session, representing the item being previewed.
            HttpSession session = RequestContext.lookup().getRequest().getSessionObject();
            holder = (ResourceHolder) session.getAttribute(ResourcesPropertiesHandler.PREVIEW_ATTRIBUTE); //Only one preview at a time...
            try {
                return holder.getResource(resName, LocaleManager.currentLocale().getLanguage());
            } catch (Exception e) {
                log.error("Error getting resource from holder " + holder + ":", e);
            }
            return null;
        }

        WorkspaceImpl workspace = null;
        Section section = null;
        Panel panel = null;
        PanelInstance instance = null;

        if (resName.getWorkspaceId() == null)
            workspace = NavigationManager.lookup().getCurrentWorkspace();
        else
            workspace = (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(resName.getWorkspaceId());

        if (workspace != null) {
            if (resName.getSectionId() == null) section = NavigationManager.lookup().getCurrentSection();
            else section = workspace.getSection(resName.getSectionId());
            if (section != null && manager.getElementScopeDescriptor().isAllowedPanel()) {
                if (resName.getPanelId() == null) {
                    panel = RequestContext.lookup().getActivePanel();
                } else {
                    panel = section.getPanel(resName.getPanelId().toString());
                }
            }

            if (manager.getElementScopeDescriptor().isAllowedInstance() && resName.getPanelId() != null) {
                instance = workspace.getPanelInstance(resName.getPanelId());
            }
        }

        if (resName.getCategoryId() != null) {
            log.debug("Getting resource by its categoryId.");
            if (useDefaults) {//Use hierarchy
                if (manager.getElementScopeDescriptor().isAllowedPanel()) {
                    holder = manager.getElement(resName.getCategoryId(), workspace == null ? null : workspace.getId(), section == null ? null : section.getId(), panel == null ? null : panel.getPanelId());
                }
                if (holder == null && manager.getElementScopeDescriptor().isAllowedInstance()) {
                    holder = manager.getElement(resName.getCategoryId(), workspace == null ? null : workspace.getId(), section == null ? null : section.getId(), instance == null ? null : instance.getInstanceId());
                    if (holder == null)
                        holder = manager.getElement(resName.getCategoryId(), workspace == null ? null : workspace.getId(), null, instance == null ? null : instance.getInstanceId());
                }
                if (holder == null && manager.getElementScopeDescriptor().isAllowedSection()) {
                    holder = manager.getElement(resName.getCategoryId(), workspace == null ? null : workspace.getId(), section == null ? null : section.getId(), null);
                }
                if (holder == null && manager.getElementScopeDescriptor().isAllowedWorkspace()) {
                    holder = manager.getElement(resName.getCategoryId(), workspace == null ? null : workspace.getId(), null, null);
                }
                if (holder == null)
                    holder = manager.getElement(resName.getCategoryId(), null, null, null);

                Resource res = getResourceFromHolder(holder, resName);
                if (res != null)
                    return res;
            } else { //Retrieve the resource with given parameters, and don't attempt to use resources hierarchy.
                holder = manager.getElement(resName.getCategoryId(), resName.getWorkspaceId(), resName.getSectionId(), resName.getPanelId());
                Resource res = getResourceFromHolder(holder, resName);
                return res;
            }
        }
        if (panel != null) {
            log.debug("Getting resource from panel.");
            try {
                Method holderGetter = panel.getClass().getMethod(methodName, null);
                holder = (ResourceHolder) holderGetter.invoke(panel, null);
                Resource res = getResourceFromHolder(holder, resName);
                if (res != null)
                    return res;
            } catch (Exception e) {
                log.debug("Error getting " + resourceName + " from panel: " + e);
            }
        }
        if (instance != null) {
            log.debug("Getting resource from panel instance.");
            try {
                Method holderGetter = instance.getClass().getMethod(methodName, null);
                holder = (ResourceHolder) holderGetter.invoke(instance, null);
                Resource res = getResourceFromHolder(holder, resName);
                if (res != null)
                    return res;
            } catch (Exception e) {
                log.debug("Error getting " + resourceName + " from panel instance: " + e);
            }
        }
        if (section != null) {
            log.debug("Getting resource from section.");
            try {
                Method holderGetter = section.getClass().getMethod(methodName, null);
                holder = (ResourceHolder) holderGetter.invoke(section, null);
                Resource res = getResourceFromHolder(holder, resName);
                if (res != null)
                    return res;
            } catch (Exception e) {
                log.debug("Error getting " + resourceName + " from section: " + e);
            }
        }
        if (workspace != null) {
            log.debug("Getting resource from workspace.");
            try {
                Method holderGetter = workspace.getClass().getMethod(methodName, null);
                holder = (ResourceHolder) holderGetter.invoke(workspace, null);
                Resource res = getResourceFromHolder(holder, resName);
                if (res != null)
                    return res;
            } catch (Exception e) {
                log.debug("Error getting " + resourceName + " from workspace: " + e);
            }
        }
        log.debug("Getting default resource from category " + resName.getCategory());
        holder = manager.getDefaultElement();
        return getResourceFromHolder(holder, resName);
    }

    protected Resource getResourceFromHolder(ResourceHolder holder, ResourceName resName) {
        if (holder != null) {
            holder.checkDeployment();
            try {
                return holder.getResource(resName, LocaleManager.currentLocale().getLanguage());
            } catch (Exception e) {
                log.error("Error getting resource from holder " + holder + ":", e);
            }
        }
        return null;
    }
}
