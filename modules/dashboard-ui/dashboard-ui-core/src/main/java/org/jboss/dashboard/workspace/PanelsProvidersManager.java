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
package org.jboss.dashboard.workspace;

import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.ui.panel.PanelProvider;

import java.util.Locale;

/**
 * Manages installed panel providers
 */
public interface PanelsProvidersManager {

    /**
     * Returns all panels providers installed in the system
     *
     * @return An array of available panel providers
     */
    PanelProvider[] getProviders();

    /**
     * Returns all panels providers installed in the system allowed for given workspace
     *
     * @param workspace Workspace that allows returned providers.
     * @return An array of available panel providers
     */
    PanelProvider[] getProviders(Workspace workspace);

    /**
     * Returns a panel provider by ID
     *
     * @param id Provider id to return
     * @return a panel provider by its id
     * @throws Exception
     */
    PanelProvider getProvider(String id) throws Exception;

    /**
     * Enumerates all existing groups of providers
     *
     * @return an array of provider groups available
     */
    String[] enumerateProvidersGroups();

    /**
     * Enumerates all existing groups of providers containing panel Instances allowed by given workspace
     *
     * @param workspace Workspace that allows returned providers.
     * @return an array of provider groups available
     */
    String[] enumerateProvidersGroups(Workspace workspace);

    /**
     * Get the group display text
     *
     * @param groupId Group id to use
     * @return A human readable group name
     */
    String getGroupDisplayName(String groupId);

    /**
     * Get the group display text
     *
     * @param groupId Group id to use
     * @param locale  Locale in which the description has to be
     * @return A human readable group name
     */
    String getGroupDisplayName(String groupId, Locale locale);

    /**
     * Returns all providers belonging to a given group, sorted by description
     *
     * @param group or null if we want all panels NOT belonging to any group
     * @return all providers belonging to a given group, sorted by description
     */
    PanelProvider[] getProvidersInGroup(String group);

    /**
     * Returns all providers belonging to a given group, sorted by description,
     * and allowed by given workspace
     *
     * @param group  or null if we want all panels NOT belonging to any group
     * @param workspace Workspace that allows returned providers.
     * @return all providers belonging to a given group, sorted by description
     */
    PanelProvider[] getProvidersInGroup(String group, Workspace workspace);

    /**
     * @return The providers installed, but not licensed for use in the workspace.
     */
    PanelProvider[] getDisabledProviders();

    /**
     * Returns all providers belonging to a given group, sorted by description, but
     * disabled by license.
     *
     * @param group or null if we want all panels NOT belonging to any group
     * @return The providers in group, but not licensed for use in the workspace.
     */
    PanelProvider[] getDisabledProvidersInGroup(String group);

    /**
     * Enumerates all existing groups of providers containing only disabled panel Instances
     *
     * @return all existing groups of providers containing only disabled panel Instances
     */
    public String[] enumerateDisabledProvidersGroups();

    public String getProviderGroupImage(String groupId);
}
