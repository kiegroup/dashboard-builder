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
package org.jboss.dashboard.ui;

import java.util.HashMap;
import java.util.Map;

import org.jboss.dashboard.displayer.DataDisplayerRenderer;
import org.jboss.dashboard.displayer.DataDisplayerType;
import org.jboss.dashboard.ui.components.*;
import org.jboss.dashboard.displayer.DataDisplayer;
import org.jboss.dashboard.kpi.KPI;
import org.jboss.dashboard.provider.DataProviderType;
import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 * The locator service for several UI beans.
 */
@ApplicationScoped
@Named("UIBeanLocator")
public class UIBeanLocator {

    public static UIBeanLocator lookup() {
        return (UIBeanLocator) CDIBeanLocator.getBeanByName("UIBeanLocator");
    }

    protected Map<String,String> dataProviderEditorMap;
    protected Map<String,String> dataDisplayerViewerMap;
    protected Map<String,String> dataDisplayerEditorMap;

    @PostConstruct
    public void init() throws Exception {
        // Register the provider editors that come de-facto with the product.
        dataProviderEditorMap = new HashMap<String, String>();
        dataProviderEditorMap.put("sql", "org.jboss.dashboard.ui.components.SQLProviderEditor");
        dataProviderEditorMap.put("csv", "org.jboss.dashboard.ui.components.CSVProviderEditor");

        // Register the displayer editors that come de-facto with the product.
        dataDisplayerEditorMap = new HashMap<String, String>();
        dataDisplayerEditorMap.put("barchart", "org.jboss.dashboard.ui.components.BarChartEditor");
        dataDisplayerEditorMap.put("piechart", "org.jboss.dashboard.ui.components.PieChartEditor");
        dataDisplayerEditorMap.put("linechart", "org.jboss.dashboard.ui.components.LineChartEditor");
        dataDisplayerEditorMap.put("meterchart", "org.jboss.dashboard.ui.components.MeterChartEditor");
        dataDisplayerEditorMap.put("table", "org.jboss.dashboard.ui.components.TableEditor");

        // Register the displayer viewers that come de-facto with the product.
        dataDisplayerViewerMap = new HashMap<String, String>();
        dataDisplayerViewerMap.put("barchart", "org.jboss.dashboard.ui.components.BarChartViewer");
        dataDisplayerViewerMap.put("piechart", "org.jboss.dashboard.ui.components.PieChartViewer");
        dataDisplayerViewerMap.put("linechart", "org.jboss.dashboard.ui.components.LineChartViewer");
        dataDisplayerViewerMap.put("meterchart", "org.jboss.dashboard.ui.components.MeterChartViewer");
        dataDisplayerViewerMap.put("table", "org.jboss.dashboard.ui.components.TableViewer");
    }

    /**
     * Get the editor component for the specified KPI.
     */
    public KPIEditor getEditor(KPI target) {
        String editorPath = "org.jboss.dashboard.ui.components.KPIEditor";
        KPIEditor editor = (KPIEditor) Factory.lookup(editorPath);
        editor.setKpi(target);
        return editor;
    }

    /**
     * Get the viewer component for the specified KPI.
     */
    public KPIViewer getViewer(KPI target) {
        String viewerPath = "org.jboss.dashboard.ui.components.KPIViewer";
        KPIViewer viewer = (KPIViewer) Factory.lookup(viewerPath);
        viewer.setKpi(target);
        return viewer;
    }

    /**
     * Get the editor component for the specified data provider.
     */
    public DataProviderEditor getEditor(DataProviderType target) {
        String name = dataProviderEditorMap.get(target.getUid());
        return (DataProviderEditor) Factory.lookup(name);
    }

    /**
     * Get the editor component for the specified data displayer.
     */
    public DataDisplayerEditor getEditor(DataDisplayer target) {
        DataDisplayerType type = target.getDataDisplayerType();
        String name = dataDisplayerEditorMap.get(type.getUid());
        DataDisplayerEditor editor = (DataDisplayerEditor) Factory.lookup(name);
        editor.setDataDisplayer(target);
        return (DataDisplayerEditor) Factory.lookup(name);
    }

    /**
     * Get the viewer component for the specified data displayer.
     */
    public DataDisplayerViewer getViewer(DataDisplayer target) {
        DataDisplayerType type = target.getDataDisplayerType();
        String prefix = dataDisplayerViewerMap.get(type.getUid());

        DataDisplayerRenderer lib = target.getDataDisplayerRenderer();
        String name = prefix + "_" + lib.getUid();
        DataDisplayerViewer viewer = (DataDisplayerViewer) Factory.lookup(name);
        viewer.setDataDisplayer(target);
        return viewer;
    }
}