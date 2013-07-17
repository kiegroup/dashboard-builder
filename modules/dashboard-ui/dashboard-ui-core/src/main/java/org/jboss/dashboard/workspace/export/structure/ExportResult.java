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
package org.jboss.dashboard.workspace.export.structure;

import org.jboss.dashboard.commons.xml.XMLNode;
import org.jboss.dashboard.workspace.export.ExportManager;

import java.io.IOException;
import java.io.Writer;

/**
 * Implementation of Workspaces export result.
 */
public class ExportResult extends ImportExportResult {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ExportResult.class.getName());

    private XMLNode node;
    private ExportManager exportManager;

    /**
     * Construct a result from a Node.
     */
    public ExportResult(XMLNode node, ExportManager exportManager) {
        super();
        this.exportManager = exportManager;
        setWarnings(node.getWarnings());
        setWarningArguments(node.getWarningArguments());
        this.node = node;
    }

    public ExportResult(Exception exception, ExportManager exportManager) {
        super(exception);
        this.exportManager = exportManager;
    }

    public boolean hasErrors() {
        return node == null || super.hasErrors();
    }

    public void writeXMLversion(Writer writer, boolean blanks) throws IOException {
        writer.write(exportManager.getXmlHeader());
        node.writeXML(writer, blanks);
    }

}
