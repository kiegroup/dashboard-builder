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
package org.jboss.dashboard.displayer.chart;

import org.jboss.dashboard.displayer.AbstractDataDisplayerType;
import org.jboss.dashboard.displayer.DataDisplayer;

public abstract class AbstractChartDisplayerType extends AbstractDataDisplayerType {

    public void copyDataDisplayer(DataDisplayer sourceDisplayer, DataDisplayer targetDisplayer) {
        try {
            AbstractChartDisplayer source = (AbstractChartDisplayer) sourceDisplayer;
            AbstractChartDisplayer target = (AbstractChartDisplayer) targetDisplayer;
            target.setDataDisplayerRenderer(source.getDataDisplayerRenderer());
            target.setBackgroundColor(source.getBackgroundColor());
            target.setColor(source.getColor());
            target.setDataProvider(source.getDataProvider());
            target.setDomainConfiguration(source.domainConfig);
            target.setDomainProperty(source.getDomainProperty());
            target.setGraphicAlign(source.getGraphicAlign());
            target.setHeight(source.getHeight());
            target.setLegendAnchor(source.getLegendAnchor());
            target.setRangeConfiguration(source.rangeConfig);
            target.setRangeProperty(source.getRangeProperty());
            target.setRangeScalarFunction(source.getRangeScalarFunction());
            target.setMarginBottom(source.getMarginBottom());
            target.setMarginTop(source.getMarginTop());
            target.setMarginLeft(source.getMarginLeft());
            target.setMarginRight(source.getMarginRight());
            target.setTitle(source.getTitle());
            target.setWidth(source.getWidth());
            target.setAxisInteger(source.isAxisInteger());
            target.setShowLegend(source.isShowLegend());
            target.setShowTitle(source.isShowTitle());
            target.setIntervalsSortCriteria(source.getIntervalsSortCriteria());
            target.setIntervalsSortOrder(source.getIntervalsSortOrder());
        } catch (ClassCastException e) {
            // Ignore non-chart displayers.
        }
    }
}
