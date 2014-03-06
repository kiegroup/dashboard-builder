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
import org.jboss.dashboard.displayer.annotation.BarChart;
import org.jboss.dashboard.export.DataDisplayerXMLFormat;

@ApplicationScoped
@Install
@BarChart
public class BarChartDisplayerType extends AbstractChartDisplayerType {

    public static final String UID = "barchart";

    @Inject @Config(UID)
    protected String uid;

    @Inject @Config(value="components/bam/images/bar.png")
    protected String iconPath;

    @Inject @Install @BarChart
    protected Instance<DataDisplayerRenderer> chartRenderers;

    @Inject
    protected LocaleManager localeManager;

    @Inject
    protected ChartDisplayerXMLFormat xmlFormat;

    @PostConstruct
    protected void init() {
        displayerRenderers = new ArrayList<DataDisplayerRenderer>();
        for (DataDisplayerRenderer type: chartRenderers) {
            displayerRenderers.add(type);
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
        return i18n.getString("barChartDisplayer.barDescription");
    }

    public DataDisplayer createDataDisplayer() {
        BarChartDisplayer displayer = new BarChartDisplayer();
        displayer.setDataDisplayerType(this);
        return displayer;
    }
}
