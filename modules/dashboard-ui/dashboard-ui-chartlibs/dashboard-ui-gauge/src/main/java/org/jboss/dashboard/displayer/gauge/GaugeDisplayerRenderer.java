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

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.displayer.annotation.MeterChart;

import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.displayer.*;
import org.jboss.dashboard.displayer.chart.MeterChartDisplayerType;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.components.js.JSIncluder;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.*;

@MeterChart
//@Install -- Under development
public class GaugeDisplayerRenderer extends AbstractDataDisplayerRenderer  {

    public static final String UID = "gauge";

    @Inject @Config("true")
    protected boolean enabled;

    @Inject @Config("meter")
    protected String[] meterChartTypes;

    @Inject @Config("meter")
    protected String meterChartDefault;

    @Inject @Config("/components/bam/displayer/chart/gauge/raphael.2.1.0.min.js," +
                    "/components/bam/displayer/chart/gauge/justgage.1.0.1.min.js")
    private List<String> jsFiles;

    protected List<DataDisplayerFeature> featuresSupported;
    protected Map<String,List<String>> availableChartTypes;
    protected Map<String,String> defaultChartTypes;

    @Inject
    protected LocaleManager localeManager;

    public boolean isEnabled() {
        return enabled;
    }

    public String getUid() {
        return UID;
    }

    public String getDescription(Locale l) {
        try {
            ResourceBundle i18n = localeManager.getBundle("org.jboss.dashboard.displayer.gauge.messages", l);
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
            ResourceBundle i18n = localeManager.getBundle("org.jboss.dashboard.displayer.gauge.messages", locale);
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
        featuresSupported.add(DataDisplayerFeature.SHOW_LEGEND_POSITION);
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

        // If enabled then ensure the JS API files are included into the app header.
        if (enabled) {
            JSIncluder jsIncluder = UIServices.lookup().getJsIncluder();
            for (String jsFile : jsFiles) {
                jsIncluder.addJsHeaderFile(jsFile);
            }
        }
    }
}
