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
package org.jboss.dashboard;

import org.jboss.dashboard.annotation.StartableProcessor;
import org.jboss.dashboard.commons.io.DirectoriesScanner;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.text.MessageFormat;
import java.util.*;

/**
 * Class that defines some application set-up parameters.
 */
@ApplicationScoped
public class Application {

    public static Application lookup() {
        return CDIBeanLocator.getBeanByType(Application.class);
    }

    @Inject
    protected StartableProcessor startupProcessor;

    @Inject
    protected LocaleManager localeManager;

    protected String libDirectory = null;
    protected String baseCfgDirectory = null;
    protected String baseAppDirectory = null;
    protected transient Set<File> jarFiles = null;

    public String getBaseAppDirectory() {
        return baseAppDirectory;
    }

    public String getBaseCfgDirectory() {
        return baseCfgDirectory;
    }

    public void setBaseAppDirectory(String newBaseAppDirectory) {
        baseAppDirectory = newBaseAppDirectory;
    }

    public void setBaseCfgDirectory(String newBaseCfgDirectory) {
        baseCfgDirectory = newBaseCfgDirectory;
    }

    public String getLibDirectory() {
        return libDirectory;
    }

    public void setLibDirectory(String libDirectory) {
        this.libDirectory = libDirectory;
    }

    public void start() throws Exception {
        startupProcessor.wakeUpStartableBeans();
    }

    public Set<File> getJarFiles() {
        if (jarFiles != null) return jarFiles;

        jarFiles = new HashSet<File>();
        File libDir = new File(libDirectory);
        File[] jars = new DirectoriesScanner("jar").findFiles(libDir);
        for (int i = 0; i < jars.length; i++) {
            File jar = jars[i];
            String jarName = jar.getName();
            if (jarName.startsWith("dashboard-")) {
                jarFiles.add(jar);
            }
        }
        return jarFiles;
    }

    /**
     * Return a string containing the copyright.
     */
    public String getCopyright() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        String currentYear = Integer.toString(cal.get(Calendar.YEAR));
        ResourceBundle i18n = localeManager.getBundle("org.jboss.dashboard.messages", LocaleManager.currentLocale());
        return MessageFormat.format(i18n.getString("config.copyright"), currentYear);
    }
}
