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

import org.jboss.dashboard.workspace.copyoptions.SectionCopyOption;

/**
 * Workspace copy operations.
 */
public interface CopyManager {
    
    /**
     * Copies this panel instance to another Workspace. If the workspace is the same, a new PanelInstance with
     * same parameters will exist after this procedure.
     * Workspace will NOT be saved after this operation.
     *
     * @param workspace  Workspace to copy this panelInstance to.
     * @param panelInstance PanelInstance to copy.
     * @return The PanelInstance created in workspace, or null if it was not possible.
     */
    PanelInstance copy(PanelInstance panelInstance, WorkspaceImpl workspace) throws Exception;

    /**
     * Copies this panel to another section.
     *
     * @param panel Panel to copy.
     * @param region  region to copy panel to.
     * @param section section to copy panel to.
     * @return The panel created, or null in case of error.
     */
    Panel copy(Panel panel, Section section, LayoutRegion region) throws Exception;

    /**
     * Copies this panel to another section.
     *
     * @param panel  Panel to copy.
     * @param region   region to copy panel to.
     * @param section  section to copy panel to.
     * @param instance Instance the panel must have. Normally, it is the same, but can be different if the panel's instance is also being copied.
     * @return The panel created, or null in case of error.
     */
    Panel copy(Panel panel, Section section, LayoutRegion region, PanelInstance instance) throws Exception;

    /**
     * Copies this section and all panels inside to another workspace.
     * The Panel references to PanelInstances are not modified.
     *
     * @param section Source section to be copied.
     * @param workspace  Destination Workspace for this section.
     * @param sco     Copy options. May be null, wich means 'do default copy'.
     * @return the new Section created, or null if an error prevented creation.
     */
    Section copy(Section section, WorkspaceImpl workspace, SectionCopyOption sco) throws Exception;

    /**
     * Copies a Workspace. It creates a Workspace with same PanelInstances and Sections inside.
     *
     * @param workspace Workspace to copy
     * @param id     Id to assign to new Workspace.
     * @return The Workspace created.
     * @throws Exception If any exception occurs
     */
    WorkspaceImpl copy(WorkspaceImpl workspace, String id) throws Exception;

    /**
     * Copies a Workspace. It creates a Workspace with same PanelInstances and Sections inside.
     *
     * @param workspace Workspace to copy
     * @return The Workspace created.
     */
    WorkspaceImpl copy(WorkspaceImpl workspace) throws Exception;
}