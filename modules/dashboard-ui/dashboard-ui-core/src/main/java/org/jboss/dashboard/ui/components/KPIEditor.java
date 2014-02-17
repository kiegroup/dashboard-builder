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
package org.jboss.dashboard.ui.components;

import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.ui.annotation.panel.PanelScoped;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.kpi.KPI;
import org.jboss.dashboard.ui.UIBeanLocator;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.displayer.DataDisplayerType;
import org.jboss.dashboard.displayer.DataDisplayer;

import java.util.*;
import javax.inject.Inject;
import javax.inject.Named;

@PanelScoped
@Named("kpi_editor")
public class KPIEditor extends KPIViewer {

    /** Get the instance. */
    public static KPIEditor lookup() {
        return CDIBeanLocator.getBeanByType(KPIEditor.class);
    }

    @Inject @Config("/components/bam/kpi_edit.jsp")
    protected String editJSP;

    protected DataDisplayerEditor displayerEditor;

    // i18n
    public static final String I18N_PREFFIX = "kpiEditorComponent.";

    // Parameters
    public static final String PARAM_KPI_DESCRIPTION = "kpiDescription";

    public KPIEditor() {
        super();
        displayerEditor = null;
    }

    public String getBeanJSP() {
        return editJSP;
    }

    public void setKpi(KPI kpi) {
        super.setKpi(kpi);

        this.displayerEditor = null;
        if (kpi != null) {
            DataDisplayer displayer = kpi.getDataDisplayer();
            this.displayerEditor = UIBeanLocator.lookup().getEditor(displayer);
        }
    }

    public DataDisplayerEditor getDisplayerEditor() {
        return displayerEditor;
    }

    public CommandResponse actionSubmit(CommandRequest request) throws Exception {
        String changeDisplayer = request.getRequestObject().getParameter("changeDisplayer");
        if ("true".equals(changeDisplayer)) return actionChangeDisplayer(request);

        // Change the data provider if necessary.
        Locale locale = LocaleManager.currentLocale();
        DataDisplayer kpiDisplayer = kpi.getDataDisplayer();
        String selProviderCode = request.getRequestObject().getParameter("providerSelected");
        if (selProviderCode != null && !kpi.getDataProvider().getCode().equals(selProviderCode)) {
            DataProvider selProvider = DataDisplayerServices.lookup().getDataProviderManager().getDataProviderByCode(selProviderCode);
            kpi.setDataProvider(selProvider);
            kpiDisplayer.setDataProvider(selProvider);
            setKpi(kpi);
        }

        // Update the configuration.
        displayerEditor.actionSubmit(request);

        // Update the kpi description.
        String kpiDescr = request.getRequestObject().getParameter(PARAM_KPI_DESCRIPTION);
        if (!StringUtils.isBlank(kpiDescr) && !kpiDescr.contains("<script>")) {
            kpi.setDescription(kpiDescr, locale);
        }

        // Check for change of locale and update LocaleManager if necessary.
        localeChanged(request);
        return null;
    }

    public CommandResponse actionChangeDisplayer(CommandRequest request) throws Exception {
        String uid = request.getRequestObject().getParameter("uid");
        DataDisplayerType selectedType = DataDisplayerServices.lookup().getDataDisplayerManager().getDisplayerTypeByUid(uid);
        if (selectedType == null) return null;

        DataDisplayer kpiDisplayer = kpi.getDataDisplayer();
        DataDisplayerType kpiDisplayerType = kpiDisplayer.getDataDisplayerType();

        // If the displayer associated to the kpi is not the new displayer, change it.
        if (!kpiDisplayerType.getUid().equals(selectedType.getUid())) {
            DataDisplayer newDisplayer = selectedType.createDataDisplayer();
            newDisplayer.setDefaultSettings();
            newDisplayer.copyFrom(kpiDisplayer);
            kpi.setDataDisplayer(newDisplayer);
            setKpi(kpi);
            return null;
        }
        return null;
    }

    /**
     * Check to see if the locale has been changed in the "localeLang" parameter.
     * If so change the SessionManager CurrentLocale and return true.
     * Else return false.
     * @param request
     * @return true if the language has changed.
     */
    protected boolean localeChanged(CommandRequest request) {
        String localeLangParm = request.getRequestObject().getParameter("localeLang");
        LocaleManager lManager = LocaleManager.lookup();
        if (localeLangParm != null && !localeLangParm.equals("") && !localeLangParm.equals(lManager.getCurrentLang())) {
            lManager.setCurrentLang(localeLangParm);
            return true;
        }
        return false;
    }
}
