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
package org.jboss.dashboard.i18n;

import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.LocaleManager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

/**
 * Base class that defines that provides some conversion services between classic resource bundles and dashbuilder's
 * i18n resources such as: KPIs or Workspace export files.
 */
public abstract class XmlToBundleConverter {

    public File xmlFile = null;
    public File bundleDir = null;
    public String bundleName = "messages";

    public abstract Map<Locale,Properties> extract() throws Exception;
    public abstract void inject(Map<Locale,Properties> bundles) throws Exception;

    public Properties getBundle(Map<Locale,Properties> bundles, Locale l) {
        Properties bundle = bundles.get(l);
        if (bundle == null) bundles.put(l, bundle = new Properties());
        return bundle;
    }

    public Map<Locale,Properties> read() throws Exception {
        Map<Locale,Properties> result = new HashMap<Locale, Properties>();
        Locale[] locales = LocaleManager.lookup().getPlatformAvailableLocales();
        for (Locale locale : locales) {
            File inputFile = new File(bundleDir, bundleName + "_" + locale.toString() + ".properties");
            if (inputFile.exists()) {
                Properties bundle = new Properties();
                bundle.load(new FileReader(inputFile));
                result.put(locale, bundle);
            }
        }
        return result;
    }

    public void write(Map<Locale,Properties> bundles) throws Exception {
        if (bundleDir == null || !bundleDir.isDirectory()) {
            throw new IllegalArgumentException("It's not a directory: " + bundleDir);
        }
        for (Locale locale : bundles.keySet()) {
            Properties bundle = bundles.get(locale);
            File outputFile = new File(bundleDir, bundleName + "_" + locale.toString() + ".properties");
            bundle.store(new FileWriter(outputFile), null);
        }
    }
}
