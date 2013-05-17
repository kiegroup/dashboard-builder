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
import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.factory.FactoryWork;
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
        return (Application) CDIBeanLocator.getBeanByType(Application.class);
    }

    @Inject
    protected StartableProcessor startupProcessor;

    protected boolean upAndRunning = false;
    protected String libDirectory = null;
    protected String baseCfgDirectory = null;
    protected String baseAppDirectory = null;

    protected transient Set<File> jarFiles = null;
    protected transient Factory globalFactory = null;

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

    public Factory getGlobalFactory() {
        return globalFactory;
    }

    public void setGlobalFactory(Factory globalFactory) {
        if (this.globalFactory != null) {
            Factory.doWork(new FactoryWork() {
                public void doWork() {
                    Application.this.globalFactory.destroy();
                }
            });
        }
        this.globalFactory = globalFactory;
    }

    public boolean isUpAndRunning() {
        return upAndRunning;
    }

    public void setUpAndRunning(boolean upAndRunning) {
        this.upAndRunning = upAndRunning;
    }

    public void start() throws Exception {
        startupProcessor.wakeUpStartableBeans();
        setUpAndRunning(true);
    }

    public void shutdown() {
        setUpAndRunning(false);

        // Destroy the Factory configuration.
        setGlobalFactory(null);
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
        ResourceBundle i18n = ResourceBundle.getBundle("org.jboss.dashboard.messages", LocaleManager.currentLocale());
        return MessageFormat.format(i18n.getString("config.copyright"), currentYear);
    }
}
