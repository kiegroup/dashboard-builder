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
package org.jboss.dashboard.displayer.jfree;

import org.jboss.dashboard.annotation.Install;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.displayer.*;
import org.jboss.dashboard.displayer.annotation.BarChart;
import org.jboss.dashboard.displayer.annotation.MeterChart;
import org.jboss.dashboard.displayer.annotation.PieChart;
import org.jboss.dashboard.displayer.chart.BarChartDisplayerType;
import org.jboss.dashboard.displayer.chart.MeterChartDisplayerType;
import org.jboss.dashboard.displayer.chart.PieChartDisplayerType;
import de.laures.cewolf.taglib.CewolfChartFactory;
import de.laures.cewolf.taglib.IncompatibleDatasetException;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.ValueDataset;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.awt.Font;
import java.util.*;

@Install @BarChart @PieChart @MeterChart
public class JFreeDisplayerRenderer extends AbstractDataDisplayerRenderer {

    public static final String UID = "jfree";

    @Inject @Config(UID)
    protected String uid;

    @Inject @Config("horizontalbar, horizontalbar3d, verticalbar, verticalbar3d")
    public String[] barChartTypes;

    @Inject @Config("verticalbar")
    public String barChartDefault;

    @Inject @Config("pie")
    public String[] pieChartTypes;

    @Inject @Config("pie")
    public String pieChartDefault;

    @Inject @Config("meter, thermometer, dial")
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
            ResourceBundle i18n = ResourceBundle.getBundle("org.jboss.dashboard.displayer.jfree.messages", l);
            return i18n.getString("jfree.name");
        } catch (Exception e) {
            return "JFreeChart";
        }
    }

    public boolean isFeatureSupported(DataDisplayer displayer, DataDisplayerFeature feature) {
        if (DataDisplayerFeature.SET_CHART_TYPE == feature) {
            // Chart type selection doesn't make sense for pie charts.
            return (!displayer.getDataDisplayerType().getUid().equals(PieChartDisplayerType.UID));
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
            ResourceBundle i18n = ResourceBundle.getBundle("org.jboss.dashboard.displayer.jfree.messages", locale);
            return i18n.getString("jfree.type." + chartType);
        } catch (Exception e) {
            return chartType;
        }
    }

    @PostConstruct
    public void init() {
        // Define the displaying features supported by the JFree renderer.
        featuresSupported = new ArrayList<DataDisplayerFeature>();
        featuresSupported.add(DataDisplayerFeature.ALIGN_CHART);
        featuresSupported.add(DataDisplayerFeature.SHOW_TITLE);
        featuresSupported.add(DataDisplayerFeature.SHOW_LEGEND);
        featuresSupported.add(DataDisplayerFeature.ROUND_TO_INTEGER);
        featuresSupported.add(DataDisplayerFeature.SET_CHART_TYPE);
        featuresSupported.add(DataDisplayerFeature.SET_CHART_WIDTH);
        featuresSupported.add(DataDisplayerFeature.SET_CHART_HEIGHT);
        featuresSupported.add(DataDisplayerFeature.SET_FOREGRND_COLOR);

        // Register the available chart types.
        availableChartTypes = new HashMap<String, List<String>>();
        availableChartTypes.put(BarChartDisplayerType.UID, Arrays.asList(barChartTypes));
        availableChartTypes.put(PieChartDisplayerType.UID, Arrays.asList(pieChartTypes));
        availableChartTypes.put(MeterChartDisplayerType.UID, Arrays.asList(meterChartTypes));

        // Set the default chart type for each displayer type.
        defaultChartTypes = new HashMap<String, String>();
        defaultChartTypes.put(BarChartDisplayerType.UID, barChartDefault);
        defaultChartTypes.put(PieChartDisplayerType.UID, pieChartDefault);
        defaultChartTypes.put(MeterChartDisplayerType.UID, meterChartDefault);

        // Cewolf patch in the creation of combined meter, dial & thermometer charts. Always hide the legend.
        CewolfChartFactory.registerFactory(new CewolfChartFactory("combinedmeter") {
            public JFreeChart getChartInstance(String title, String xAxisLabel, String yAxisLabel, Dataset data) throws IncompatibleDatasetException {
                check(data, ValueDataset.class, "combinedmeter");
                MeterPlot plot = new MeterPlot((ValueDataset) data);
                return new JFreeChart(title, new Font("SansSerif", Font.BOLD, 18), plot, false);
            }
        });
        CewolfChartFactory.registerFactory(new CewolfChartFactory("combineddial") {
            public JFreeChart getChartInstance(String title, String xAxisLabel, String yAxisLabel, Dataset data) throws IncompatibleDatasetException {
                check(data, ValueDataset.class, "combineddial");
                DialPlot dplot = new DialPlot((ValueDataset) data);
                dplot.addPointer(new DialPointer.Pin());
                StandardDialScale scale = new StandardDialScale();
                scale.setTickLabelFont(new Font("Dialog", Font.BOLD, 10));
                dplot.addScale(0, scale);
                return new JFreeChart(title, new Font("SansSerif", Font.BOLD, 18), dplot, false);
            }
        });
        CewolfChartFactory.registerFactory(new CewolfChartFactory("combinedthermometer") {
            public JFreeChart getChartInstance(String title, String xAxisLabel, String yAxisLabel, Dataset data) throws IncompatibleDatasetException {
                check(data, ValueDataset.class, "combinedthermometer");
                ThermometerPlot tplot = new ThermometerPlot((ValueDataset) data);
                return new JFreeChart(title, new Font("SansSerif", Font.BOLD, 18), tplot, false);
            }
        });
    }
}
