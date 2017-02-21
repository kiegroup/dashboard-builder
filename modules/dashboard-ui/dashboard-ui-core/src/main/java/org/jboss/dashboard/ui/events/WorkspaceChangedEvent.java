/**
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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
package org.jboss.dashboard.ui.events;

import org.jboss.dashboard.ui.NavigationManager;
import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.WorkspaceImpl;

/**
 * Fired when the current workspace changes
 * (see {@link NavigationManager#setCurrentWorkspace(WorkspaceImpl)}).
 */
public class WorkspaceChangedEvent {

    Workspace oldWorkspace;
    Workspace newWorkspace;

    public WorkspaceChangedEvent() {
    }

    public WorkspaceChangedEvent(Workspace oldWorkspace, Workspace newWorkspace) {
        this.oldWorkspace = oldWorkspace;
        this.newWorkspace = newWorkspace;
    }

    public Workspace getOldWorkspace() {
        return oldWorkspace;
    }

    public Workspace getNewWorkspace() {
        return newWorkspace;
    }
}
