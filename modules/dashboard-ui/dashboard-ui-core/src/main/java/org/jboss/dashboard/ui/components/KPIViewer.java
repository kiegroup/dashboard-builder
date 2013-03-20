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

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.displayer.chart.AbstractChartDisplayer;
import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.kpi.KPI;
import org.jboss.dashboard.ui.UIBeanLocator;
import org.jboss.dashboard.displayer.DataDisplayer;

import java.util.Locale;

public class KPIViewer extends UIComponentHandlerFactoryElement {

    /** Get the instance. */
    public static KPIViewer lookup() {
        return (KPIViewer) Factory.lookup("org.jboss.dashboard.ui.components.KPIViewer");
    }

    protected KPI kpi;
    protected DataDisplayerViewer displayerViewer;
    protected String componentIncludeJSP;

    public KPIViewer() {
        kpi = null;
        displayerViewer = null;
    }

    public DataDisplayerViewer getDisplayerViewer() {
        return displayerViewer;
    }

    public KPI getKpi() {
        return kpi;
    }

    public void setKpi(KPI kpi) {
        this.kpi = kpi;
        this.displayerViewer = null;
        if (kpi != null) {
            DataDisplayer displayer = kpi.getDataDisplayer();
            this.displayerViewer = UIBeanLocator.lookup().getViewer(displayer);
        }
    }

    public boolean isReady() {
        return kpi.getDataProvider() != null && kpi.getDataProvider().isReady();
    }

    // UIComponentHandlerFactoryElement interface

    public String getComponentIncludeJSP() {
        return this.componentIncludeJSP;
    }

    public void setComponentIncludeJSP(String componentIncludeJSP) {
        this.componentIncludeJSP = componentIncludeJSP;
    }

    public void beforeRenderComponent() {
        // The displayer's title must be the kpi's description.
        // So set it before render the component.
        Locale locale = LocaleManager.currentLocale();
        DataDisplayer kpiDisplayer = kpi.getDataDisplayer();
        if (kpiDisplayer instanceof AbstractChartDisplayer) {
            AbstractChartDisplayer displayer = (AbstractChartDisplayer) kpiDisplayer;
            displayer.setTitle(kpi.getDescription(locale));
        }
    }
}
