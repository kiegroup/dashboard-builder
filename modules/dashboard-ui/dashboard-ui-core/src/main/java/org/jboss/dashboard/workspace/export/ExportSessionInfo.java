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
package org.jboss.dashboard.workspace.export;

import org.jboss.dashboard.workspace.export.structure.CreateResult;
import org.jboss.dashboard.workspace.export.structure.ExportResult;
import org.jboss.dashboard.workspace.export.structure.ImportResult;

import java.io.Serializable;

/**
 * Helper class that stores the import or export process result.
 */
public class ExportSessionInfo implements Serializable {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ExportSessionInfo.class.getName());

    private ExportResult exportResult;
    private ImportResult[] importResult;
    private CreateResult[] createResult;

    public ExportResult getExportResult() {
        return exportResult;
    }

    public void setExportResult(ExportResult exportResult) {
        this.exportResult = exportResult;
    }

    public ImportResult[] getImportResult() {
        return importResult;
    }

    public void setImportResult(ImportResult[] importResult) {
        this.importResult = importResult;
    }

    public CreateResult[] getCreateResult() {
        return createResult;
    }

    public void setCreateResult(CreateResult[] createResult) {
        this.createResult = createResult;
    }
}
