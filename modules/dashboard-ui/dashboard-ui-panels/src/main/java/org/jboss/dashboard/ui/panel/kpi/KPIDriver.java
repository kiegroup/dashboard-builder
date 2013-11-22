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
package org.jboss.dashboard.ui.panel.kpi;

import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.displayer.DataDisplayerType;
import org.jboss.dashboard.displayer.chart.BarChartDisplayerType;
import org.jboss.dashboard.kpi.KPIManager;
import org.jboss.dashboard.ui.Dashboard;
import org.jboss.dashboard.ui.UIBeanLocator;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.DashboardHandler;
import org.jboss.dashboard.ui.components.KPIViewer;
import org.jboss.dashboard.ui.panel.DashboardDriver;
import org.jboss.dashboard.ui.panel.PanelDriver;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.ui.panel.parameters.ComboListParameter;
import org.jboss.dashboard.kpi.KPI;
import org.jboss.dashboard.ui.components.KPIEditor;
import org.jboss.dashboard.displayer.chart.BarChartDisplayer;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.PanelSession;
import org.jboss.dashboard.workspace.PanelsManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

public class KPIDriver extends PanelDriver implements DashboardDriver {

    /** Logger */
    protected static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(KPIDriver.class);

    // i18n
    public static final String I18N_PREFFIX = "kpiDriver.";

    // Panel JSPs
    public static final String PAGE_SHOW = "show";
    public static final String PAGE_EDIT = "edit";
    public static final String PAGE_PROVIDER_SELECTION = "provider_selection";

    /** The locale manager. */
    protected LocaleManager localeManager;

    public KPIDriver() {
        localeManager = LocaleManager.lookup();
    }

    // Panel accessors

    /**
     * Get the panel KPI from the current dashboard. The KPI is loaded if necessary.
     */
    public KPI getKPI(Panel panel) throws Exception {
        return DashboardHandler.lookup().getCurrentDashboard().getKPI(panel);
    }

    // PanelDriver interface

    public int getEditWidth(Panel panel, CommandRequest request) {
        return 1000;
    }

    public int getEditHeight(Panel panel, CommandRequest request) {
        return 600;
    }

    public void init(PanelProvider provider) throws Exception {
        super.init(provider);
        addParameter(new ComboListParameter(provider, Dashboard.KPI_CODE, false, new KPIDataSupplier(), false));
    }

    protected void beforeRenderPanel(Panel panel, HttpServletRequest req, HttpServletResponse res) {
        super.beforeRenderPanel(panel, req, res);

        // Ensure the KPI editor/viewer component is initialized (at session level).
        KPI kpi = null;
        try {
            kpi = getKPI(panel);
        } catch (Exception e) {
            log.error("Error: ",e);
        }
        PanelSession panelSession = getPanelSession(panel);
        if (kpi == null) panelSession.setCurrentPageId(PAGE_PROVIDER_SELECTION);
        else panelSession.setCurrentPageId(PAGE_SHOW);

        // Ensure that the UI editor & viewer see the KPI
        passKPItoUI(kpi);
    }

    protected void passKPItoUI(KPI kpi) {
        KPIEditor kpiEditor = UIBeanLocator.lookup().getEditor();
        KPIViewer kpiViewer = UIBeanLocator.lookup().getViewer();
        if (kpiEditor.getKpi() == null) kpiEditor.setKpi(kpi);
        if (kpiViewer.getKpi() == null) kpiViewer.setKpi(kpi);
    }


    protected void beforePanelInstanceRemove(PanelInstance instance) throws Exception {
        // Delete from persistence the KPI attached to the panel.
        KPI kpi = Dashboard.getKPI(instance);

        // Only delete not null KPIs based on deleteable providers.
        if (kpi != null && kpi.getDataProvider().isCanDelete()) {

            // Only delete the KPI if not referred by other panels.
            PanelsManager panelsManager = UIServices.lookup().getPanelsManager();
            Set<PanelInstance> panels = panelsManager.getPanelsByParameter(Dashboard.KPI_CODE, kpi.getCode());
            if (panels.size() == 1) {
                kpi.delete();
            }
        }
    }

    public boolean supportsHelpMode(Panel panel) {
        return true;
    }

    public boolean supportsEditMode(Panel panel) {
        // Until a KPI is created do not enable the edit mode.
        try {
            return getKPI(panel) != null;
        } catch (Exception e) {
            log.error("Error: ",e);
        }
        return false;
    }

    public void replicateData(PanelInstance src, PanelInstance dest) throws Exception {
        KPI kpiSrc = Dashboard.getKPI(src);
        if (kpiSrc != null) {
            // Clone the original KPI.
            KPI kpiDest = DataDisplayerServices.lookup().getKPIManager().createKPI();
            kpiDest.setDataProvider(kpiSrc.getDataProvider());
            kpiDest.setDataDisplayer(kpiSrc.getDataDisplayer());
            kpiDest.setDescriptionI18nMap(kpiSrc.getDescriptionI18nMap());
            kpiDest.save();

            // Link the destination panel instance with the newly created KPI.
            dest.setParameterValue(Dashboard.KPI_CODE, kpiDest.getCode());
        }
    }

    // Panel actions

    /**
     * Create a default KPI.
     */
    public CommandResponse actionCreateKPI(final Panel panel, final CommandRequest request) throws Exception {
        // By now, make the panel work with a hard-coded KPI.
        KPI kpi = DataDisplayerServices.lookup().getKPIManager().createKPI();

        // Set the KPI's data provider
        String initialProvider = request.getRequestObject().getParameter("initialProvider");
        DataProvider provider = DataDisplayerServices.lookup().getDataProviderManager().getDataProviderByCode(initialProvider);
        kpi.setDataProvider(provider);

        // Set the KPI's data displayer
        DataDisplayerType displayerType = DataDisplayerServices.lookup().getDataDisplayerManager().getDisplayerTypeByUid(BarChartDisplayerType.UID);
        BarChartDisplayer displayer = (BarChartDisplayer) displayerType.createDataDisplayer();
        displayer.setDefaultSettings();
        displayer.setTitle(kpi.getDescription(LocaleManager.currentLocale()));
        displayer.setDataProvider(provider);
        kpi.setDataDisplayer(displayer);
        kpi.setCode(null);
        kpi.save(); // The KPI's id and code are auto-generated here.

        // Set a default description
        Locale[] locales = LocaleManager.lookup().getPlatformAvailableLocales();
        for (int i=0; i<locales.length; i++) {
            Locale locale = locales[i];
            ResourceBundle i18n = localeManager.getBundle("org.jboss.dashboard.ui.panel.kpi.messages", locale);
            kpi.setDescription(i18n.getString("kpiDriver.newKpi"), locale);
        }

        // Save the relationship between the panel and the KPI.
        panel.getInstance().setParameterValue(Dashboard.KPI_CODE, kpi.getCode());

        // Go to edit mode.
        passKPItoUI(kpi);
        return panelActionEditMode(panel, request);
    }

    /**
     * Save changes on the KPI being edited.
     */
    public void actionSubmit(Panel panel, CommandRequest request) throws Exception {
        KPIEditor kpiEditor = UIBeanLocator.lookup().getEditor();
        kpiEditor.actionSubmit(request);

        // Make the panel instance's description match the KPI description.
        String lang = LocaleManager.currentLang();
        String kpiDescr = request.getRequestObject().getParameter(KPIEditor.PARAM_KPI_DESCRIPTION);
        if (!StringUtils.isBlank(kpiDescr)) panel.getInstance().setTitle(kpiDescr, lang);

        // Make changes persistent.
        KPI kpi = kpiEditor.getKpi();
        kpi.save();

        // Ensure that the UI editor & viewer see the KPI changes
        passKPItoUI(kpi);
    }

    // DashboardDriver interface

    public Set<DataProvider> getDataProvidersUsed(Panel panel) throws Exception {
        Set<DataProvider> results = new HashSet<DataProvider>();
        KPI kpi = getKPI(panel);
        results.add(kpi.getDataProvider());
        return results;
    }
}
