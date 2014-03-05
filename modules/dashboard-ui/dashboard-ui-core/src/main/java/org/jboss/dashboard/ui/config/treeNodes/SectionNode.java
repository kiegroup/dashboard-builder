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
package org.jboss.dashboard.ui.config.treeNodes;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.config.AbstractNode;
import org.jboss.dashboard.ui.config.TreeNode;
import org.jboss.dashboard.ui.config.components.section.SectionPropertiesHandler;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.Section;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class SectionNode extends AbstractNode {

    @Inject
    private transient Logger log;

    @Inject
    private SectionPropertiesHandler sectionPropertiesHandler;

    @Inject
    private PanelsNode panelsNode;

    @Inject
    private SectionPermissionsNode sectionPermissionsNode;

    private String workspaceId;
    private Long sectionId;

    @PostConstruct
    protected void init() {
        super.setSubnodes(new TreeNode[] {panelsNode, sectionPermissionsNode});
    }

    public String getIconId() {
        return "16x16/ico-menu_pages.png";
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

    public Workspace getWorkspace() throws Exception {
        return UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId);
    }

    protected List listChildren() {
        List childrenNodes = new ArrayList();
        try {
            Section s = getSection();
            for (Section childSection : s.getChildren()) {
                childrenNodes.add(getNewSectionNode(childSection));
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return childrenNodes;
    }

    protected boolean hasDynamicChildren() {
        Section s = null;
        try {
            s = getSection();
            List<Section> children = s.getChildren();
            return !children.isEmpty();
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return false;
    }

    protected TreeNode listChildrenById(String id) {
        try {
            Section s = ((WorkspaceImpl) getWorkspace()).getSection(Long.decode(id));
            if (getSectionId().equals(s.getParentSectionId())) {
                return getNewSectionNode(s);
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return null;
    }

    public SectionPropertiesHandler getSectionPropertiesHandler() {
        return sectionPropertiesHandler;
    }

    public void setSectionPropertiesHandler(SectionPropertiesHandler sectionPropertiesHandler) {
        this.sectionPropertiesHandler = sectionPropertiesHandler;
    }

    protected SectionNode getNewSectionNode(Section section) {
        SectionNode sNode = CDIBeanLocator.getBeanByType(SectionNode.class);
        sNode.setTree(getTree());
        sNode.setParent(this);
        sNode.setWorkspaceId(getWorkspaceId());
        sNode.setSectionId(section.getId());
        return sNode;
    }


    public String getId() {
        return sectionId.toString();
    }

    public Section getSection() throws Exception {
        return UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId).getSection(sectionId);
    }

    public String getName(Locale l) {
        try {
            return (String) LocaleManager.lookup().localize(getSection().getTitle());
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return null;
    }

    public String getDescription(Locale l) {
        return getName(l);
    }

    public boolean onEdit() {
        try {
            getSectionPropertiesHandler().clearFieldErrors();
            getSectionPropertiesHandler().setLang(LocaleManager.lookup().getCurrentLang());
            getSectionPropertiesHandler().setWorkspace(getWorkspace());
            getSectionPropertiesHandler().setSection(getSection());
        } catch (Exception e) {
            log.error("Error: ", e);
            return false;
        }
        return true;
    }

}
