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
package org.jboss.dashboard.displayer.ofc2;

import org.jboss.dashboard.annotation.Install;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.displayer.*;
import org.jboss.dashboard.displayer.annotation.BarChart;
import org.jboss.dashboard.displayer.annotation.LineChart;
import org.jboss.dashboard.displayer.annotation.PieChart;
import org.jboss.dashboard.displayer.chart.BarChartDisplayerType;
import org.jboss.dashboard.displayer.chart.LineChartDisplayerType;
import org.jboss.dashboard.displayer.chart.PieChartDisplayerType;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.*;

@Install @BarChart @PieChart @LineChart
public class OFC2DisplayerRenderer extends AbstractDataDisplayerRenderer  {

    public static final String UID = "ofc2";

    @Inject @Config(UID)
    protected String uid;

    @Inject @Config("bar_glass, bar, bar_3d, bar_filled, bar_cylinder, bar_round, bar_sketch")
    public String[] barChartTypes;

    @Inject @Config("bar_filled")
    public String barChartDefault;

    @Inject @Config("pie")
    public String[] pìeChartTypes;

    @Inject @Config("pie")
    public String pieChartDefault;

    @Inject @Config("line")
    public String[] lineChartTypes;

    @Inject @Config("line")
    public String lineChartDefault;

    List<DataDisplayerFeature> featuresSupported;
    Map<String,List<String>> availableChartTypes;
    Map<String,String> defaultChartTypes;
    
    @PostConstruct
    protected void init() {
        // Define the displaying features supported by the JFree renderer.
        featuresSupported = new ArrayList<DataDisplayerFeature>();
        featuresSupported.add(DataDisplayerFeature.ALIGN_CHART);
        featuresSupported.add(DataDisplayerFeature.SHOW_TITLE);
        featuresSupported.add(DataDisplayerFeature.SHOW_HIDE_LABELS);
        featuresSupported.add(DataDisplayerFeature.SHOW_LEGEND);
        featuresSupported.add(DataDisplayerFeature.ROUND_TO_INTEGER);
        featuresSupported.add(DataDisplayerFeature.SET_CHART_WIDTH);
        featuresSupported.add(DataDisplayerFeature.SET_CHART_HEIGHT);
        featuresSupported.add(DataDisplayerFeature.SET_FOREGRND_COLOR);
        featuresSupported.add(DataDisplayerFeature.SET_LABELS_ANGLE);

        // Register the available chart types.
        availableChartTypes = new HashMap<String, List<String>>();
        availableChartTypes.put(BarChartDisplayerType.UID, Arrays.asList(barChartTypes));
        availableChartTypes.put(PieChartDisplayerType.UID, Arrays.asList(pìeChartTypes));
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
            ResourceBundle i18n = ResourceBundle.getBundle("org.jboss.dashboard.displayer.ofc2.messages", l);
            return i18n.getString("ofc2.name");
        } catch (Exception e) {
            return "Open Flash Chart 2";
        }
    }

    public boolean isFeatureSupported(DataDisplayer displayer, DataDisplayerFeature feature) {
        if (DataDisplayerFeature.SET_CHART_TYPE == feature) {
            // Chart type selection only makes sense for bar charts, since pie and line has only one single type available.
            return (displayer.getDataDisplayerType().getUid().equals(BarChartDisplayerType.UID));
        } else {
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
            ResourceBundle i18n = ResourceBundle.getBundle("org.jboss.dashboard.displayer.ofc2.messages", locale);
            return i18n.getString("ofc2.type." + chartType);
        } catch (Exception e) {
            return chartType;
        }
    }
}

