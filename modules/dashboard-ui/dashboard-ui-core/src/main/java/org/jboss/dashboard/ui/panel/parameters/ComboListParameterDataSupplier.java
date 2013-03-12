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
package org.jboss.dashboard.ui.panel.parameters;

import org.jboss.dashboard.workspace.PanelInstance;

import java.util.List;

/**
 * Supplies data for ComboListParameter in a panel
 */
public interface ComboListParameterDataSupplier {

    /**
     * Called to initialize status
     *
     * @param instance instance to initialize status for.
     */
    void init(PanelInstance instance);

    /**
     * Returns the values to display for a given selection control (Strings)
     *
     * @return the values to display for a given selection control
     */
    List getValues();

    /**
     * Returns the keys for displayed values (Strings)
     *
     * @return the keys for displayed values
     */
    List getKeys();
}
