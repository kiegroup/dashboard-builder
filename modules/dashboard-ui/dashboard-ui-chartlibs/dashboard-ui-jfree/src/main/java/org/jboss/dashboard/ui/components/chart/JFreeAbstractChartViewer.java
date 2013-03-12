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

import java.util.Map;
import java.util.Date;

import org.jboss.dashboard.ui.Dashboard;
import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.displayer.chart.AbstractChartDisplayer;
import org.jboss.dashboard.domain.Interval;
import org.jboss.dashboard.ui.components.DataDisplayerViewer;
import org.jboss.dashboard.ui.components.DashboardHandler;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.commons.filter.FilterByCriteria;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.responses.ShowCurrentScreenResponse;
import de.laures.cewolf.DatasetProducer;
import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.links.LinkGenerator;
import de.laures.cewolf.tooltips.ToolTipGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for JFree chart viewers.
 */
public abstract class JFreeAbstractChartViewer extends DataDisplayerViewer implements DatasetProducer {

    public static final String PARAM_NSERIE = "serie";
    public static final String PARAM_ACTION = "applyLink";

    private static transient Log log = LogFactory.getLog(JFreeAbstractChartViewer.class.getName());

    /**
     * Unique ID for the DatasetProducer
     */
    protected String producerId;

    public String getProducerId() {
        return producerId;
    }

    // DatasetProducer interface

    public Object produceDataset(Map params) throws DatasetProduceException {
        return null;
    }

    public abstract LinkGenerator getLinkGenerator();

    public abstract ToolTipGenerator getToolTipGenerator();

    /**
     * This producer's data is invalidated after 5 seconds. By this method the
     * producer can influence Cewolf's caching behaviour the way it wants to.
     */
    public boolean hasExpired(Map map, Date date) {
        //return (System.currentTimeMillis() - date.getTime()) > 5000;
        return false;
    }

    // UI actions

    public CommandResponse actionApplyLink(CommandRequest request) {
        try {
            AbstractChartDisplayer abstractChartDisplayer = (AbstractChartDisplayer) getDataDisplayer();
            DataProperty property = abstractChartDisplayer.getDomainProperty();
            Integer series = Integer.decode(request.getRequestObject().getParameter(PARAM_NSERIE));
            DataSet dataSet = abstractChartDisplayer.buildXYDataSet();
            Interval interval = (Interval) dataSet.getValueAt(series, 0);
            Dashboard dashboard = DashboardHandler.lookup().getCurrentDashboard();
            if (dashboard.filter(property.getPropertyId(), interval, FilterByCriteria.ALLOW_ANY)) {
                return new ShowCurrentScreenResponse();
            }
        } catch (Exception e) {
            log.error("Cannot apply filter.",e);
        }
        return null;
    }
}

