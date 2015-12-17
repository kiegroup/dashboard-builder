/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jboss.dashboard.workspace;

import org.jboss.dashboard.workspace.events.ListenerQueue;

import java.util.Set;

/**
* PanelsManager, implements operations related to panels and panel instances.
*/
public interface PanelsManager extends ListenerQueue {

    /**
     * Removes a panel instance. All associated panels are also removed.
     */
    void delete(PanelInstance instance) throws Exception;

    /**
     * Removes a panel.
     */
    void delete(Panel panel) throws Exception;

    /**
     * Persist panel to database
     */
    void store(Panel panel) throws Exception;

    /**
     * Persist panel Instance to database
     */
    void store(PanelInstance instance) throws Exception;

    /**
     * Get a panel by its logical id.
     */
    Panel getPanelById(Long panelId) throws Exception;

    /**
     * Get a panel by its database id.
     */
    Panel getPanelByDbId(Long panelId) throws Exception;

    /**
     * Get a set of panels containing a parameter with a given value.
     */
    Set<PanelInstance> getPanelsByParameter(String paramId, String value) throws Exception;
}
