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

import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.commons.message.AbstractMessage;
import org.jboss.dashboard.commons.message.Message;
import org.jboss.dashboard.commons.message.MessageList;
import org.jboss.dashboard.export.ImportManager;
import org.jboss.dashboard.export.ImportResults;
import org.jboss.dashboard.ui.Dashboard;
import org.jboss.dashboard.ui.annotation.panel.PanelScoped;
import org.jboss.dashboard.ui.components.DashboardHandler;
import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.kpi.KPI;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.MessagesComponentHandler;
import org.jboss.dashboard.ui.components.UIBeanHandler;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.responses.SendStreamResponse;
import org.jboss.dashboard.export.ExportManager;
import org.jboss.dashboard.export.ExportOptions;
import org.jboss.dashboard.ui.controller.responses.ShowCurrentScreenResponse;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Section;
import org.jboss.dashboard.workspace.WorkspaceImpl;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * KPI Import/Export handler
 */
@PanelScoped
public class ExportHandler extends UIBeanHandler {

    /**
     * Get the instance for the current session.
     */
    public static ExportHandler lookup() {
        return CDIBeanLocator.getBeanByType(ExportHandler.class);
    }

    public static final String MODE_EXPORT = "export";
    public static final String MODE_IMPORT = "import";
    public static final String PARAM_WORKSPACE_ID = "workspaceId";
    public static final String PARAM_SECTION_ID = "sectionId";

    @Inject
    private transient Logger log;

    @Inject
    private MessagesComponentHandler messagesHandler;

    @Inject @Config("/components/bam/export/show.jsp")
    protected String componentIncludeJSP;

    @Inject @Config("/components/bam/export/kpiImportResult.jsp")
    protected String kpiImportResultJSP;

    @Inject
    protected LocaleManager localeManager;

    protected String selectedWorkspaceId;
    protected Map<String,Set<Long>> selectedSectionIds = new HashMap<String,Set<Long>>();
    protected String mode;

    private String initJSP;

    @PostConstruct
    public void start() throws Exception {
        super.start();
        this.initJSP = getBeanJSP();
    }

    // Accessors

    public String getBeanJSP() {
        return componentIncludeJSP;
    }

    public void setComponentIncludeJSP(String componentIncludeJSP) {
        this.componentIncludeJSP = componentIncludeJSP;
    }

    public String getKpiImportResultJSP() {
        return kpiImportResultJSP;
    }

    public void setKpiImportResultJSP(String kpiImportResultJSP) {
        this.kpiImportResultJSP = kpiImportResultJSP;
    }

    public String getSelectedWorkspaceId() {
        return selectedWorkspaceId;
    }

    public void setSelectedWorkspaceId(String selectedWorkspaceId) {
        this.selectedWorkspaceId = selectedWorkspaceId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
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
        for (Section section : workspace.getSections()) {
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

    public boolean isExportMode() {
        return mode != null && MODE_EXPORT.equalsIgnoreCase(mode);
    }

    public boolean isImportMode() {
        return mode != null && MODE_IMPORT.equalsIgnoreCase(mode);
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
                for (Panel panel : section.getPanels()) {
                    KPI kpi = dash.getKPI(panel);
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
        return new SendStreamResponse(new ByteArrayInputStream(xml.getBytes()), "inline;filename=kpiExport_" + id + ".kpiex");
    }

    public CommandResponse actionImportKPIs(CommandRequest request) {
        messagesHandler.clearAll();
        if (request.getUploadedFilesCount() > 0) {
            File file = (File) request.getFilesByParamName().get("importFile");
            try {

                // Parse the file.
                ImportManager importMgr = DataDisplayerServices.lookup().getImportManager();
                ImportResults importResults = importMgr.parse(new FileInputStream(file));

                // Save the imported results.
                importMgr.update(importResults);

                // Show import messages.
                MessageList messages = importResults.getMessages();
                Locale locale = LocaleManager.currentLocale();
                Iterator it = messages.iterator();
                while (it.hasNext()) {
                    Message message = (Message) it.next();
                    switch (message.getMessageType()) {
                        case Message.ERROR: messagesHandler.addError(message.getMessage(locale)); break;
                        case Message.WARNING: messagesHandler.addWarning(message.getMessage(locale)); break;
                        case Message.INFO: messagesHandler.addMessage(message.getMessage(locale)); break;
                    }
                }
            } catch (Exception e) {
                log.error("Error importing KPIs from file (" + file + ")", e);
                messagesHandler.addError(new ExportHandlerMessage("import.kpis.importAbortedError", new Object[] {}).getMessage(LocaleManager.currentLocale()));
            }
            setComponentIncludeJSP(getKpiImportResultJSP());
        }
        return new ShowCurrentScreenResponse();
    }

    public void actionGoBack(CommandRequest request) {
        if (initJSP != null && !"".equals(initJSP)) setComponentIncludeJSP(initJSP);
    }

    private class ExportHandlerMessage extends AbstractMessage {
        public ExportHandlerMessage(String messageCode, Object[] elements) {
            super(messageCode, elements);
        }
        @Override
        public String getMessage(String messageCode, Locale l) {
            try {
                ResourceBundle i18n = localeManager.getBundle("org.jboss.dashboard.ui.components.export.messages", l);
                return i18n.getString(messageCode);
            } catch (Exception e) {
                return messageCode;
            }
        }
    }
}
