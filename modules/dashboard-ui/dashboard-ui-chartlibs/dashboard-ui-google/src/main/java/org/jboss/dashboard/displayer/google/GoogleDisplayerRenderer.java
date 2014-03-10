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
package org.jboss.dashboard.displayer.google;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.annotation.Install;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.displayer.AbstractDataDisplayerRenderer;
import org.jboss.dashboard.displayer.DataDisplayer;
import org.jboss.dashboard.displayer.DataDisplayerFeature;
import org.jboss.dashboard.displayer.DataDisplayerType;
import org.jboss.dashboard.displayer.annotation.BarChart;
import org.jboss.dashboard.displayer.annotation.LineChart;
import org.jboss.dashboard.displayer.annotation.MapChart;
import org.jboss.dashboard.displayer.annotation.PieChart;
import org.jboss.dashboard.displayer.chart.*;
import org.jboss.dashboard.displayer.map.MapDisplayerType;
import org.jboss.dashboard.ui.UIServices;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.*;

@Install @MapChart @BarChart @PieChart @LineChart
public class GoogleDisplayerRenderer extends AbstractDataDisplayerRenderer {

    public static final String UID = "google";

    @Inject @Config("false")
    protected boolean enabled;

    @Inject @Config("https://www.google.com/jsapi")
    protected String jsApiUrl;

    @Inject @Config("")
    protected String[] barChartTypes;

    @Inject @Config("")
    protected String barChartDefault;

    @Inject @Config("")
    protected String[] pieChartTypes;

    @Inject @Config("")
    protected String pieChartDefault;

    @Inject @Config("")
    protected String[] lineChartTypes;

    @Inject @Config("")
    protected String lineChartDefault;

    @Inject @Config("GeoChart, GeoMap")
    protected String[] mapChartTypes;

    @Inject @Config("GeoChart")
    protected String mapChartDefault;

    protected List<DataDisplayerFeature> featuresSupported;
    protected Map<String, List<String>> availableChartTypes;
    protected Map<String, String> defaultChartTypes;

    @Inject
    protected LocaleManager localeManager;

    @PostConstruct
    protected void init() {
        // Define the displaying features supported.
        featuresSupported = new ArrayList<DataDisplayerFeature>();
        featuresSupported.add(DataDisplayerFeature.ALIGN_CHART);
        featuresSupported.add(DataDisplayerFeature.SHOW_TITLE);
        featuresSupported.add(DataDisplayerFeature.SET_CHART_TYPE);
        featuresSupported.add(DataDisplayerFeature.SET_CHART_WIDTH);
        featuresSupported.add(DataDisplayerFeature.SET_CHART_HEIGHT);
        featuresSupported.add(DataDisplayerFeature.ROUND_TO_INTEGER);
        featuresSupported.add(DataDisplayerFeature.SET_FOREGRND_COLOR);

        // Register the available chart types.
        availableChartTypes = new HashMap<String, List<String>>();
        availableChartTypes.put(BarChartDisplayerType.UID, Arrays.asList(barChartTypes));
        availableChartTypes.put(PieChartDisplayerType.UID, Arrays.asList(pieChartTypes));
        availableChartTypes.put(LineChartDisplayerType.UID, Arrays.asList(lineChartTypes));
        availableChartTypes.put(MapDisplayerType.UID, Arrays.asList(mapChartTypes));

        // Set the default chart type for each displayer type.
        defaultChartTypes = new HashMap<String, String>();
        defaultChartTypes.put(BarChartDisplayerType.UID, barChartDefault);
        defaultChartTypes.put(PieChartDisplayerType.UID, pieChartDefault);
        defaultChartTypes.put(LineChartDisplayerType.UID, lineChartDefault);
        defaultChartTypes.put(MapDisplayerType.UID, mapChartDefault);

        // If enabled then ensure the JSP API file is included into the app header.
        if (enabled) {
            UIServices.lookup().getJsIncluder().addJsHeaderFile(jsApiUrl);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getUid() {
        return UID;
    }

    public String getDescription(Locale l) {
        try {
            ResourceBundle i18n = localeManager.getBundle("org.jboss.dashboard.displayer.google.messages", l);
            return i18n.getString("google.name");
        } catch (Exception e) {
            return "SVG Charts (Google)";
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
            ResourceBundle i18n = localeManager.getBundle("org.jboss.dashboard.displayer.google.messages", locale);
            return i18n.getString("google.type." + chartType.toLowerCase());
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

