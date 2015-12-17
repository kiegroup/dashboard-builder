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

/**
 * Definitions for some parameters' names passed to some JSPs, such as the panel
 * being rendered identifier. The system relies on these names to perform some operations.
 * Therefore, we make them accessible by defining them here, to avoid "magic"
 * strings spread along the program.
 */
public interface Parameters {

    /**
     * Pane id to be rendered (needs the order parameter to select panel)
     */
    public static final String RENDER_IDREGION = "render_idregion";

    /**
     * Selects panel for actions
     */
    public static final String DISPATCH_IDPANEL = "idPanel";

    /**
     * Selects workspace for actions
     */
    public static final String DISPATCH_IDWORKSPACE = "idWorkspace";

    /**
     * Selects panel for actions
     */
    public static final String DISPATCH_ACTION = "pAction";

    /**
     * Enables the AJAX behaviour on actions
     */
    public static final String AJAX_ACTION = "ajaxAction";

    /**
     * The embedded request parameter.
     */
    public static final String PARAM_EMBEDDED = "embedded";
}

