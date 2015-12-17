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
package org.jboss.dashboard.ui.config.components.sections;

import org.jboss.dashboard.Application;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.BeanHandler;
import org.jboss.dashboard.ui.config.ConfigurationTree;
import org.jboss.dashboard.ui.config.ConfigurationTreeStatus;
import org.jboss.dashboard.ui.formatters.FactoryURL;
import org.jboss.dashboard.ui.components.MessagesComponentHandler;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.components.SectionsHandler;
import org.jboss.dashboard.ui.config.Tree;
import org.jboss.dashboard.ui.config.TreeNode;
import org.jboss.dashboard.ui.config.TreeStatus;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.workspace.copyoptions.BasicSectionCopyOption;
import org.jboss.dashboard.workspace.copyoptions.CopyOption;
import org.jboss.dashboard.workspace.copyoptions.SectionCopyOption;
import org.jboss.dashboard.security.WorkspacePermission;
import org.jboss.dashboard.security.SectionPermission;
import org.jboss.dashboard.users.UserStatus;
import org.hibernate.Session;
import org.slf4j.Logger;

import java.io.File;
import java.util.*;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

@SessionScoped
public class SectionsPropertiesHandler extends BeanHandler {

    public static final String ACTION_MOVE_UP = "action_move_up";
    public static final String ACTION_MOVE_DOWN = "action_move_down";
    public static final String ACTION_PROMOVE_PARENT = "action_promove_parent";
    public static final String ACTION_SET_PARENT = "action_set_parent";
    public static final String ACTION_MOVE_SELECTED = "action_move_selected";
    public static final String ACTION_CREATE = "action_create_section";
    public static final String ACTION_CLONE = "action_clone_section";
    public static final String ACTION_DELETE = "action_delete_section";
    public static final String ACTION_GO_TO_PROPERTIES = "action_go_to_properties";
    public static final String ACTION_GO_TO_PANELS = "action_go_to_panels";
    public static final String ACTION_GO_TO_PERMISSIONS = "action_go_to_permissions";

    public static final String ACTION_SAVE = "action_save";
    public static final String ACTION_CANCEL = "action_cancel";
    public static final String ACTION_PREVIEW = "action_preview";

    public static final String COPY_MODE = "copyMode";

    @Inject
    private transient Logger log;

    @Inject
    private SectionsHandler sectionsHandler;

    @Inject
    private ConfigurationTree configTree;

    @Inject
    private ConfigurationTreeStatus treeStatus;

    private String workspaceId;
    private String selectedSectionId;
    private Map<String, String> titleMap;
    private String title;
    private String parent;
    private boolean visible;
    private String skin;
    private String envelope;
    private String url;
    private String layout;
    private Boolean createSection = Boolean.FALSE;
    private Boolean duplicateSection = Boolean.FALSE;
    private int regionsCellSpacing;
    private int panelsCellSpacing;
    private String action;
    private ArrayList<String> errorPermission = new ArrayList<String>();
    private Boolean moveLoop = Boolean.FALSE;

    public CopyManager getCopyManager() {
        return UIServices.lookup().getCopyManager();
    }

    public UserStatus getUserStatus() {
        return UserStatus.lookup();
    }

    public Boolean getMoveLoop() {
        return moveLoop;
    }

    public void setMoveLoop(Boolean moveLoop) {
        this.moveLoop = moveLoop;
    }

    public ArrayList<String> getErrorPermission() {
        return errorPermission;
    }

    public void setErrorPermission(ArrayList<String> errorPermission) {
        this.errorPermission = errorPermission;
    }

    public Tree getConfigTree() {
        return configTree;
    }

    public TreeStatus getTreeStatus() {
        return treeStatus;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Boolean getCreateSection() {
        return createSection;
    }

    public void setCreateSection(Boolean createSection) {
        this.createSection = createSection;
    }

    public Boolean getDuplicateSection() {
        return duplicateSection;
    }

    public void setDuplicateSection(Boolean duplicateSection) {
        this.duplicateSection = duplicateSection;
    }

    public void setWorkspace(Workspace p) {
        workspaceId = p.getId();
    }

    public Workspace getWorkspace() throws Exception {
        return UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId);
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getSelectedSectionId() {
        return selectedSectionId;
    }

    public void setSelectedSectionId(String sectionId) {
        this.selectedSectionId = sectionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Map<String, String> getTitleMap() {
        return titleMap;
    }

    public void setTitleMap(Map<String, String> titleMap) {
        this.titleMap = titleMap;
    }

    public boolean getVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getEnvelope() {
        return envelope;
    }

    public void setEnvelope(String envelope) {
        this.envelope = envelope;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public int getRegionsCellSpacing() {
        return regionsCellSpacing;
    }

    public void setRegionsCellSpacing(int regionsCellSpacing) {
        this.regionsCellSpacing = regionsCellSpacing;
    }

    public int getPanelsCellSpacing() {
        return panelsCellSpacing;
    }

    public void setPanelsCellSpacing(int panelsCellSpacing) {
        this.panelsCellSpacing = panelsCellSpacing;
    }

    public SectionsHandler getSectionsHandler() {
        return sectionsHandler;
    }

    public void setSectionsHandler(SectionsHandler sectionsHandler) {
        this.sectionsHandler = sectionsHandler;
    }

    public void defaultValues() {
        title = null;
        skin = "";
        envelope = "";
        layout = "1_col";
        parent = null;
        titleMap = new HashMap<String, String>();
    }

    public void actionManageSection(CommandRequest request) throws Exception {

        if (action.equals(ACTION_CREATE))
            actionStartNew();
        else if (action.equals(ACTION_DELETE))
            actionDeleteSection(request);
        else if (action.equals(ACTION_CLONE))
            actionStartDuplicate(request);
        else if (action.equals(ACTION_GO_TO_PROPERTIES))
            actionPageConfig(request);
        else if (action.equals(ACTION_MOVE_DOWN))
            actionMoveRight(request);
        else if (action.equals(ACTION_MOVE_UP))
            actionMoveLeft(request);
        else if (action.equals(ACTION_SET_PARENT))
            actionSetParent(request);
        else if (action.equals(ACTION_PROMOVE_PARENT))
            actionPromoveParent(request);
        else if (action.equals(ACTION_GO_TO_PANELS))
            actionGoToPagePanels(request);
        else if (action.equals(ACTION_GO_TO_PERMISSIONS))
            actionGoToPagePermissions(request);
        else if (action.equals(ACTION_MOVE_SELECTED))
            actionMoveSelected(request);

    }

    public void actionGoToPagePanels(CommandRequest request) throws Exception {
        WorkspaceImpl workspace = (WorkspaceImpl) getWorkspace();
        WorkspacePermission workspacePerm = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_ADMIN);

        if (getUserStatus().hasPermission(workspacePerm)) {
            Section section = workspace.getSection(new Long(getSelectedSectionId()));
            TreeNode currentNode = getTreeStatus().getLastEditedNode(getConfigTree());
            StringBuffer path = new StringBuffer();
            path.append(currentNode.getPath()).append("/").append(getSectionIds(section));
            path.append("/panels");
            getTreeStatus().navigateToPath(getConfigTree(), path.toString());
        } else {
            getErrorPermission().add("errorGoToPanels");
        }
    }

    public void actionGoToPagePermissions(CommandRequest request) throws Exception {
        WorkspaceImpl workspace = (WorkspaceImpl) getWorkspace();
        Section section = workspace.getSection(new Long(getSelectedSectionId()));
        SectionPermission sectionPerm = SectionPermission.newInstance(section, SectionPermission.ACTION_EDIT);

        if (getUserStatus().hasPermission(sectionPerm)) {
            TreeNode currentNode = getTreeStatus().getLastEditedNode(getConfigTree());
            StringBuffer path = new StringBuffer();
            path.append(currentNode.getPath()).append("/").append(getSectionIds(section));
            path.append("/permissions");
            getTreeStatus().navigateToPath(getConfigTree(), path.toString());
        } else {
            getErrorPermission().add("errorGoToPagePermissions");
        }
    }


    public void actionPageConfig(CommandRequest request) throws Exception {
        WorkspaceImpl workspace = (WorkspaceImpl) getWorkspace();
        Section section = workspace.getSection(new Long(getSelectedSectionId()));

        SectionPermission sectionPerm = SectionPermission.newInstance(section, SectionPermission.ACTION_EDIT);

        if (getUserStatus().hasPermission(sectionPerm)) {
            TreeNode currentNode = getTreeStatus().getLastEditedNode(getConfigTree());
            StringBuffer path = new StringBuffer();
            path.append(currentNode.getPath()).append("/").append(getSectionIds(section));
            getTreeStatus().navigateToPath(getConfigTree(), path.toString());
        } else {
            getErrorPermission().add("errorEditSection");
        }

    }

    protected String getSectionIds(Section section) throws Exception {
        StringBuffer sb = new StringBuffer();
        while (section != null) {
            sb.insert(0, section.getId());
            section = section.getParent();
            if (section != null) {
                sb.insert(0, "/");
            }
        }
        return sb.toString();
    }

    public void actionStartNew() {
        this.setCreateSection(Boolean.TRUE);
        this.setDuplicateSection(Boolean.FALSE);
    }

    public void actionStartDuplicate(final CommandRequest request) {
        this.setDuplicateSection(Boolean.TRUE);
        this.setCreateSection(Boolean.FALSE);
    }

    /**
     * Duplicates Section in workspace
     */
    public void actionDuplicateSection(final CommandRequest request) {
        MessagesComponentHandler messagesHandler = MessagesComponentHandler.lookup();
        try {
            if (action != null && action.equals(ACTION_SAVE)) {
                final Long selectedSectionId = Long.decode(getSelectedSectionId());
                if (selectedSectionId != null && selectedSectionId.longValue() != 0L) {
                    HibernateTxFragment txFragment = new HibernateTxFragment() {
                        protected void txFragment(Session session) throws Exception {
                            log.debug("Duplicating section " + selectedSectionId.toString());
                            WorkspaceImpl workspace = (WorkspaceImpl) getWorkspace();
                            Section section = null;
                            section = workspace.getSection(selectedSectionId);
                            if (section != null) {
                                // Security check.
                                WorkspacePermission sectionPerm = WorkspacePermission.newInstance(workspace, WorkspacePermission.ACTION_CREATE_PAGE);
                                getUserStatus().checkPermission(sectionPerm);

                                // Duplicate
                                SectionCopyOption sco = getCopyOptions(request);
                                Section sectionCopy = getCopyManager().copy(section, workspace, sco);
                                Map<String, String> title = section.getTitle();
                                for (String lang : title.keySet()) {
                                    String desc = title.get(lang);
                                    String prefix = "Copia de ";
                                    prefix = lang.equals("en") ? "Copy of " : prefix;
                                    sectionCopy.setTitle(prefix + desc, lang);
                                }
                                UIServices.lookup().getSectionsManager().store(sectionCopy);
                            }
                        }
                    };

                    txFragment.execute();
                    messagesHandler.addMessage("ui.alert.sectionCopy.OK");
                }
            }
            this.setDuplicateSection(Boolean.FALSE);
            this.setCreateSection(Boolean.FALSE);
            this.setSelectedSectionId(null);
            defaultValues();
        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
            messagesHandler.clearAll();
            messagesHandler.addError("ui.alert.sectionCopy.KO");
        }
    }

    public void actionDeleteSection(CommandRequest request) {
        try {
            Section section = ((WorkspaceImpl) getWorkspace()).getSection(new Long(getSelectedSectionId()));
            SectionPermission sectionPerm = SectionPermission.newInstance(section, SectionPermission.ACTION_DELETE);

            if (getUserStatus().hasPermission(sectionPerm)) {
                getSectionsHandler().deleteSection((WorkspaceImpl) getWorkspace(), section);

                this.setDuplicateSection(Boolean.FALSE);
                this.setCreateSection(Boolean.FALSE);
                this.setSelectedSectionId(null);
            } else {
                getErrorPermission().add("errorDeleteSection");
            }
        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
        }
    }


    public void actionMoveSelected(final CommandRequest request) {

        try {
            HibernateTxFragment txFragment = new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {

                    String sectionId = request.getParameter("MoveSelected");

                    WorkspaceImpl workspace = (WorkspaceImpl) getWorkspace();
                    Section section;
                    Section sectionToMove;
                    if (sectionId != null && getSelectedSectionId() != null) {

                        sectionToMove = workspace.getSection(new Long(getSelectedSectionId()));
                        section = workspace.getSection(new Long(sectionId));

                        if (checkMoveLoop(section.getParent(), sectionToMove)) {
                            if (section != null && sectionToMove != null) {
                                changeSectionSelected(section, sectionToMove);
                            }
                        }
                    }
                }
            };
            txFragment.execute();
            this.setDuplicateSection(Boolean.FALSE);
            this.setCreateSection(Boolean.FALSE);
            this.setSelectedSectionId(null);


        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    /**
     * Moves selected region right
     */
    public void actionMoveRight(final CommandRequest request) {
        try {

            HibernateTxFragment txFragment = new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    WorkspaceImpl workspace = (WorkspaceImpl) getWorkspace();
                    Section section;
                    if (getSelectedSectionId() != null) {
                        section = workspace.getSection(new Long(getSelectedSectionId()));
                        if (section != null) {
                            Section parent = section.getParent();
                            if (parent != null)
                                parent.moveDown(section);
                            else
                                workspace.moveDown(section);
                        }
                    }
                }
            };

            txFragment.execute();
            this.setDuplicateSection(Boolean.FALSE);
            this.setCreateSection(Boolean.FALSE);
            this.setSelectedSectionId(null);

        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
        }
    }

    /**
     * Moves selected region left
     */
    public void actionMoveLeft(final CommandRequest request) {
        try {

            HibernateTxFragment txFragment = new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    WorkspaceImpl workspace = (WorkspaceImpl) getWorkspace();
                    Section section = null;
                    if (getSelectedSectionId() != null) {
                        section = workspace.getSection(new Long(getSelectedSectionId()));
                        if (section != null) {
                            Section parent = section.getParent();
                            if (parent != null)
                                parent.moveUp(section);
                            else
                                workspace.moveUp(section);

                        }
                    }
                }
            };

            txFragment.execute();
            this.setDuplicateSection(Boolean.FALSE);
            this.setCreateSection(Boolean.FALSE);
            this.setSelectedSectionId(null);

        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
        }
    }

    public void actionPromoveParent(final CommandRequest request) {
        try {

            HibernateTxFragment txFragment = new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    WorkspaceImpl workspace = (WorkspaceImpl) getWorkspace();
                    Section section;
                    if (getSelectedSectionId() != null) {
                        section = workspace.getSection(Long.decode(getSelectedSectionId()));
                        if (section != null) {
                            Section oldParent = section.getParent();
                            if (oldParent != null) {
                                changeParent(section, oldParent, oldParent.getParent());
                                UIServices.lookup().getSectionsManager().store(section);
                            }
                        }
                    }
                }
            };

            txFragment.execute();
            this.setDuplicateSection(Boolean.FALSE);
            this.setCreateSection(Boolean.FALSE);
            this.setSelectedSectionId(null);

        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
        }
    }

    public void actionSetParent(final CommandRequest request) {
        try {

            HibernateTxFragment txFragment = new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    WorkspaceImpl workspace = (WorkspaceImpl) getWorkspace();
                    Section section;
                    if (getSelectedSectionId() != null) {
                        section = workspace.getSection(Long.decode(getSelectedSectionId()));
                        if (section != null) {
                            Section oldParent = section.getParent();

                            Section newParent = null;
                            Section[] brothers;
                            if (oldParent != null)
                                brothers = workspace.getAllChildSections(oldParent.getId());
                            else
                                brothers = workspace.getAllRootSections();
                            if (brothers != null) {
                                for (int i = 0; i < brothers.length && newParent == null; i++) {
                                    if (i > 0 && brothers[i].getId().equals(section.getId()))
                                        newParent = brothers[i - 1];
                                }
                            }

                            if (newParent != null) {
                                changeParent(section, oldParent, newParent);
                                UIServices.lookup().getSectionsManager().store(section);
                            }
                        }
                    }
                }
            };

            txFragment.execute();
            this.setDuplicateSection(Boolean.FALSE);
            this.setCreateSection(Boolean.FALSE);
            this.setSelectedSectionId(null);

        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
        }
    }

    public synchronized void actionCreateSection(CommandRequest request) throws Exception {
        MessagesComponentHandler messagesHandler = MessagesComponentHandler.lookup();

        if (action != null && (action.equals(ACTION_SAVE) || action.equals(ACTION_PREVIEW))) {

            setLangTitle(request);

            if (action.equals(ACTION_PREVIEW)) return;

            if (validateBeforeEdition()) {
                try {
                    // Create a new section instance.
                    final Section newSection = new Section();
                    WorkspaceImpl workspace = (WorkspaceImpl) getWorkspace();
                    newSection.setTitle(titleMap);
                    if ((parent != null && !"".equals(parent))) {
                        newSection.setParent(workspace.getSection(new Long(parent)));
                    }
                    newSection.setVisible(Boolean.TRUE);
                    newSection.setSkinId(skin);
                    newSection.setEnvelopeId(envelope);
                    newSection.setRegionsCellSpacing(new Integer(2));
                    newSection.setPanelsCellSpacing(new Integer(2));
                    newSection.setLayoutId(layout);

                    // Make changes persistent
                    new HibernateTxFragment() {
                    protected void txFragment(Session session) throws Exception {
                        ((WorkspaceImpl) getWorkspace()).addSection(newSection);
                        UIServices.lookup().getSectionsManager().store(newSection);
                        UIServices.lookup().getWorkspacesManager().store(getWorkspace());
                    }}.execute();

                    // Finish creation action
                    this.setDuplicateSection(Boolean.FALSE);
                    this.setCreateSection(Boolean.FALSE);
                    this.setSelectedSectionId(null);
                    defaultValues();

                    // Print an ok message and move the user into the new page
                    messagesHandler.addMessage("ui.alert.sectionCreation.OK");
                    NavigationManager.lookup().setCurrentSection(newSection);
                } catch (Exception e) {
                    log.error("Error creating section: ", e);
                    messagesHandler.clearAll();
                    messagesHandler.addError("ui.alert.sectionCreation.KO");
                }
            }
        } else {
            this.setDuplicateSection(Boolean.FALSE);
            this.setCreateSection(Boolean.FALSE);
            this.setSelectedSectionId(null);
            defaultValues();
        }
    }

    public void setLangTitle(CommandRequest request) throws Exception {
        titleMap = new HashMap<String, String>();
        Map<String, String[]> params = request.getRequestObject().getParameterMap();
        for (String paramName : params.keySet()) {
            if (paramName.startsWith("name_")) {
                String lang = paramName.substring("name_".length());
                String paramValue = request.getParameter(paramName);
                if (paramValue != null && !"".equals(paramValue))
                    titleMap.put(lang, paramValue);
            }
        }
    }

    protected boolean validateBeforeEdition() {
        MessagesComponentHandler messagesHandler = MessagesComponentHandler.lookup();
        messagesHandler.clearAll();
        boolean valid = validate();
        if (!valid) messagesHandler.getErrorsToDisplay().add(0, "ui.alert.sectionCreation.KO");
        return valid;
    }

    protected boolean validate() {
        MessagesComponentHandler messagesHandler = MessagesComponentHandler.lookup();
        try {
            if (titleMap == null || titleMap.isEmpty()) {
                addFieldError(new FactoryURL(getBeanName(), "title"), null, title);
                messagesHandler.addError("ui.alert.sectionErrors.title");
            }

            if (!isValidURL(url)) {
                addFieldError(new FactoryURL(getBeanName(), "url"), null, url);
                messagesHandler.addError("ui.alert.sectionErrors.url");
            }

            return getFieldErrors().isEmpty();
        } catch (Exception e) {
            log.error("Error: ", e);
            return false;
        }
    }

    public boolean checkMoveLoop(Section newParent, Section sectionToMove) throws Exception {

        boolean found = false;

        while (newParent != null && !found) {
            if (newParent.getSectionId().equals(sectionToMove.getSectionId())) {
                found = true;
            } else {
                newParent = newParent.getParent();
            }
        }

        if (found) {
            setMoveLoop(found);
        }
        return !found;
    }

    private void changeSectionSelected(Section section, Section sectionToMove) throws Exception {
        try {

            Section newParent = section.getParent();
            Section oldParent = sectionToMove.getParent();


            if (newParent == null) {
                // X -> root
                if (oldParent == null) {
                    // root -> root
                    reorderMovedSection(section, sectionToMove, newParent, oldParent, true);

                } else {
                    // section -> root
                    reorderMovedSection(section, sectionToMove, newParent, oldParent, false);

                }
            } else {
                // X -> section
                if (oldParent == null) {
                    // root -> section
                    reorderMovedSection(section, sectionToMove, newParent, oldParent, false);

                } else {
                    // section -> section
                    if (newParent.getSectionId().equals(oldParent.getSectionId())) {

                        reorderMovedSection(section, sectionToMove, newParent, oldParent, true);

                    } else {

                        reorderMovedSection(section, sectionToMove, newParent, oldParent, false);

                    }
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }

    }

    //Move the sectionToMove just behind section and reordersections

    private void reorderMovedSection(Section section, Section sectionToMove, Section newParent, Section oldParent, boolean sameParent) throws Exception {


        WorkspaceImpl workspace = (WorkspaceImpl) getWorkspace();
        SectionsManager sectionManager = UIServices.lookup().getSectionsManager();

        if (sameParent) {
            if (section.getPosition() == sectionToMove.getPosition() - 1)
                return;

        } else {
            sectionToMove.setParent(newParent);

            reorderSections(workspace, oldParent, sectionToMove);

        }


        Section[] sectionList;
        if (newParent == null)
            sectionList = workspace.getAllRootSections();
        else
            sectionList = workspace.getAllChildSections(newParent.getSectionId());


        int count = 0;

        for (int i = 0; i < sectionList.length; i++) {

            if (section.getId().equals(sectionList[i].getId())) {
                section.setPosition(count);
                sectionToMove.setPosition(count + 1);
                count += 2;
                sectionManager.store(section);
                sectionManager.store(sectionToMove);

            } else if (!sectionToMove.getId().equals(sectionList[i].getId())) {
                sectionList[i].setPosition(count);
                count++;
                sectionManager.store(sectionList[i]);

            }
        }


    }

    private void reorderSections(WorkspaceImpl workspace, Section root, Section sectionToMove) throws Exception {

        Section[] sections;
        if (root != null) {
            sections = workspace.getAllChildSections(root.getId());
        } else {
            sections = workspace.getAllRootSections();
        }

        int count = 0;
        for (Section section : sections) {
            if (!section.getSectionId().equals(sectionToMove.getSectionId())) {
                section.setPosition(count);
                count++;
                UIServices.lookup().getSectionsManager().store(section);
            }
        }
    }

    private void changeParent(Section section, Section oldParent, Section newParent) throws Exception {
        log.debug("changeParent(" + section.getId() + ") from " + (oldParent != null ? oldParent.getId() : null) + " to " + (newParent != null ? newParent.getId() : null));

        // Change parent
        section.setParent(newParent);

        // Reorder sections position
        if (oldParent != null) {
            if (newParent != null) {
                if (!oldParent.getId().equals(newParent.getId())) {
                    // Reorder old parent sections
                    section.getWorkspace().reorderSections(oldParent, section.getPosition());

                    // Set new section position
                    if (newParent.isAncestor(oldParent)) {
                        section.setPosition(oldParent.getPosition() + 1);
                        // Reorder new parent sections
                        section.getWorkspace().reorderSections(newParent, oldParent.getPosition());
                    } else {
                        Section[] brotherSections = section.getWorkspace().getAllChildSections(newParent.getId());
                        section.setPosition(brotherSections.length - 1);
                    }
                }
            } else {
                // Reorder old parent sections
                section.getWorkspace().reorderSections(oldParent, section.getPosition());

                // Set new section position
                section.setPosition(oldParent.getPosition() + 1);
                // Reorder new parent sections
                section.getWorkspace().reorderSections(newParent, oldParent.getPosition());
            }
        } else {
            if (newParent != null) {
                // Reorder root sections
                section.getWorkspace().reorderSections(null, 0);

                // Set new section position
                Section[] brotherSections = section.getWorkspace().getAllChildSections(newParent.getId());
                section.setPosition(brotherSections.length - 1);
            }
        }
    }

    protected SectionCopyOption getCopyOptions(CommandRequest request) {
        String sCopyMode = request.getParameter(COPY_MODE);
        int copyMode = 0;
        if (sCopyMode != null)
            copyMode = Integer.parseInt(sCopyMode);
        switch (copyMode) {
            case SectionCopyOption.COPY_ALL:
                return new BasicSectionCopyOption(true);
            case SectionCopyOption.COPY_NONE:
                return new BasicSectionCopyOption(false);
            default:
                SectionCopyOption sco = CopyOption.DEFAULT_SECTION_COPY_OPTION_SAME_WORKSPACE;
                for (Enumeration<String> en = request.getRequestObject().getParameterNames(); en.hasMoreElements();) {
                    String paramName = en.nextElement();
                    if (!paramName.startsWith("duplicatePanelInstance_"))
                        continue;
                    String sDuplicate = request.getParameter(paramName);
                    boolean duplicate = false;
                    if (sDuplicate != null)
                        duplicate = Boolean.parseBoolean(sDuplicate);
                    String panelInstanceId = paramName.substring("duplicatePanelInstance_".length());
                    log.debug("PanelInstance with id=" + panelInstanceId + " will " + (duplicate ? "" : "not ") + "be duplicated.");
                    sco.addPanelInstanceToDuplicate(panelInstanceId, duplicate);
                }
                return sco;
        }
    }


    protected boolean isValidURL(String url) {
        if (url == null || "".equals(url))
            return true;
        String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-.";
        for (int i = 0; i < url.length(); i++)
            if (validChars.indexOf(Character.toUpperCase(url.charAt(i))) == -1)
                return false;
        //Chars are valid

        if (new File(Application.lookup().getBaseAppDirectory() + "/" + url).exists())
            return false;

        //No file or directory exists in root with same name
        try {
            Workspace p = UIServices.lookup().getWorkspacesManager().getWorkspaceByUrl(url);
            if (p == null) return true;//No workspace with same url exists.
            WorkspaceImpl workspace = (WorkspaceImpl) getWorkspace();
            if (workspace.getId().equals(p.getId())) return true;//It is my own workspace
        } catch (Exception e) {
            log.error("Error getting workspace", e);
        }
        return false;
    }
}
