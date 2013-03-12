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
package org.jboss.dashboard.ui.components.export;

import org.jboss.dashboard.ui.Dashboard;
import org.jboss.dashboard.ui.components.DashboardHandler;
import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.kpi.KPI;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.UIComponentHandlerFactoryElement;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.responses.SendStreamResponse;
import org.jboss.dashboard.export.ExportManager;
import org.jboss.dashboard.export.ExportOptions;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.workspace.WorkspaceImpl;

import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * The export handler.
 */
public class ExportHandler extends UIComponentHandlerFactoryElement {

    /**
     * Get the instance for the current session.
     */
    public static ExportHandler lookup() {
        return (ExportHandler) Factory.lookup("org.jboss.dashboard.ui.components.ExportHandler");
    }

    public static final String PARAM_WORKSPACE_ID = "workspaceId";
    public static final String PARAM_SECTION_ID = "sectionId";

    protected String componentIncludeJSP;
    protected String selectedWorkspaceId;
    protected Map<String,Set<Long>> selectedSectionIds = new HashMap<String,Set<Long>>();

    // Accessors

    public String getComponentIncludeJSP() {
        return componentIncludeJSP;
    }

    public void setComponentIncludeJSP(String componentIncludeJSP) {
        this.componentIncludeJSP = componentIncludeJSP;
    }

    public String getSelectedWorkspaceId() {
        return selectedWorkspaceId;
    }

    public void setSelectedWorkspaceId(String selectedWorkspaceId) {
        this.selectedWorkspaceId = selectedWorkspaceId;
    }

    public WorkspaceImpl getSelectedWorkspace() throws Exception {
        if (selectedWorkspaceId == null) return null;
        return (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(selectedWorkspaceId);
    }

    public List<WorkspaceImpl> getWorkspacesWithKPIs() throws Exception {
        List<WorkspaceImpl> results = new ArrayList<WorkspaceImpl>();
        WorkspaceImpl[] workspaces = UIServices.lookup().getWorkspacesManager().getWorkspaces();
        for (int i = 0; i < workspaces.length; i++) {
            WorkspaceImpl workspace = workspaces[i];
            List<Section> dashs = getSectionsWithKPIs(workspace);
            if (!dashs.isEmpty()) results.add(workspace);
        }
        return results;
    }

    public List<Section> getSectionsWithKPIs(WorkspaceImpl workspace) throws Exception {
        DashboardHandler dashboardHandler = DashboardHandler.lookup();
        List<Section> results = new ArrayList<Section>();
        Iterator sectionIt = workspace.getSections().iterator();
        while (sectionIt.hasNext()) {
            Section section = (Section) sectionIt.next();
            if (dashboardHandler.containsKPIs(section)) {
                results.add(section);
            }
        }
        return results;
    }

    public boolean isSectionSelected(Long sectionId) {
        for (Set<Long> sectionIds : selectedSectionIds.values()) {
            if (sectionIds.contains(sectionId)) return true;
        }
        return false;
    }

    public Set<Section> getSelectedSections(WorkspaceImpl workspace) throws Exception {
        Set<Long> sectionIds = selectedSectionIds.get(workspace.getId());
        Set<Section> results = new HashSet<Section>();
        for (Long sectionId : sectionIds) {
            results.add(workspace.getSection(sectionId));
        }
        return results;
    }

    public List<KPI> getSelectedKPIs() throws Exception {
        List<KPI> results = new ArrayList<KPI>();
        DashboardHandler dashboardHandler = DashboardHandler.lookup();
        for (String workspaceId : selectedSectionIds.keySet()) {
            WorkspaceImpl workspace = (WorkspaceImpl) UIServices.lookup().getWorkspacesManager().getWorkspace(workspaceId);
            Set<Section> sections = getSelectedSections(workspace);
            for (Section section : sections) {
                Dashboard dash = dashboardHandler.getDashboard(section);
                Iterator it = section.getPanels().iterator();
                while (it.hasNext()) {
                    KPI kpi = dash.getKPI((Panel) it.next());
                    if (kpi != null && !results.contains(kpi)) results.add(kpi);
                }
            }
        }
        final Locale l = LocaleManager.currentLocale();
        Collections.sort(results, new Comparator() {
            public int compare(Object o1, Object o2) {
                KPI s1 = (KPI) o1;
                KPI s2 = (KPI) o2;
                return s1.getDescription(l).compareTo(s2.getDescription(l));
            }
        });
        return results;
    }

    public List<KPI> getSelectedKPIs(DataProvider dataProvider) throws Exception {
        List<KPI> results = getSelectedKPIs();
        Iterator<KPI> it = results.iterator();
        while (it.hasNext()) {
            KPI kpi = it.next();
            if (kpi != null && !kpi.getDataProvider().equals(dataProvider)) {
                it.remove();
            }
        }
        return results;
    }

    public List<DataProvider> getSelectedDataProviders() throws Exception {
        List<DataProvider> results = new ArrayList<DataProvider>();
        for (KPI kpi: getSelectedKPIs()) {
            DataProvider dp = kpi.getDataProvider();
            if (!results.contains(dp)) results.add(dp);
        }
        final Locale l = LocaleManager.currentLocale();
        Collections.sort(results, new Comparator() {
            public int compare(Object o1, Object o2) {
                DataProvider s1 = (DataProvider) o1;
                DataProvider s2 = (DataProvider) o2;
                return s1.getDescription(l).compareTo(s2.getDescription(l));
            }
        });
        return results;
    }

    // Actions

    public void actionSelectWorkspace(CommandRequest request) throws Exception {
        String newWorkspaceId = request.getRequestObject().getParameter(PARAM_WORKSPACE_ID);
        if (newWorkspaceId.equals(selectedWorkspaceId)) selectedWorkspaceId = null;
        else selectedWorkspaceId = newWorkspaceId;
    }

    public void actionCheckSection(CommandRequest request) throws Exception {
        Long sectionId = Long.valueOf(request.getRequestObject().getParameter(PARAM_SECTION_ID));
        if (isSectionSelected(sectionId)) {
            for (Set<Long> sectionIds : selectedSectionIds.values()) {
                if (sectionIds.contains(sectionId)) {
                    sectionIds.remove(sectionId);
                }
            }
        } else if (selectedWorkspaceId != null) {
            Set<Long> sectionIds = selectedSectionIds.get(selectedWorkspaceId);
            if (sectionIds == null) selectedSectionIds.put(selectedWorkspaceId, sectionIds = new HashSet<Long>());
            sectionIds.add(Long.valueOf(sectionId));
        }
    }

    public void actionSelectAllSections(CommandRequest request) throws Exception {
        WorkspaceImpl workspace = getSelectedWorkspace();
        if (workspace != null) {
            List<Section> dashs = getSectionsWithKPIs(workspace);
            Set<Long> sectionIds = new HashSet<Long>();
            selectedSectionIds.put(selectedWorkspaceId, sectionIds);
            for (Section dash : dashs) {
                sectionIds.add(dash.getId());
            }
        }
    }

    public void actionUnselectAllSections(CommandRequest request) throws Exception {
        if (selectedWorkspaceId != null) {
            selectedSectionIds.remove(selectedWorkspaceId);
        }
    }

    public void actionClearSelectedKPIs(CommandRequest request) throws Exception {
        selectedSectionIds.clear();
    }

    public CommandResponse actionExportSelectedKPIs(CommandRequest request) throws Exception {
        // Export in XML format all the KPIs and data providers in the system.
        ExportManager expMgr = DataDisplayerServices.lookup().getExportManager();
        ExportOptions options = expMgr.createExportOptions();
        options.setIgnoreKPIs(false);
        options.setIgnoreDataProviders(false);
        options.setKPIs(getSelectedKPIs());
        options.setDataProviders(getSelectedDataProviders());
        String xml = expMgr.format(options);

        // Send XML bytes as a stream response.
        int id = xml.hashCode();
        if (id < 0) id = id*-1;
        return new SendStreamResponse(new ByteArrayInputStream(xml.getBytes()), "inline;filename=kpiExport_" + id + ".xml");
    }
}
