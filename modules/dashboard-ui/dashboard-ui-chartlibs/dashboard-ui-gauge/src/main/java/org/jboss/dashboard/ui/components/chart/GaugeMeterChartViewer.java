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
package org.jboss.dashboard.ui.components.chart;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.ui.Dashboard;
import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.displayer.chart.AbstractChartDisplayer;
import org.jboss.dashboard.domain.Interval;
import org.jboss.dashboard.ui.annotation.panel.PanelScoped;
import org.jboss.dashboard.ui.components.DataDisplayerViewer;
import org.jboss.dashboard.ui.components.DashboardHandler;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.commons.filter.FilterByCriteria;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.responses.ShowCurrentScreenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PanelScoped
@Named("gauge_meterchart_viewer")
public class GaugeMeterChartViewer extends DataDisplayerViewer {

    public static final String PARAM_ACTION = "applyLink";
    public static final String PARAM_NSERIE = "serie";

    @Inject @Config("/components/bam/displayer/chart/gauge_meterchart_viewer.jsp")
    protected String beanJSP;

    public String getBeanJSP() {
        return beanJSP;
    }

    public CommandResponse actionApplyLink(CommandRequest request) throws Exception {
        AbstractChartDisplayer abstractChartDisplayer = (AbstractChartDisplayer) getDataDisplayer();
        DataProperty property = abstractChartDisplayer.getDomainProperty();
        Integer series = Integer.decode(request.getRequestObject().getParameter(PARAM_NSERIE));
        DataSet dataSet = abstractChartDisplayer.buildXYDataSet();
        Interval interval = (Interval) dataSet.getValueAt(series, 0);
        Dashboard dashboard = DashboardHandler.lookup().getCurrentDashboard();
        if (dashboard.filter(property.getPropertyId(), interval, FilterByCriteria.ALLOW_ANY)) {
            return new ShowCurrentScreenResponse();
        }
        return null;
    }
}
