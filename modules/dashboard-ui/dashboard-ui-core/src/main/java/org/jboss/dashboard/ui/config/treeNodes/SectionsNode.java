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

import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.ui.config.AbstractNode;
import org.jboss.dashboard.ui.config.TreeNode;
import org.jboss.dashboard.ui.config.components.sections.SectionsPropertiesHandler;
import org.jboss.dashboard.users.UserStatus;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.security.SectionPermission;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.WorkspaceImpl;

import java.util.ArrayList;
import java.util.List;

public class SectionsNode extends AbstractNode {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(SectionsNode.class.getName());
    private String workspaceId;
    private SectionsPropertiesHandler sectionsPropertiesHandler;

    public Workspace getWorkspace() throws Exception {
        WorkspaceNode parent = (WorkspaceNode) getParent();
        WorkspaceImpl workspace = (WorkspaceImpl) parent.getWorkspace();
        return (workspace);
    }

    public SectionsPropertiesHandler getSectionsPropertiesHandler() {
        return sectionsPropertiesHandler;
    }

    public void setSectionsPropertiesHandler(SectionsPropertiesHandler sectionsPropertiesHandler) {
        this.sectionsPropertiesHandler = sectionsPropertiesHandler;
    }

    public String getWorkspaceId() throws Exception {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    protected List listChildren() {
        try {
            Section[] sections = ((WorkspaceImpl) getWorkspace()).getAllRootSections();
            if (sections != null) {
                List children = new ArrayList();
                for (int i = 0; i < sections.length; i++) {
                    Section section = sections[i];
                    SectionPermission viewPerm = SectionPermission.newInstance(section, SectionPermission.ACTION_VIEW);
                    if (UserStatus.lookup().hasPermission(viewPerm))
                        children.add(getNewSectionNode(section));
                }
                return children;
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return null;
    }

    protected TreeNode listChildrenById(String id) {
        try {
            Section section = ((WorkspaceImpl) getWorkspace()).getSection(Long.decode(id));
            if (section.isRoot())
                return getNewSectionNode(section);
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return null;
    }

    protected boolean hasDynamicChildren() {
        try {
            return getWorkspace().getSectionsCount() > 0;
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return false;
    }

    protected SectionNode getNewSectionNode(Section section) {
        SectionNode sNode = (SectionNode) Factory.lookup("org.jboss.dashboard.ui.config.treeNodes.SectionNode");
        sNode.setTree(getTree());
        sNode.setParent(this);
        sNode.setWorkspaceId(section.getWorkspace().getId());
        sNode.setSectionId(section.getId());
        SectionPermission editSectionPerm = SectionPermission.newInstance(section, SectionPermission.ACTION_EDIT);
        sNode.setExpandible(true);
        sNode.setEditable(UserStatus.lookup().hasPermission(editSectionPerm));
        return sNode;
    }

    private boolean visibilityCalculated = false;

    private void calculateVisibility() {
        if (!visibilityCalculated) {
            visibilityCalculated = true;
            try {
                SectionPermission editSectionPerm = SectionPermission.newInstance(getWorkspace(), SectionPermission.ACTION_EDIT);
                setEditable(UserStatus.lookup().hasPermission(editSectionPerm));
                if (isEditable()) {
                    setExpandible(true);
                } else {  //Will be expandible if I can edit any section
                    Section[] allSections = ((WorkspaceImpl) getWorkspace()).getAllUnsortedSections();
                    for (int i = 0; i < allSections.length; i++) {
                        Section s = allSections[i];
                        SectionPermission editPerm = SectionPermission.newInstance(s, SectionPermission.ACTION_EDIT);
                        if (UserStatus.lookup().hasPermission(editPerm)) {
                            setExpandible(true);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error calculating node visibility attributes ", e);
            }
        }
    }

    public boolean isEditable() {
        calculateVisibility();
        return super.isEditable();
    }

    public boolean isExpandible() {
        calculateVisibility();
        return super.isExpandible();
    }

    public String getId() {
        return "sections";
    }

    public boolean onEdit() {
        try {
            getSectionsPropertiesHandler().clearFieldErrors();
            getSectionsPropertiesHandler().setWorkspace(getWorkspace());
            getSectionsPropertiesHandler().defaultValues();
        } catch (Exception e) {
            log.error("Error: ", e);
            return false;
        }
        return true;
    }

}
