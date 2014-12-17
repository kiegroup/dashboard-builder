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

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.SecurityServices;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.commons.xml.XMLNode;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.security.Policy;
import org.jboss.dashboard.security.principals.DefaultPrincipal;
import org.jboss.dashboard.workspace.export.structure.CreateResult;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.security.*;
import org.jboss.dashboard.ui.resources.GraphicElement;
import org.hibernate.Session;

import javax.enterprise.context.ApplicationScoped;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class that builds workspaces based on the XML Node received.
 */
@ApplicationScoped
public class WorkspaceBuilder {

    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WorkspaceBuilder.class.getName());

    public CreateResult create(XMLNode node) {
        return create(node, false);
    }

    public CreateResult create(final XMLNode node, final boolean onStartup) {
        return create(node, Collections.EMPTY_MAP, onStartup);
    }

    public CreateResult create(final XMLNode node, final Map attributes, final boolean onStartup) {
        final CreateResult result = new CreateResult();

        HibernateTxFragment txFragment = new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                if (ExportVisitor.WORKSPACE.equals(node.getObjectName())) {
                    createWorkspace(result, node, attributes, onStartup);
                } else if (ExportVisitor.RESOURCE.equals(node.getObjectName())) {
                    createResource(result, null, null, null, node, attributes, onStartup);
                } else {
                    throw new IllegalArgumentException("Invalid workspace node.");
                }
            }
        };
        try {
            txFragment.execute();
        } catch (Exception e) {
            log.error("Error:", e);
            return new CreateResult(e);
        }
        return result;
    }


    protected void createWorkspace(CreateResult result, Map attributes, XMLNode node) throws Exception {
        createWorkspace(result, node, attributes, false);
    }

    protected void createWorkspace(CreateResult result, XMLNode node, Map attributes, boolean onStartup) throws Exception {
        String workspaceId = node.getAttributes().getProperty(ExportVisitor.WORKSPACE_ATTR_ID);
        String skinId = node.getAttributes().getProperty(ExportVisitor.WORKSPACE_ATTR_SKIN_ID);
        String envelopeId = node.getAttributes().getProperty(ExportVisitor.WORKSPACE_ATTR_ENVELOPE_ID);
        String friendlyUrl = node.getAttributes().getProperty(ExportVisitor.WORKSPACE_ATTR_FR_URL);
        String homeMode = node.getAttributes().getProperty(ExportVisitor.WORKSPACE_ATTR_HOME_MODE);

        log.debug("Creando workspace " + workspaceId);
        final WorkspaceImpl workspace = new WorkspaceImpl();
        workspace.setId(workspaceId);
        workspace.setSkinId(skinId);
        workspace.setEnvelopeId(envelopeId);
        workspace.setFriendlyUrl(friendlyUrl);
        workspace.setHomeSearchMode(Integer.parseInt(homeMode));
        // TODO. Modify export format so it can be read from them
        Set<String> allowedPanelIds = new HashSet<String>();
        if (onStartup) allowedPanelIds.add("*");
        workspace.setPanelProvidersAllowed(allowedPanelIds);

        WorkspacesManager workspacesManager = UIServices.lookup().getWorkspacesManager();
        synchronized (workspacesManager) {
            if (onStartup) {
                // Delete the old workspace (if any)
                Workspace currentWorkspace = workspacesManager.getWorkspace(workspaceId);
                if (currentWorkspace != null) {
                    workspacesManager.delete(currentWorkspace);
                }
            }
            // Register the new one
            workspacesManager.addNewWorkspace(workspace);
        }

        //Children
        for (XMLNode child : node.getChildren()) {
            if (ExportVisitor.PARAMETER.equals(child.getObjectName())) {
                createWorkspaceParameter(child, workspace);
            } else if (ExportVisitor.PANEL_INSTANCE.equals(child.getObjectName())) {
                createPanelInstance(result, child, workspace, attributes, onStartup);
            } else if (ExportVisitor.RESOURCE.equals(child.getObjectName())) {
                createResource(result, workspace.getId(), null, null, child, attributes, onStartup);
            } else if (ExportVisitor.SECTION.equals(child.getObjectName())) {
                createSection(result, workspace, child, attributes, onStartup);
            } else if (ExportVisitor.PERMISSION.equals(child.getObjectName())) {
                createPermission(result, workspace, workspace, child, attributes);
            }
        }

        if (!onStartup) {
            synchronized (workspacesManager) {
                Map<String, String> names = workspacesManager.generateUniqueWorkspaceName(workspace);
                workspace.setName(names);
            }
        }

        //Save the policy, as probably everybody modified it...
        Policy securityPolicy = SecurityServices.lookup().getSecurityPolicy();
        securityPolicy.save();

        result.setObjectCreated(workspace);
    }

    protected void createPermission(CreateResult result, Workspace workspace, Object resource, XMLNode node, Map attributes) throws Exception {
        String actions = node.getAttributes().getProperty(ExportVisitor.PERMISSION_ATTR_ACTIONS);
        String permClass = node.getAttributes().getProperty(ExportVisitor.PERMISSION_ATTR_PERMISSION_CLASS);
        String principalName = node.getAttributes().getProperty(ExportVisitor.PERMISSION_ATTR_PRINCIPAL);
        String principalClass = node.getAttributes().getProperty(ExportVisitor.PERMISSION_ATTR_PRINCIPAL_CLASS);
        String readonly = node.getAttributes().getProperty(ExportVisitor.PERMISSION_ATTR_READONLY);

        UIPermission permission = null;
        if (WorkspacePermission.class.getName().equals(permClass)) {
            permission = WorkspacePermission.newInstance(resource, actions);
        } else if (SectionPermission.class.getName().equals(permClass)) {
            permission = SectionPermission.newInstance(resource, actions);
        } else if (PanelPermission.class.getName().equals(permClass)) {
            permission = PanelPermission.newInstance(resource, actions);
        }

        if (principalClass.equals("org.jboss.dashboard.security.UserPrincipal")) {
            principalClass = "org.jboss.dashboard.security.principals.UserPrincipal";
        } else if (principalClass.equals("org.jboss.dashboard.security.UserGroupPrincipal")) {
            principalClass = "org.jboss.dashboard.security.principals.UserGroupPrincipal";
        } else if (principalClass.equals("org.jboss.dashboard.security.BehaviourPrincipal")) {
            principalClass = "org.jboss.dashboard.security.principals.UserGroupPrincipal";
        }

        principalName = transformPrincipalName(principalName, principalClass, attributes);
        permission.setReadOnly(readonly != null && readonly.equals("true"));

        Constructor principalConstructor = Class.forName(principalClass).getConstructor(new Class[]{String.class});
        DefaultPrincipal principal = (DefaultPrincipal) principalConstructor.newInstance(new Object[]{principalName});
        Policy securityPolicy = SecurityServices.lookup().getSecurityPolicy();
        securityPolicy.addPermission(principal, permission);
        securityPolicy.save();
    }

    /**
     * Transforms the principal name, as it might be modified (group, role, ...)
     *
     * @param principalName
     * @param principalClass
     * @param attributes
     * @return
     */
    protected String transformPrincipalName(String principalName, String principalClass, Map attributes) {
         return principalName; //Default implementation leaves it unchanged.
    }


    protected void createSection(CreateResult result, WorkspaceImpl workspace, XMLNode node, Map attributes, boolean onStartup) throws Exception {
        String id = node.getAttributes().getProperty(ExportVisitor.SECTION_ATTR_ID);
        String idTemplate = node.getAttributes().getProperty(ExportVisitor.SECTION_ATTR_ID_TEMPLATE);
        String position = node.getAttributes().getProperty(ExportVisitor.SECTION_ATTR_POSITION);
        String visible = node.getAttributes().getProperty(ExportVisitor.SECTION_ATTR_VISIBLE);
        String regionSpacing = node.getAttributes().getProperty(ExportVisitor.SECTION_ATTR_REGIONSPACING);
        String panelSpacing = node.getAttributes().getProperty(ExportVisitor.SECTION_ATTR_PANELSPACING);
        String parentId = node.getAttributes().getProperty(ExportVisitor.SECTION_ATTR_PARENT_ID);
        String friendlyUrl = node.getAttributes().getProperty(ExportVisitor.SECTION_ATTR_FR_URL);
        String idSkin = node.getAttributes().getProperty(ExportVisitor.SECTION_ATTR_SKIN_ID);
        String idEnvelope = node.getAttributes().getProperty(ExportVisitor.SECTION_ATTR_ENVELOPE_ID);

        Section section = new Section();
        section.setId(Long.decode(id));
        section.setLayoutId(idTemplate);
        section.setPosition(Integer.decode(position).intValue());
        section.setVisible(Boolean.valueOf(visible));
        section.setRegionsCellSpacing(Integer.decode(regionSpacing));
        section.setPanelsCellSpacing(Integer.decode(panelSpacing));
        if (parentId != null)
            section.setParentSectionId(Long.decode(parentId));
        section.setFriendlyUrl(friendlyUrl);
        section.setSkinId(idSkin);
        section.setEnvelopeId(idEnvelope);
        section.setWorkspace(workspace);
        UIServices.lookup().getSectionsManager().store(section);
        workspace.addSection(section);
        UIServices.lookup().getWorkspacesManager().store(workspace);

        //Children
        for (XMLNode child : node.getChildren()) {
            if (ExportVisitor.PARAMETER.equals(child.getObjectName())) {
                String name = child.getAttributes().getProperty(ExportVisitor.PARAMETER_ATTR_NAME);
                if (ExportVisitor.SECTION_CHILD_TITLE.equals(name)) {
                    String value = child.getAttributes().getProperty(ExportVisitor.PARAMETER_ATTR_VALUE);
                    String lang = child.getAttributes().getProperty(ExportVisitor.PARAMETER_ATTR_LANG);
                    section.setTitle(value, lang);
                }
            } else if (ExportVisitor.RESOURCE.equals(child.getObjectName())) {
                createResource(result, workspace.getId(), section.getId(), null, child, attributes, onStartup);
            } else if (ExportVisitor.PANEL.equals(child.getObjectName())) {
                createPanel(result, section, child, attributes, onStartup);
            } else if (ExportVisitor.PERMISSION.equals(child.getObjectName())) {
                createPermission(result, section.getWorkspace(), section, child, attributes);
            }
        }
    }

    protected void createPanel(CreateResult result, Section section, XMLNode node, Map attributes, boolean onStartup) throws Exception {
        String id = node.getAttributes().getProperty(ExportVisitor.PANEL_ATTR_ID);
        String instanceId = node.getAttributes().getProperty(ExportVisitor.PANEL_ATTR_INSTANCE_ID);
        String regionId = node.getAttributes().getProperty(ExportVisitor.PANEL_ATTR_REGION_ID);
        String position = node.getAttributes().getProperty(ExportVisitor.PANEL_ATTR_POSITION);
        Panel panel = new Panel();
        panel.setSection(section);
        panel.setPosition(Integer.decode(position).intValue());
        panel.setPanelId(Long.decode(id));
        panel.setInstanceId(Long.decode(instanceId));
        if (regionId != null) {
            panel.getProvider().getDriver().fireBeforePanelPlacedInRegion(panel, section.getLayout().getRegion(regionId));
            panel.setLayoutRegionId(regionId);
        }
        UIServices.lookup().getPanelsManager().store(panel);
        // Add panel to section
        section.assignPanel(panel, regionId);
        UIServices.lookup().getSectionsManager().store(section);
        panel.getProvider().getDriver().fireAfterPanelPlacedInRegion(panel, null);
        for (XMLNode child : node.getChildren()) {
            if (ExportVisitor.PERMISSION.equals(child.getObjectName())) {
                createPermission(result, panel.getWorkspace(), panel, child, attributes);
            }
        }
    }


    protected void createWorkspaceParameter(XMLNode node, WorkspaceImpl workspace) {
        String paramName = node.getAttributes().getProperty(ExportVisitor.PARAMETER_ATTR_NAME);
        String paramValue = node.getAttributes().getProperty(ExportVisitor.PARAMETER_ATTR_VALUE);
        String paramLang = node.getAttributes().getProperty(ExportVisitor.PARAMETER_ATTR_LANG);
        if (paramLang != null && paramLang.trim().equals("")) paramLang = null;
        log.debug("Adding workspace parameter: " + paramName + "=" + paramValue + " (" + paramLang + ")");
        if (paramValue != null && paramValue.trim().length() > 0) {
            workspace.getWorkspaceParams().add(new WorkspaceParameter(paramName, workspace, paramLang, paramValue));
        }
    }


    protected void createPanelInstanceParameter(XMLNode node, PanelInstance pi) {
        String paramName = node.getAttributes().getProperty(ExportVisitor.PARAMETER_ATTR_NAME);
        String paramValue = node.getAttributes().getProperty(ExportVisitor.PARAMETER_ATTR_VALUE);
        String paramLang = node.getAttributes().getProperty(ExportVisitor.PARAMETER_ATTR_LANG);
        if (paramLang == null || (paramLang.trim().equals(""))) paramLang = " ";
        log.debug("Adding panel instance parameter: " + paramName + "=" + paramValue + " (" + paramLang + ")");
        if (paramValue != null && paramValue.trim().length() > 0) {
            pi.setParameterValue(paramName, paramValue, paramLang);
        }
    }

    protected void createResource(CreateResult result, String workspaceId, Long sectionId, Long panelId, XMLNode node, Map attributes, boolean onStartup) throws Exception {
        String className = node.getAttributes().getProperty(ExportVisitor.RESOURCE_ATTR_CATEGORY);
        String id = node.getAttributes().getProperty(ExportVisitor.RESOURCE_ATTR_ID);
        byte[] rawContent = (node.getChildren().get(0)).getContent();

        GraphicElement element = (GraphicElement) Class.forName(className).newInstance();

        if (element.getInstanceManager().getElement(id, workspaceId, sectionId, panelId) != null) {
            log.warn("Refusing to overwrite existing resource with id " + id);
            result.getWarnings().add("refusingOverwriteResource");
            result.getWarningArguments().add(new Object[]{element.getCategoryName(), id, workspaceId, sectionId, panelId});
        } else {
            element.setId(id);
            element.setWorkspaceId(workspaceId);
            element.setSectionId(sectionId);
            element.setPanelId(panelId);
            element.setZipFile(rawContent);
            element.getInstanceManager().createOrUpdate(element);
            if (workspaceId == null)
                result.setObjectCreated(element);
        }
    }

    protected void createPanelInstance(CreateResult result, XMLNode node, WorkspaceImpl workspace, Map attributes, boolean onStartup) throws Exception {
        String id = node.getAttributes().getProperty(ExportVisitor.PANELINSTANCE_ATTR_ID);
        String serialization = node.getAttributes().getProperty(ExportVisitor.PANELINSTANCE_ATTR_SERIALIZATION);
        String provider = node.getAttributes().getProperty(ExportVisitor.PANELINSTANCE_ATTR_PROVIDER);
        log.debug("Adding panelInstance workspaceId=" + workspace.getId() + ", id=" + id + ", type=" + provider);
        PanelInstance pi = new PanelInstance();
        pi.setProviderName(provider);
        if ("?".equals(pi.getProvider().getGroup())) {
            result.getWarnings().add("providerNotFound");
            result.getWarningArguments().add(new Object[]{provider});
        }
        pi.setId(id);
        pi.setPersistence(serialization);
        pi.setWorkspace(workspace);
        UIServices.lookup().getPanelsManager().store(pi);
        pi.init();
        //Children
        for (XMLNode child : node.getChildren()) {
            if (ExportVisitor.PARAMETER.equals(child.getObjectName())) {
                createPanelInstanceParameter(child, pi);
            } else if (ExportVisitor.RESOURCE.equals(child.getObjectName())) {
                createResource(result, workspace.getId(), null, pi.getInstanceId(), child, attributes, onStartup);
            } else if (ExportVisitor.RAWCONTENT.equals(child.getObjectName())) {
                if (pi.getProvider().getDriver() instanceof Exportable) {
                    try {
                        ((Exportable) pi.getProvider().getDriver()).importContent(pi, new ByteArrayInputStream(child.getContent()));
                    }
                    catch (Exception e) {
                        result.getWarnings().add("panelFailedToImport");
                        result.getWarningArguments().add(new Object[]{LocaleManager.lookup().localize(pi.getTitle()), e, e.getMessage()});
                    }
                }
            }
        }
        workspace.addPanelInstance(pi);
        UIServices.lookup().getWorkspacesManager().store(workspace);
    }

}
