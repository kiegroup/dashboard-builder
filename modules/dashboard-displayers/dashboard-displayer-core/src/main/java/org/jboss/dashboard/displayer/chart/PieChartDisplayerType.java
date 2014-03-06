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

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.annotation.Install;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.displayer.DataDisplayer;
import org.jboss.dashboard.displayer.DataDisplayerRenderer;
import org.jboss.dashboard.displayer.annotation.PieChart;
import org.jboss.dashboard.export.DataDisplayerXMLFormat;

@ApplicationScoped
@Install
@PieChart
public class PieChartDisplayerType extends AbstractChartDisplayerType {

    public static final String UID = "piechart";

    @Inject @Config(UID)
    protected String uid;

    @Inject @Config(value="components/bam/images/pie.png")
    protected String iconPath;

    @Inject @Install @PieChart
    protected Instance<DataDisplayerRenderer> chartRenderers;

    @Inject
    protected ChartDisplayerXMLFormat xmlFormat;

    @Inject
    protected LocaleManager localeManager;

    @PostConstruct
    protected void init() {
        displayerRenderers = new ArrayList<DataDisplayerRenderer>();
        for (DataDisplayerRenderer renderer: chartRenderers) {
            if (renderer.isEnabled()) displayerRenderers.add(renderer);
        }
    }

    public String getUid() {
        return uid;
    }

    public String getIconPath() {
        return iconPath;
    }

    public DataDisplayerXMLFormat getXmlFormat() {
        return xmlFormat;
    }

    public String getDescription(Locale l) {
        ResourceBundle i18n = localeManager.getBundle("org.jboss.dashboard.displayer.messages", LocaleManager.currentLocale());
        return i18n.getString("pieChartDisplayer.pieDescription");
    }

    public DataDisplayer createDataDisplayer() {
        PieChartDisplayer displayer = new PieChartDisplayer();
        displayer.setDataDisplayerType(this);
        return displayer;
    }
}