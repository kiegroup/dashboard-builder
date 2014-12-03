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

/**
 * A data displayer is a component that is able to render graphically the data gathered by a data provider.
 */
public interface DataDisplayer extends Cloneable {

    /**
     * The type of this displayer.
     */
    DataDisplayerType getDataDisplayerType();
    void setDataDisplayerType(DataDisplayerType type);

    /**
     * The data provider which feeds this displayer with data.
     * 
     * @throws IllegalArgumentException If a property in data displayer is not present in the given data provider.
     */
    DataProvider getDataProvider();
    void setDataProvider(DataProvider provider) throws DataDisplayerInvalidConfiguration;

    /**
     * The library used to render the displayer.
     */
    DataDisplayerRenderer getDataDisplayerRenderer();
    void setDataDisplayerRenderer(DataDisplayerRenderer library);

    /**
     * Restore the default settings.
     */
    void setDefaultSettings();

    /**
     * Copies the configuration of one displayer into this.
     * @param source The displayer with the configuration to copy.
     * @throws IllegalArgumentException If a property in data displayer is not present in the given data provider.
     */
    void copyFrom(DataDisplayer source) throws DataDisplayerInvalidConfiguration;

    /**
     * Check if the current displayer properties are present in the given data provider.
     * 
     * @param provider The data provider.
     * @throws IllegalArgumentException If a property in data displayer is not present in the given data provider.
     */
    void validate(DataProvider provider) throws DataDisplayerInvalidConfiguration;
}
