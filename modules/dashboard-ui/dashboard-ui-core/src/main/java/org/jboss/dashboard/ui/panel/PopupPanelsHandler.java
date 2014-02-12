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
package org.jboss.dashboard.ui.panel;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.ModalDialogComponent;
import org.jboss.dashboard.ui.components.PanelComponent;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.slf4j.Logger;

import java.util.*;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

@SessionScoped
public class PopupPanelsHandler extends PanelComponent {

    @Inject
    private transient Logger log;

    @Inject
    private NavigationManager navigationManager;

    @Inject /** The locale manager. */
    protected LocaleManager localeManager;

    @Inject @Config("/panels/panelsPopUp/panels.jsp")
    protected String componentIncludeJSP;

    @Inject @Config("500")
    protected int width;

    @Inject @Config("300")
    protected int height;

    public static String GROUPID = "groupId";
    public static String PANEL_INSTANCE_ID = "panelInstanceId";
    public static String PANEL_SUBCATEGORY_ID = "subcategoryId";
    public static String PANEL_INSTANCE_PAGE_ID = "page";

    private String showedGroupId;
    private String showedPanelInstanceId;
    private String showedPanelInstancePage;
    private String showedPanelSubgroupId;

    public String getShowedPanelInstanceId() {
        return showedPanelInstanceId;
    }

    public void setShowedPanelInstanceId(String showedPanelInstanceId) {
        this.showedPanelInstanceId = showedPanelInstanceId;
    }

    public String getShowedGroupId() {
        return showedGroupId;
    }

    public void setShowedGroupId(String showedGroupId) {
        this.showedGroupId = showedGroupId;
    }

    public String getShowedPanelInstancePage() {
        return showedPanelInstancePage;
    }

    public void setShowedPanelInstancePage(String showedPanelInstancePage) {
        this.showedPanelInstancePage = showedPanelInstancePage;
    }

    public String getShowedPanelSubgroupId() {
        return showedPanelSubgroupId;
    }

    public void setShowedPanelSubgroupId(String showedPanelSubgroupId) {
        this.showedPanelSubgroupId = showedPanelSubgroupId;
    }

    public void actionGetPanelsPopupPage(CommandRequest request) {
        ModalDialogComponent modalDialog = getModalDialogComponent();

        if (modalDialog.isShowing()) {
            modalDialog.hide();
            return;
        }

        ResourceBundle i18n = localeManager.getBundle("org.jboss.dashboard.ui.panel.messages", LocaleManager.currentLocale());
        modalDialog.setTitle(i18n.getString("ui.panels.popup.title"));
        modalDialog.setCurrentComponent(this);
        modalDialog.setModal(false);
        modalDialog.setDraggable(true);
        modalDialog.setCloseListener(new Runnable() {
            public void run() {
                PopupPanelsHandler.this.reset();
            }
        });
        modalDialog.show();
    }

    public void actionRedrawPopup(CommandRequest request) {
        String grId = request.getRequestObject().getParameter(PopupPanelsHandler.GROUPID);
        String instanceId = request.getRequestObject().getParameter(PopupPanelsHandler.PANEL_INSTANCE_ID);
        String subgrup = request.getRequestObject().getParameter(PopupPanelsHandler.PANEL_SUBCATEGORY_ID);


        if (grId.equals(getShowedGroupId()) && instanceId == null) {
            if (subgrup == null) {
                setShowedGroupId(null); //si se pide la segunda vez se cierra
            } else {
                setShowedGroupId(grId);//el caso que se despliega un grupo de paneles
            }
        } else {
            setShowedGroupId(grId);
        }
        setShowedPanelInstanceId(instanceId);
        setShowedPanelInstancePage(request.getRequestObject().getParameter(PopupPanelsHandler.PANEL_INSTANCE_PAGE_ID));
        setShowedPanelSubgroupId(subgrup);
    }

    public Map prepareGroupsMap() {
        String showedGroupId = getShowedGroupId();

        WorkspaceImpl workspace = (WorkspaceImpl) getNavigationManager().getCurrentWorkspace();
        Map panelStatistics = calculatePanelsStatistics(workspace);
        String[] groupList = UIServices.lookup().getPanelsProvidersManager().enumerateProvidersGroups(workspace);

        if (groupList == null || groupList.length == 0) return null;

        Set instances = workspace.getPanelInstancesSet();
        Map groups = new HashMap();
        String groupId;
        for (int i = 0; i < groupList.length; i++) {
            groupId = groupList[i];
            Map group = (HashMap) groups.get(groupId);
            if (group == null) {
                group = new HashMap();
                groups.put(groupId, group);
            }
            if (groupId.equals(showedGroupId)) {
                PanelProvider[] providers = UIServices.lookup().getPanelsProvidersManager().getProvidersInGroup(groupId, workspace);
                for (int j = 0; j < providers.length; j++) {
                    Map provider = (HashMap) group.get(providers[j]);
                    if (provider == null) {
                        provider = new HashMap();
                        group.put(providers[j], provider);
                    }
                    for (Iterator iterator = instances.iterator(); iterator.hasNext(); ) {
                        PanelInstance instance = (PanelInstance) iterator.next();
                        if (instance.getProviderName().equals(providers[j].getId())) {
                            String instanceGroupName = instance.getParameterValue(PanelInstance.PARAMETER_GROUP, LocaleManager.lookup().getDefaultLang());
                            instanceGroupName = instanceGroupName == null ? "" : instanceGroupName.trim();
                            Map instanceGroup = (Map) provider.get(instanceGroupName);
                            if (instanceGroup == null) {
                                instanceGroup = new HashMap();
                                provider.put(instanceGroupName, instanceGroup);
                            }
                            instanceGroup.put(instance.getInstanceId(), instance);
                        }
                    }
                }
            }
        }

        return groups;
    }


    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    public String getBeanJSP() {
        return componentIncludeJSP;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public ModalDialogComponent getModalDialogComponent() {
        return CDIBeanLocator.getBeanByType(ModalDialogComponent.class);
    }

    public void reset() {
        setShowedGroupId(null);
        setShowedPanelInstanceId(null);
        setShowedPanelInstancePage(null);
    }


     protected Map calculatePanelsStatistics(WorkspaceImpl workspace) {
        Set sections = workspace.getSections();
        HashMap result = new HashMap();
        for (Iterator iterator = sections.iterator(); iterator.hasNext();) {
            Section section = (Section) iterator.next();
            Set panels = section.getPanels();
            for (Iterator iterator1 = panels.iterator(); iterator1.hasNext();) {
                Panel panel = (Panel) iterator1.next();
                Long instanceId = panel.getInstanceId();
                Integer instanceCount = (Integer) result.get(instanceId);
                if (instanceCount == null) {
                    result.put(instanceId, new Integer(1));
                } else {
                    result.put(instanceId, new Integer(1 + instanceCount.intValue()));
                }
            }
        }
        return result;
    }
}
