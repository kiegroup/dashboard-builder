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
package org.jboss.dashboard.ui.panel.dashboard;

import org.jboss.dashboard.ui.Dashboard;
import org.jboss.dashboard.ui.DashboardFilter;
import org.jboss.dashboard.ui.components.DashboardFilterProperty;
import org.jboss.dashboard.ui.components.DashboardHandler;
import org.jboss.dashboard.ui.controller.responses.ShowPanelPage;
import org.jboss.dashboard.ui.panel.DashboardDriver;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.ui.panel.PanelDriver;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.ui.panel.parameters.StringParameter;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.components.DashboardFilterHandler;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.PanelInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class DashboardFilterDriver extends PanelDriver implements DashboardDriver {

    /** The logger */
    protected static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DashboardFilterDriver.class.getName());

    public DashboardFilterHandler getDashboardFilterHandler(Panel panel) {
        String code = panel.getParameterValue(DashboardFilter.FILTER_HANDLER_CODE);
        return DashboardFilterHandler.lookup(code);
    }
    
    public void init(PanelProvider provider) throws Exception {
        super.init(provider);
        addParameter(new StringParameter(provider, DashboardFilter.FILTER_HANDLER_CODE, false, false));
    }

    public boolean supportsEditMode(Panel panel) {
        return true;
    }

    protected void beforeRenderPanel(Panel panel, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        super.beforeRenderPanel(panel, httpServletRequest, httpServletResponse);

        initDashboardFilterHandler(panel);
    }

    protected void initDashboardFilterHandler(Panel panel) {
        DashboardFilterHandler handler = getDashboardFilterHandler(panel);

        // Check if same panel is in different sections.
        if (checkPanelDuplicated(panel)) handler.setPanelDuplicated(true);
        else handler.setPanelDuplicated(false);

        // Check if component have not been deserialized yet. If so then load its persistent status.
        if (handler.getSerializedProperties() == null) {
            try {
                boolean  needsToReserialize = handler.deserializeComponentData((String) panel.getContentData());
                if (needsToReserialize) saveDashboardFilterHandler(panel);
            } catch (Exception e) {
                log.error("Error deserializing visible properties for dashboard filter.", e);
            }
        }
    }

    protected boolean checkPanelDuplicated(Panel panel) {
        PanelInstance instance = panel.getInstance();
        Set sections = new HashSet();
        Panel[] panels = instance.getAllPanels();
        for (int i = 0; i < panels.length; i++) {
            Panel panel1 = panels[i];
            sections.add(panel1.getSection());
        }
        return sections.size() > 1;
    }

    public void saveDashboardFilterHandler(Panel panel) throws Exception {
        DashboardFilterHandler handler = getDashboardFilterHandler(panel);
        handler.serializeComponentData();
        String serializedStr = handler.getSerializedProperties();
        panel.setContentData(serializedStr);
    }

    @Override
    public void activateEditMode(Panel panel, CommandRequest request) throws Exception {
        super.activateEditMode(panel, request);

        DashboardFilterHandler handler = getDashboardFilterHandler(panel);
        handler.enableEditMode();
    }

    @Override
    public void activateNormalMode(Panel panel, CommandRequest commandRequest) throws Exception {
        super.activateNormalMode(panel, commandRequest);

        DashboardFilterHandler handler = getDashboardFilterHandler(panel);
        handler.enableShowMode();
    }

    public CommandResponse actionStore(Panel panel, CommandRequest request) throws Exception {
        saveDashboardFilterHandler(panel);
        return new ShowPanelPage();
    }

    // DashboardDriver interface

    public Set<DataProvider> getDataProvidersUsed(Panel panel) throws Exception {
        Set<DataProvider> results = new HashSet<DataProvider>();
        Dashboard dashboard = DashboardHandler.lookup().getCurrentDashboard();
        DashboardFilterHandler handler = dashboard.getDashboardFilter().getHandler(panel);
        if (handler == null) return results; // It happens on drill down.

        for (DashboardFilterProperty filterProperty : handler.getVisibleProperties()) {
            DataProvider dataProvider = filterProperty.getDataProperty().getDataSet().getDataProvider();
            results.add(dataProvider);
        }
        return results;
    }
}
