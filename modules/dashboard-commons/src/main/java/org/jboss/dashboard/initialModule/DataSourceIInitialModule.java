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
package org.jboss.dashboard.initialModule;

import org.jboss.dashboard.Application;
import org.jboss.dashboard.CoreServices;
import org.jboss.dashboard.database.DataSourceEntry;
import org.jboss.dashboard.database.DataSourceManager;
import org.jboss.dashboard.export.DataSourceImportManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * DataSource initial module class. It makes possible to install datasources at start-up.
 */
public class DataSourceIInitialModule extends InitialModule {

    /** Logger */
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataSourceIInitialModule.class.getName());
    
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

    protected boolean install() {
        return installOrUpgrade(false);
    }

    protected boolean upgrade(long currentVersion) {
        long newVersion = getVersion();
        if (newVersion <= currentVersion) return false;

        return installOrUpgrade(true);
    }

    protected boolean installOrUpgrade(boolean upgrade) {
        try {
            if (!check()) return false;

            // Get the XML file.
            log.info("Parsing datasource XML file: " + importFile);
            Application cm = Application.lookup();
            File pf = new File(cm.getBaseAppDirectory() + File.separator + importFile);
            if (!pf.exists()) {
                log.error("Cannot find file " + importFile + " for datasource initial module.");
                return false;
            }
            DataSourceImportManager dataSourceImportManager = CoreServices.lookup().getDataSourceImportManager();
            DataSourceEntry entry = dataSourceImportManager.doImport(new BufferedInputStream(new FileInputStream(pf)));

            DataSourceManager dataSourceManager = CoreServices.lookup().getDataSourceManager();
            
            // CHeck if datasource exist, then override it.
            DataSourceEntry existingEntry = dataSourceManager.getDataSourceEntry(entry.getName()); 
            if ( entry != null && existingEntry != null) {
                log.info("Datasource with name " + entry.getName() + " already exists. Overriding it.");
                existingEntry.delete();
            }

            // Save the new datasource entry.
            if (entry != null) entry.save();
            
            return true;
        } catch (Exception e) {
            log.error("Error importing datasource file (" + importFile + ") from initial module.", e);
            return false;
        }
    }

    /**
     * Check if file exists.
     */
    protected boolean check() {
        boolean correct = true;
        if (importFile == null) {
            log.error("Error. Import file not defined for initial module.");
            correct = false;
        }
        return correct;
    }
}
