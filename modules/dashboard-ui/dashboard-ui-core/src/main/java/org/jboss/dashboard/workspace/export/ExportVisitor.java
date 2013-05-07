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

import org.jboss.dashboard.commons.xml.XMLNode;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.security.UIPermission;
import org.jboss.dashboard.ui.resources.GraphicElement;

import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.workspace.*;

import javax.enterprise.context.ApplicationScoped;

/**
 * Class that visits workspaces tree and creates a XMLNode object, ready for export procedures.
 */
@ApplicationScoped
public class ExportVisitor implements WorkspaceVisitor {

    public static final String WORKSPACE_EXPORT = "workspaceExport";
    public static final String HEAD = "header";
    public static final String WORKSPACE = "workspace";
    public static final String SECTION = "section";
    public static final String PANEL_INSTANCE = "panelInstance";
    public static final String PANEL = "panel";
    public static final String PERMISSION = "permission";
    public static final String RESOURCE = "resource";
    public static final String RAWCONTENT = "rawcontent";
    public static final String PARAMETER = "param";

    public static final String SECTION_ATTR_ID = "id";
    public static final String SECTION_ATTR_ID_TEMPLATE = "idTemplate";
    public static final String SECTION_ATTR_POSITION = "position";
    public static final String SECTION_ATTR_VISIBLE = "visible";
    public static final String SECTION_ATTR_REGIONSPACING = "regionSpacing";
    public static final String SECTION_ATTR_PANELSPACING = "panelSpacing";
    public static final String SECTION_ATTR_PARENT_ID = "parentId";
    public static final String SECTION_ATTR_FR_URL = "friendlyUrl";
    public static final String SECTION_ATTR_SKIN_ID = "idSkin";
    public static final String SECTION_ATTR_ENVELOPE_ID = "idEnvelope";
    public static final String SECTION_CHILD_TITLE = "title";

    public static final String PANELINSTANCE_ATTR_ID = "id";
    public static final String PANELINSTANCE_ATTR_PROVIDER = "provider";
    public static final String PANELINSTANCE_ATTR_SERIALIZATION = "serialization";

    public static final String WORKSPACE_ATTR_ID = "id";
    public static final String WORKSPACE_ATTR_SKIN_ID = "skinId";
    public static final String WORKSPACE_ATTR_ENVELOPE_ID = "envelopeId";
    public static final String WORKSPACE_ATTR_FR_URL = "friendlyUrl";
    public static final String WORKSPACE_ATTR_HOME_MODE = "homeMode";

    public static final String PANEL_ATTR_ID = "id";
    public static final String PANEL_ATTR_INSTANCE_ID = "instanceId";
    public static final String PANEL_ATTR_REGION_ID = "regionId";
    public static final String PANEL_ATTR_POSITION = "position";

    public static final String PERMISSION_ATTR_PRINCIPAL = "principal";
    public static final String PERMISSION_ATTR_PRINCIPAL_CLASS = "principalClass";
    public static final String PERMISSION_ATTR_PERMISSION_CLASS = "permissionClass";
    public static final String PERMISSION_ATTR_ACTIONS = "actions";
    public static final String PERMISSION_ATTR_READONLY = "readonly";
    public static final String PARAMETER_ATTR_NAME = "name";
    public static final String PARAMETER_ATTR_VALUE = "value";
    public static final String PARAMETER_ATTR_LANG = "lang";

    public static final String RESOURCE_ATTR_ID = "id";
    public static final String RESOURCE_ATTR_CATEGORY = "category";

    private XMLNode rootNode;
    private XMLNode currentNode;

    public ExportVisitor() {
        rootNode = new XMLNode(WORKSPACE_EXPORT, null);
        currentNode = rootNode;
    }

    public XMLNode getRootNode() {
        return rootNode;
    }

    public Object visitWorkspace(Workspace p) throws Exception {
        XMLNode node = new XMLNode(WORKSPACE, currentNode);
        node.addAttribute(WORKSPACE_ATTR_ID, p.getId());
        node.addAttribute(WORKSPACE_ATTR_SKIN_ID, p.getSkinId());
        node.addAttribute(WORKSPACE_ATTR_ENVELOPE_ID, p.getEnvelopeId());
        if (p.getFriendlyUrl() != null) node.addAttribute(WORKSPACE_ATTR_FR_URL, p.getFriendlyUrl());
        node.addAttribute(WORKSPACE_ATTR_HOME_MODE, String.valueOf(p.getHomeSearchMode()));
        currentNode.addChild(node);
        currentNode = node;
        return node;
    }

    public Object visitSection(Section section) throws Exception {
        XMLNode node = new XMLNode(SECTION, currentNode);
        node.addAttribute(SECTION_ATTR_ID, section.getId().toString());
        node.addAttribute(SECTION_ATTR_ID_TEMPLATE, section.getLayoutId());
        node.addAttribute(SECTION_ATTR_POSITION, String.valueOf(section.getPosition()));
        node.addAttribute(SECTION_ATTR_VISIBLE, section.isVisible().toString());
        node.addAttribute(SECTION_ATTR_REGIONSPACING, section.getRegionsCellSpacing().toString());
        node.addAttribute(SECTION_ATTR_PANELSPACING, section.getPanelsCellSpacing().toString());
        if (section.getParentSectionId() != null)
            node.addAttribute(SECTION_ATTR_PARENT_ID, section.getParentSectionId().toString());
        node.addAttribute(SECTION_ATTR_FR_URL, section.getFriendlyUrl());
        node.addAttribute(SECTION_ATTR_SKIN_ID, section.getSkinId());
        node.addAttribute(SECTION_ATTR_ENVELOPE_ID, section.getEnvelopeId());
        Map title = section.getTitle();
        for (Iterator it = title.keySet().iterator(); it.hasNext();) {
            String lang = (String) it.next();
            String value = (String) title.get(lang);
            XMLNode titleNode = new XMLNode(PARAMETER, node);
            titleNode.addAttribute(PARAMETER_ATTR_NAME, SECTION_CHILD_TITLE);
            titleNode.addAttribute(PARAMETER_ATTR_VALUE, value);
            titleNode.addAttribute(PARAMETER_ATTR_LANG, lang);
            node.addChild(titleNode);
        }
        currentNode.addChild(node);
        currentNode = node;
        return node;
    }

    public Object visitPanelInstance(PanelInstance instance) throws Exception {
        XMLNode node = new XMLNode(PANEL_INSTANCE, currentNode);
        node.addAttribute(PANELINSTANCE_ATTR_ID, instance.getInstanceId().toString());
        node.addAttribute(PANELINSTANCE_ATTR_SERIALIZATION, instance.getPersistence() == null ? "" : instance.getPersistence());
        node.addAttribute(PANELINSTANCE_ATTR_PROVIDER, instance.getProviderName());
        if (instance.getProvider().getDriver() instanceof Exportable) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ((Exportable) instance.getProvider().getDriver()).exportContent(instance, bos);
            byte[] content = bos.toByteArray();
            XMLNode contentNode = new XMLNode(RAWCONTENT, node);
            contentNode.setContent(content);
            node.addChild(contentNode);
        }
        currentNode.addChild(node);
        currentNode = node;
        return node;
    }

    public Object visitPanel(Panel panel) throws Exception {
        XMLNode node = new XMLNode(PANEL, currentNode);
        node.addAttribute(PANEL_ATTR_ID, panel.getPanelId().toString());
        node.addAttribute(PANEL_ATTR_INSTANCE_ID, panel.getInstance().getInstanceId().toString());
        node.addAttribute(PANEL_ATTR_REGION_ID, panel.getLayoutRegionId());
        node.addAttribute(PANEL_ATTR_POSITION, String.valueOf(panel.getPosition()));
        currentNode.addChild(node);
        currentNode = node;
        return node;
    }

    public Object visitGraphicElement(GraphicElement resource) throws Exception {
        XMLNode node = new XMLNode(RESOURCE, currentNode);
        node.addAttribute(RESOURCE_ATTR_ID, resource.getId());
        node.addAttribute(RESOURCE_ATTR_CATEGORY, resource.getClass().getName());
        XMLNode rawContentNode = new XMLNode(RAWCONTENT, node);
        rawContentNode.setContent(resource.getZipFile());
        node.addChild(rawContentNode);
        currentNode.addChild(node);
        currentNode = node;
        return node;
    }

    public Object visitPanelParameter(PanelParameter param) throws Exception {
            XMLNode node = new XMLNode(PARAMETER, currentNode);
            node.addAttribute(PARAMETER_ATTR_NAME, param.getIdParameter());
            node.addAttribute(PARAMETER_ATTR_VALUE, StringUtils.defaultString(param.getValue()));
            node.addAttribute(PARAMETER_ATTR_LANG, param.getLanguage());
            currentNode.addChild(node);
            currentNode = node;
            return node;
    }

    public Object visitWorkspaceParameter(WorkspaceParameter param) throws Exception {
        XMLNode node = new XMLNode(PARAMETER, currentNode);
        node.addAttribute(PARAMETER_ATTR_NAME, param.getParameterId());
        node.addAttribute(PARAMETER_ATTR_VALUE, param.getValue());
        node.addAttribute(PARAMETER_ATTR_LANG, param.getLanguage());
        currentNode.addChild(node);
        currentNode = node;
        return node;
    }

    public Object visitPermission(UIPermission perm, Principal relatedPrincipal) throws Exception {
        XMLNode node = new XMLNode(PERMISSION, currentNode);
        node.addAttribute(PERMISSION_ATTR_PERMISSION_CLASS, perm.getClass().getName());
        node.addAttribute(PERMISSION_ATTR_ACTIONS, perm.getActions());
        node.addAttribute(PERMISSION_ATTR_PRINCIPAL_CLASS, perm.getRelatedPrincipal().getClass().getName());
        node.addAttribute(PERMISSION_ATTR_PRINCIPAL, perm.getRelatedPrincipal().getName());
        node.addAttribute(PERMISSION_ATTR_READONLY, Boolean.toString(perm.isReadOnly()));
        currentNode.addChild(node);
        currentNode = node;
        return node;
    }

    public Object endVisit() throws Exception {
        return currentNode = currentNode.getParent();
    }


}
