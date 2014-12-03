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
package org.jboss.dashboard.displayer;

import org.jboss.dashboard.displayer.exception.DataDisplayerInvalidConfiguration;
import org.jboss.dashboard.provider.DataProvider;

import java.util.List;

/**
 * Base class for the implementation of custom data displayers.
 */
public abstract class AbstractDataDisplayer implements DataDisplayer {

    protected DataDisplayerType dataDisplayerType;
    protected DataDisplayerRenderer dataDisplayerRenderer;
    protected DataProvider dataProvider;

    protected AbstractDataDisplayer() {
        dataDisplayerType = null;
        dataDisplayerRenderer = null;
        dataProvider = null;
    }

    public DataDisplayerType getDataDisplayerType() {
        return dataDisplayerType;
    }

    public void setDataDisplayerType(DataDisplayerType type) {
        this.dataDisplayerType = type;
    }

    public void setDataProvider(DataProvider dp) throws DataDisplayerInvalidConfiguration {

        // If provider is valid, set it.
        dataProvider = dp;

        // If data provider definition does not match with displayer configuration, do not set the provider to the table object.
        validate(dp);
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    public DataDisplayerRenderer getDataDisplayerRenderer() {
        List<DataDisplayerRenderer> renderers = dataDisplayerType.getSupportedRenderers();
        if (dataDisplayerRenderer == null || !renderers.contains(dataDisplayerRenderer)) {
            dataDisplayerRenderer = dataDisplayerType.getDefaultRenderer();
        }
        return dataDisplayerRenderer;
    }

    public void setDataDisplayerRenderer(DataDisplayerRenderer renderer) {
        List<DataDisplayerRenderer> renderers = dataDisplayerType.getSupportedRenderers();
        if (renderer != null && renderers.contains(renderer)) {
            this.dataDisplayerRenderer = renderer;
        }
    }

    public void setDefaultSettings() {
        // The settings differs depending on the type of the renderer.
        getDataDisplayerRenderer().setDefaultSettings(this);
    }

    public void copyFrom(DataDisplayer sourceDisplayer) throws DataDisplayerInvalidConfiguration {
        try {
            AbstractDataDisplayer source = (AbstractDataDisplayer) sourceDisplayer;
            setDataDisplayerRenderer(source.getDataDisplayerRenderer());
            setDataProvider(source.getDataProvider());
        } catch (ClassCastException e) {
            // Ignore wrong types
        }
    }
}
