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

import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.displayer.DataDisplayer;
import org.jboss.dashboard.displayer.chart.AbstractChartDisplayer;
import org.jboss.dashboard.displayer.exception.DataDisplayerInvalidConfiguration;
import org.jboss.dashboard.kpi.KPI;
import org.jboss.dashboard.kpi.KPIListener;
import org.jboss.dashboard.kpi.KPIListenerAdapter;
import org.jboss.dashboard.kpi.KPIManager;
import org.jboss.dashboard.ui.UIBeanLocator;
import org.jboss.dashboard.ui.annotation.panel.PanelScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Locale;

@PanelScoped
@Named("kpi_viewer")
public class KPIViewer extends UIBeanHandler {

    /** Logger */
    private transient static Logger log = LoggerFactory.getLogger(KPIViewer.class);
    
    /** Get the instance. */
    public static KPIViewer lookup() {
        return CDIBeanLocator.getBeanByType(KPIViewer.class);
    }

    @Inject @Config("/components/bam/kpi_view.jsp")
    protected String viewJSP;

    protected KPI kpi;
    protected DataDisplayerViewer displayerViewer;

    // Is the KPI being displayed in embedded mode or as a part of a general dashboard
    protected boolean embeddedMode = false;

    protected transient KPIListener kpiListener;

    public KPIViewer() {
        kpi = null;
        displayerViewer = null;
        kpiListener = new KPIViewerListener();
        KPIManager kpiManager = DataDisplayerServices.lookup().getKPIManager();
        kpiManager.addKPIListener(kpiListener, KPIManager.EVENT_ALL);
    }

    public DataDisplayerViewer getDisplayerViewer() {
        return displayerViewer;
    }

    public KPI getKpi() {
        return kpi;
    }

    public void setKpi(KPI kpi) {
        this.setKpi(kpi, false);
    }

    public void setKpi(KPI kpi, boolean embeddedMode) {
        this.kpi = kpi;
        this.displayerViewer = null;
        this.embeddedMode=embeddedMode;
        if (kpi != null) {
            DataDisplayer displayer = kpi.getDataDisplayer();
            this.displayerViewer = UIBeanLocator.lookup().getViewer(displayer);
        }
    }

    public boolean isReady() {
        return kpi.getDataProvider() != null && kpi.getDataProvider().isReady() && validate();
    }

    // UIBeanHandler interface

    public String getBeanJSP() {
        return this.viewJSP;
    }

    /**
     * BZ-1100635: Check if data provider definition (all properties) match with the serialized in the current displayer
     */
    protected boolean validate() {
        if (kpi.getDataDisplayer() != null) {
            try {
                kpi.getDataDisplayer().validate(kpi.getDataProvider());
            } catch (DataDisplayerInvalidConfiguration dataDisplayerConfigurationInvalid) {
                Locale currentLocale = LocaleManager.currentLocale();
                log.info("Data provider '" + kpi.getDataProvider().getDescription(currentLocale)+ "' definition has been changed. " +
                        "KPI '" + getKpi().getDescription(currentLocale) + "' must be reconfigured.");

                return false;
            }
        }
        return true;
    }
    
    public void beforeRenderBean() {
        
        // Provider definition could have been modified.
        validate();
        
        // The displayer's title must be the kpi's description.
        // So set it before render the component.
        Locale locale = LocaleManager.currentLocale();
        DataDisplayer kpiDisplayer = kpi.getDataDisplayer();
        if (kpiDisplayer instanceof AbstractChartDisplayer) {
            AbstractChartDisplayer displayer = (AbstractChartDisplayer) kpiDisplayer;
            displayer.setTitle(kpi.getDescription(locale));
        }
    }

    /**
     * Listener that listen for changes made to the KPI instance.
     */
    private class KPIViewerListener extends KPIListenerAdapter {

        public void kpiSaved(KPI other) {
            if (kpi != null && kpi.getCode().equals(other.getCode())) {
                setKpi(other);
            }
        }

        public void kpiDeleted(KPI other) {
            if (kpi != null && kpi.getCode().equals(other.getCode())) {
                setKpi(null);
            }
        }
    }
}
