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
package org.jboss.dashboard.ui.resources;

/**
 * Defines the possible scopes for an element.
 * Skins may be defined for workspaces, sections, panels, or global
 * Resource Galleries are defined for instances instead of panels.
 * Envelopes and Layouts cannot be defined for panels or instances, only up to Section level.
 * Future elements will have to define its own behaviur if any.
 */
public class GraphicElementScopeDescriptor {

    boolean allowedGlobal;
    boolean allowedWorkspace;
    boolean allowedSection;
    boolean allowedPanel;
    boolean allowedInstance;

//    public static GraphicElementScopeDescriptor PANEL_SCOPED = new GraphicElementScopeDescriptor(true, true, true, false, true);
//    public static GraphicElementScopeDescriptor INSTANCE_SCOPED = new GraphicElementScopeDescriptor(true, true, true, true, false);
    public static GraphicElementScopeDescriptor SECTION_SCOPED = new GraphicElementScopeDescriptor(true, true, true, false, false);
    public static GraphicElementScopeDescriptor WORKSPACE_SCOPED = new GraphicElementScopeDescriptor(true, true, false, false, false);
    public static GraphicElementScopeDescriptor GLOBAL_SCOPED = new GraphicElementScopeDescriptor(true, false, false, false, false);


    private GraphicElementScopeDescriptor(boolean global, boolean workspace, boolean section, boolean instance, boolean panel) {
        this.allowedGlobal = global;
        this.allowedWorkspace = workspace;
        this.allowedSection = section;
        this.allowedInstance = instance; //instance and panel must not be true at the same time!!!
        this.allowedPanel = panel;
    }

    public boolean isAllowedGlobal() {
        return allowedGlobal;
    }

    public boolean isAllowedWorkspace() {
        return allowedWorkspace;
    }

    public boolean isAllowedSection() {
        return allowedSection;
    }

    public boolean isAllowedPanel() {
        return allowedPanel;
    }

    public boolean isAllowedInstance() {
        return allowedInstance;
    }

    public boolean equals(Object o) {
        GraphicElementScopeDescriptor other = (GraphicElementScopeDescriptor) o;
        return this.allowedGlobal == other.allowedGlobal &&
                this.allowedWorkspace == other.allowedWorkspace &&
                this.allowedSection == other.allowedSection &&
                this.allowedPanel == other.allowedPanel &&
                this.allowedInstance == other.allowedInstance;
    }
}
