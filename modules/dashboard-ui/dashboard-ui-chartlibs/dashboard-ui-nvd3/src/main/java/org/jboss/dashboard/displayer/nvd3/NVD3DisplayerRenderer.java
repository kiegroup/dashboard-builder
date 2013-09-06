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
package org.jboss.dashboard.displayer.nvd3;

import org.jboss.dashboard.annotation.Install;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.displayer.*;
import org.jboss.dashboard.displayer.annotation.BarChart;
import org.jboss.dashboard.displayer.annotation.LineChart;
import org.jboss.dashboard.displayer.annotation.PieChart;
import org.jboss.dashboard.displayer.chart.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.*;

@Install
@BarChart
@PieChart
@LineChart
public class NVD3DisplayerRenderer extends AbstractDataDisplayerRenderer {

    public static final String UID = "nvd3";

    @Inject
    @Config(UID)
    protected String uid;

    @Inject
    @Config("")
    public String[] barChartTypes;

    @Inject
    @Config("")
    public String barChartDefault;

    @Inject @Config("")
    public String[] pieChartTypes;

    @Inject
    @Config("")
    public String pieChartDefault;

    @Inject
    @Config("")
    public String[] lineChartTypes;

    @Inject
    @Config("")
    public String lineChartDefault;

    List<DataDisplayerFeature> featuresSupported;
    Map<String, List<String>> availableChartTypes;
    Map<String, String> defaultChartTypes;

    @PostConstruct
    protected void init() {
        // Define the displaying features supported.
        featuresSupported = new ArrayList<DataDisplayerFeature>();
        featuresSupported.add(DataDisplayerFeature.ALIGN_CHART);
        featuresSupported.add(DataDisplayerFeature.SHOW_TITLE);
        featuresSupported.add(DataDisplayerFeature.SHOW_HIDE_LABELS);
        featuresSupported.add(DataDisplayerFeature.ROUND_TO_INTEGER);
        //featuresSupported.add(DataDisplayerFeature.SET_CHART_TYPE);
        featuresSupported.add(DataDisplayerFeature.SET_CHART_WIDTH);
        featuresSupported.add(DataDisplayerFeature.SET_CHART_HEIGHT);
        //featuresSupported.add(DataDisplayerFeature.SET_FOREGRND_COLOR);
        featuresSupported.add(DataDisplayerFeature.SET_LABELS_ANGLE);
        featuresSupported.add(DataDisplayerFeature.SET_MARGIN_BOTTOM);
        featuresSupported.add(DataDisplayerFeature.SET_MARGIN_TOP);
        featuresSupported.add(DataDisplayerFeature.SET_MARGIN_LEFT);
        featuresSupported.add(DataDisplayerFeature.SET_MARGIN_RIGHT);
        //featuresSupported.add(DataDisplayerFeature.SHOW_LINES_AREA);

        // Register the available chart types.
        availableChartTypes = new HashMap<String, List<String>>();
        availableChartTypes.put(BarChartDisplayerType.UID, Arrays.asList(barChartTypes));
        availableChartTypes.put(PieChartDisplayerType.UID, Arrays.asList(pieChartTypes));
        availableChartTypes.put(LineChartDisplayerType.UID, Arrays.asList(lineChartTypes));

        // Set the default chart type for each displayer type.
        defaultChartTypes = new HashMap<String, String>();
        defaultChartTypes.put(BarChartDisplayerType.UID, barChartDefault);
        defaultChartTypes.put(PieChartDisplayerType.UID, pieChartDefault);
        defaultChartTypes.put(LineChartDisplayerType.UID, lineChartDefault);
    }

    public String getUid() {
        return uid;
    }

    public String getDescription(Locale l) {
        try {
            ResourceBundle i18n = ResourceBundle.getBundle("org.jboss.dashboard.displayer.nvd3.messages", l);
            return i18n.getString("nvd3.name");
        } catch (Exception e) {
            return "SVG Charts (NVD3)";
        }
    }

    public boolean isFeatureSupported(DataDisplayer displayer, DataDisplayerFeature feature) {
        if (displayer instanceof PieChartDisplayer && feature.equals(DataDisplayerFeature.SHOW_LEGEND)) {
            return true;
        } else
        if (displayer instanceof LineChartDisplayer && feature.equals(DataDisplayerFeature.SHOW_LINES_AREA)) {
            return true;
        }
        if (displayer instanceof LineChartDisplayer && feature.equals(DataDisplayerFeature.SET_FOREGRND_COLOR)) {
            return true;
        }
        else {
            return featuresSupported.contains(feature);
        }
    }

    public List<String> getAvailableChartTypes(DataDisplayer displayer) {
        DataDisplayerType displayerType = displayer.getDataDisplayerType();
        return availableChartTypes.get(displayerType.getUid());
    }

    public String getDefaultChartType(DataDisplayer displayer) {
        DataDisplayerType displayerType = displayer.getDataDisplayerType();
        return defaultChartTypes.get(displayerType.getUid());
    }

    public String getChartTypeDescription(String chartType, Locale locale) {
        try {
            ResourceBundle i18n = ResourceBundle.getBundle("org.jboss.dashboard.displayer.nvd3.messages", locale);
            return i18n.getString("nvd3.type." + chartType);
        } catch (Exception e) {
            return chartType;
        }
    }

    public void setDefaultSettings(DataDisplayer displayer) {
        if (displayer instanceof AbstractChartDisplayer) {
            AbstractChartDisplayer chartDisplayer = (AbstractChartDisplayer) displayer;
            chartDisplayer.setMarginLeft(80);
            chartDisplayer.setMarginBottom(100);
        }
        if (displayer instanceof AbstractXAxisDisplayer) {
            AbstractXAxisDisplayer xAxisDisplayer = (AbstractXAxisDisplayer) displayer;
            xAxisDisplayer.setLabelAngleXAxis(-45);
        }
        if (displayer instanceof LineChartDisplayer) {
            LineChartDisplayer lineChartDisplayer = (LineChartDisplayer) displayer;
            lineChartDisplayer.setShowLinesArea(true);
        }
    }
}

