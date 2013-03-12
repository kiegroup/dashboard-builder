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

import org.jboss.dashboard.Application;
import org.jboss.dashboard.factory.InitialModule;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.workspace.export.structure.ImportResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;

public class ImportWorkspacesModule extends InitialModule {

    private static transient Log log = LogFactory.getLog(ImportWorkspacesModule.class.getName());

    /**
     * Represents the path of the XML file to import. A relative path to the application directory.
     */
    protected String importFile;

    public String getImportFile() {
        return importFile;
    }

    public void setImportFile(String importFile) {
        this.importFile = importFile;
    }

    public boolean upgrade(long l) {
        return false;
    }

    public boolean install() {
        try {
            // Get the XML file.
            Application cm = Application.lookup();
            File pf = new File(cm.getBaseAppDirectory() + File.separator + importFile);
            if (!pf.exists()) {
                log.error("Cannot find file " + importFile + " for Workspace initial module.");
                return false;
            }

            // Load the file.
            ExportManager exportManager = UIServices.lookup().getExportManager();
            ImportResult result = exportManager.loadXML(pf.getName(), new FileInputStream(pf));
            if (result == null) {
                log.warn("Error on importation. Nothing to import");
                return false;
            }

            if (result.getException() != null) throw result.getException();
            if (result.getWarnings() != null && result.getWarnings().size() > 0) {
                for (int j = 0; j < result.getWarnings().size(); j++) {
                    log.warn("Problems importing entry " + result.getEntryName() + ": " + result.getWarnings().get(j));
                }
            }

            exportManager.create(new ImportResult[]{result}, null, true);
            return true;
        } catch (Exception e) {
            log.error("Error importing module " + getName() + " version " + getVersion(), e);
        }
        return false;
    }
}
