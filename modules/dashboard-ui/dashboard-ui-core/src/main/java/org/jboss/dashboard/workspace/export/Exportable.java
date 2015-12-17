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
package org.jboss.dashboard.workspace.export;

import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.PanelInstance;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Definition interface that must implement the panel drivers that need to export it's content on the workspaces export file.
 */
public interface Exportable {

    /**
     * Write instance content to given OutputStream, which must not be closed.
     *
     * @param instance
     * @param os
     * @throws Exception
     */
    public void exportContent(PanelInstance instance, OutputStream os) throws Exception;

    /**
     * Read instance content from given InputStream, which must not be closed.
     *
     * @param instance
     * @param is
     * @throws Exception
     */
    public void importContent(PanelInstance instance, InputStream is) throws Exception;
}
