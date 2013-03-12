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

import org.jboss.dashboard.workspace.Workspace;
import org.jboss.dashboard.workspace.Workspace;

public class WorkspaceDuplicationEvent extends WorkspaceEvent {

    Workspace clone;

    public WorkspaceDuplicationEvent(String eventId, Workspace source, Workspace clone) {
        super(eventId, source);
        this.clone = clone;
    }

    public Workspace getClone() {
        return clone;
    }
}
