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
package org.jboss.dashboard.kpi;

import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.Application;
import org.jboss.dashboard.initialModule.InitialModule;
import org.jboss.dashboard.export.ImportManager;
import org.jboss.dashboard.export.ImportResults;
import org.jboss.dashboard.commons.message.Message;
import org.jboss.dashboard.commons.message.MessageList;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;

/**
 * KPI initial module class. It makes possible to install KPIs and data providers at start-up.
 */
public class KPIInitialModule extends InitialModule {

    /** Logger */
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(KPIInitialModule.class.getName());

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
        return _install();
    }

    protected boolean upgrade(long currentVersion) {
        long newVersion = getVersion();
        if (newVersion <= currentVersion) return false;

        return _install();
    }

    protected boolean _install() {
        try {
            if (!check()) return false;

            // Get the XML file.
            log.info("Parsing KPI's XML file: " + importFile);
            Application cm = Application.lookup();
            File pf = new File(cm.getBaseAppDirectory() + File.separator + importFile);
            if (!pf.exists()) {
                log.error("Cannot find file " + importFile + " for KPI initial module.");
                return false;
            }

            // Parse the file.
            ImportManager importMgr = DataDisplayerServices.lookup().getImportManager();
            ImportResults importResults = importMgr.parse(new FileInputStream(pf));

            // Save the imported results.
            importMgr.save(importResults);

            // Show import messages.
            MessageList messages = importResults.getMessages();
            Locale locale = LocaleManager.currentLocale();
            for (Message message : messages) {
                switch (message.getMessageType()) {
                    case Message.ERROR: log.error(message.getMessage(locale)); break;
                    case Message.WARNING: log.warn(message.getMessage(locale)); break;
                    case Message.INFO: log.info(message.getMessage(locale)); break;
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Error importing KPIs file (" + importFile + ") from initial module.", e);
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
