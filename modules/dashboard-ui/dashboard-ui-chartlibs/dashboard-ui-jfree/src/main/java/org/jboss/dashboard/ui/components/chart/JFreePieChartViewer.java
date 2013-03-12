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

import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.displayer.chart.AbstractChartDisplayer;
import org.jboss.dashboard.domain.Interval;
import org.jboss.dashboard.displayer.chart.PieChartDisplayer;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.URLMarkupGenerator;

import de.laures.cewolf.links.PieSectionLinkGenerator;
import de.laures.cewolf.links.LinkGenerator;
import de.laures.cewolf.tooltips.PieToolTipGenerator;
import de.laures.cewolf.tooltips.ToolTipGenerator;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import org.apache.commons.lang.StringUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.text.NumberFormat;

public class JFreePieChartViewer extends JFreeAbstractChartViewer implements PieToolTipGenerator, PieSectionLinkGenerator {

    public JFreePieChartViewer() {
        producerId = "PieChartViewer_DatasetProducer_ID";
    }

    public Object produceDataset(Map params) {
        Locale locale = LocaleManager.currentLocale();
        AbstractChartDisplayer displayer = (AbstractChartDisplayer) getDataDisplayer();
        DataSet xyDataSet = displayer.buildXYDataSet();
        DefaultPieDataset pieds = new DefaultPieDataset();
        for (int i=0; i< xyDataSet.getRowCount(); i++) {
            String xvalue = ((Interval) xyDataSet.getValueAt(i, 0)).getDescription(locale);
            double yvalue = ((Number) xyDataSet.getValueAt(i, 1)).doubleValue();
            pieds.setValue(xvalue,yvalue);
        }
        return pieds;
    }

    // Interface methods.

    public LinkGenerator getLinkGenerator() {
        return this;
    }

    public ToolTipGenerator getToolTipGenerator() {
        return this;
    }

    public String generateToolTip(PieDataset pieDataset, Comparable comparable, int i) {
        Locale locale = LocaleManager.currentLocale();
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        PieChartDisplayer displayer = (PieChartDisplayer) getDataDisplayer();
        String value = numberFormat.format(pieDataset.getValue(i));
        return StringUtils.replace(displayer.getUnit(locale), AbstractChartDisplayer.UNIT_VALUE_TAG, value);
    }

    public String generateLink(Object object, Object object1) {
        URLMarkupGenerator urlMarkupGenerator = UIServices.lookup().getUrlMarkupGenerator();
        int index = ((DefaultPieDataset)object).getIndex((Comparable) object1);
        Map params = new HashMap();
        params.put(JFreeAbstractChartViewer.PARAM_NSERIE, new Integer(index));
        return urlMarkupGenerator.getMarkup(this.getName(), JFreeAbstractChartViewer.PARAM_ACTION,params);
    }
}
