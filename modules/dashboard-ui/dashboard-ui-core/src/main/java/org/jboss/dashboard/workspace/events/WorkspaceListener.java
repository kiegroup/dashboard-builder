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
package org.jboss.dashboard.workspace.events;

/**
 * 
 */
public interface WorkspaceListener extends EventListener {
    /**
     * Called when a Workspace is created.
     *
     * @param workspaceEvent The event containing the related data.
     */
    public void workspaceCreated(WorkspaceEvent workspaceEvent);

    /**
     * Called when a Workspace is deleted.
     *
     * @param workspaceEvent The event containing the related data.
     */
    public void workspaceRemoved(WorkspaceEvent workspaceEvent);

    /**
     * Called when a Workspace is updated.
     *
     * @param workspaceEvent The event containing the related data.
     */
    public void workspaceUpdated(WorkspaceEvent workspaceEvent);

    /**
     * Called when a Workspace is duplicated.
     *
     * @param workspaceEvent The event containing the related data.
     */
    public void workspaceDuplicated(WorkspaceDuplicationEvent workspaceEvent);

    /**
     * Called when a Workspace is duplicated and the wizard finishes.
     *
     * @param workspaceEvent The event containing the related data.
     */
    public void workspaceWizardFinished(WorkspaceDuplicationEvent workspaceEvent);
}
