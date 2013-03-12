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
package org.jboss.dashboard.workspace.events;

/**
 * Date: 27-may-2004
 * Time: 17:39:42
 */
public class WorkspaceAdapter implements WorkspaceListener {
    /**
     * Called when a Workspace is created.
     *
     * @param workspaceEvent The event containing the related data.
     */
    public void workspaceCreated(WorkspaceEvent workspaceEvent) {
    }

    /**
     * Called when a Workspace is deleted.
     *
     * @param workspaceEvent The event containing the related data.
     */
    public void workspaceRemoved(WorkspaceEvent workspaceEvent) {
    }

    /**
     * Called when a Workspace is updated.
     *
     * @param workspaceEvent The event containing the related data.
     */
    public void workspaceUpdated(WorkspaceEvent workspaceEvent) {
    }

    /**
     * Called when a Workspace is duplicated.
     *
     * @param workspaceEvent The event containing the related data.
     */
    public void workspaceDuplicated(WorkspaceDuplicationEvent workspaceEvent) {
    }

    public void workspaceWizardFinished(WorkspaceDuplicationEvent workspaceEvent) {
    }
}
