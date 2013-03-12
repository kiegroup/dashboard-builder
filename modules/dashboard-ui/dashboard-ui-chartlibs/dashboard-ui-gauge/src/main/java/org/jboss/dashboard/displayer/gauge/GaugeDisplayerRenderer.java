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
package org.jboss.dashboard.displayer.gauge;

import org.jboss.dashboard.displayer.annotation.MeterChart;

import org.jboss.dashboard.annotation.Install;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.displayer.*;
import org.jboss.dashboard.displayer.chart.MeterChartDisplayerType;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.*;

@Install @MeterChart
public class GaugeDisplayerRenderer extends AbstractDataDisplayerRenderer  {

    public static final String UID = "gauge";

    @Inject @Config(UID)
    protected String uid;

    @Inject @Config("meter")
    public String[] meterChartTypes;

    @Inject @Config("meter")
    public String meterChartDefault;

    List<DataDisplayerFeature> featuresSupported;
    Map<String,List<String>> availableChartTypes;
    Map<String,String> defaultChartTypes;

    public String getUid() {
        return uid;
    }

    public String getDescription(Locale l) {
        try {
            ResourceBundle i18n = ResourceBundle.getBundle("org.jboss.dashboard.displayer.gauge.messages", l);
            return i18n.getString("gauge.name");
        } catch (Exception e) {
            return "Gauge";
        }
    }

    public boolean isFeatureSupported(DataDisplayer displayer, DataDisplayerFeature feature) {
        return featuresSupported.contains(feature);
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
            ResourceBundle i18n = ResourceBundle.getBundle("org.jboss.dashboard.displayer.gauge.messages", locale);
            return i18n.getString("gauge.type." + chartType);
        } catch (Exception e) {
            return chartType;
        }
    }

    @PostConstruct
    public void init() {
        // Define the displaying features supported by the Gauge renderer.
        featuresSupported = new ArrayList<DataDisplayerFeature>();
        featuresSupported.add(DataDisplayerFeature.ALIGN_CHART);
        featuresSupported.add(DataDisplayerFeature.SHOW_TITLE);
        featuresSupported.add(DataDisplayerFeature.SHOW_LEGEND);
        featuresSupported.add(DataDisplayerFeature.ROUND_TO_INTEGER);
        //featuresSupported.add(DataDisplayerFeature.SET_CHART_WIDTH);
        //featuresSupported.add(DataDisplayerFeature.SET_CHART_HEIGHT);
        featuresSupported.add(DataDisplayerFeature.SET_FOREGRND_COLOR);
        featuresSupported.add(DataDisplayerFeature.SET_CHART_TYPE);

        // Register the available chart types.
        availableChartTypes = new HashMap<String, List<String>>();
        availableChartTypes.put(MeterChartDisplayerType.UID, Arrays.asList(meterChartTypes));

        // Set the default chart type for each displayer type.
        defaultChartTypes = new HashMap<String, String>();
        defaultChartTypes.put(MeterChartDisplayerType.UID, meterChartDefault);
    }
}
