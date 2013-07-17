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
package org.jboss.dashboard.ui.panel;

import org.jboss.dashboard.Application;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.factory.BasicFactoryElement;
import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.PanelSession;
import org.jboss.dashboard.ui.panel.help.PanelHelp;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Panel provider for a given type of panels. This class handles all the
 * common stuff for all the providers, and delegates all tasks which are dependant
 * on the panel's type to the attached panel driver.
 */
public class PanelProvider extends BasicFactoryElement {

    /**
     * Logger
     */
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PanelProvider.class.getName());

    /**
     * Attributes
     */
    private String id = null;

    /**
     * Description
     */
    private String description = null;

    /**
     * Provider group classification
     */
    private String group = null;

    /**
     * Stylesheet
     */
    private String styleSheet = null;

    /**
     * Properties
     */
    private Properties properties = new Properties();

    /**
     * Resource Bundles
     */
    private List bundles = new ArrayList();

    /**
     * Mapping from pages names to JSPs names
     */
    private Map jsps = new HashMap();

    /**
     * Driver class that will handle requests to this panel
     */
    private PanelDriver driver = null;

    /**
     * Flag to indicate if provider is enabled, or license makes it disabled.
     */
    private boolean enabled = true;

    /**
     * Flag to indicate if provider is one of the base panels
     */
    private boolean isBasePanel = false;

    /**
     * Flag to indicate if provider is one of the deprecated panels
     */
    private boolean isDeprecatedPanel = false;

    /**
     * thumbnail image
     */
    private String thumbnail = "";

    /**
     * Panel help object.
     */
    private PanelHelp panelHelp;

    private String[] defaultBundleFiles = new String[]{};

    private String panelStatusDir = "WEB-INF/data";
    private String panelsDir = "WEB-INF/data/panels";
    private String panelsUrlMapping = "/WEB-INF/data/panels";
    private String invalidDriverPage = "/panels/panelNotFound.jsp";

    public PanelProvider() {
    }

    public PanelProvider(PanelDriver newDriver) {
        this.driver = newDriver;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStyleSheet() {
        return styleSheet;
    }

    public void setStyleSheet(String styleSheet) {
        this.styleSheet = styleSheet;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public PanelDriver getDriver() {
        return driver;
    }

    public String getInvalidDriverPage() {
        return invalidDriverPage;
    }

    public void setInvalidDriverPage(String invalidDriverPage) {
        this.invalidDriverPage = invalidDriverPage;
    }

    public void setDriver(PanelDriver driver) {
        this.driver = driver;
    }

    public void addPage(String id, String jsp) {
        jsps.put(id, jsp);
    }

    public String getPage(String id) {
        return (String) jsps.get(id);
    }

    public Properties getProperties() {
        return properties;
    }

    public List getBundles() {
        return bundles;
    }


    public String getPanelStatusDir() {
        return panelStatusDir;
    }

    public void setPanelStatusDir(String panelStatusDir) {
        this.panelStatusDir = panelStatusDir;
    }

    public String getPanelsDir() {
        return panelsDir;
    }

    public void setPanelsDir(String panelsDir) {
        this.panelsDir = panelsDir;
    }

    public String getPanelsUrlMapping() {
        return panelsUrlMapping;
    }

    public void setPanelsUrlMapping(String panelsUrlMapping) {
        this.panelsUrlMapping = panelsUrlMapping;
    }

    public String[] getDefaultBundleFiles() {
        return defaultBundleFiles;
    }

    public void setDefaultBundleFiles(String[] defaultBundleFiles) {
        this.defaultBundleFiles = defaultBundleFiles;
    }

    public PanelHelp getPanelHelp() {
        return panelHelp;
    }

    public void setPanelHelp(PanelHelp panelHelp) {
        this.panelHelp = panelHelp;
    }

    public boolean isBasePanel() {
        return isBasePanel;
    }

    public void setBasePanel(boolean basePanel) {
        isBasePanel = basePanel;
    }

    public boolean isDeprecatedPanel() {
        return isDeprecatedPanel;
    }

    public void setDeprecatedPanel(boolean deprecatedPanel) {
        isDeprecatedPanel = deprecatedPanel;
    }

    public String getResource(String key) {
        return getResource(key, null);
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    private Map resourcesCache = new HashMap();

    public String getResource(String key, Locale l) {
        Locale locale = l != null ? l : LocaleManager.currentLocale();
        Map localeMap = (Map) resourcesCache.get(locale);
        if (localeMap == null) resourcesCache.put(locale, localeMap = new HashMap());
        String result = (String) localeMap.get(key);
        if (result != null) return result;

        String value = null;
        boolean resourceFound = false;
        for (int iBundle = 0; iBundle < bundles.size() && !resourceFound; iBundle++) {
            File bundleFile = (File) bundles.get(iBundle);
            try {
                URLClassLoader loader = new URLClassLoader(new URL[]{bundleFile.getParentFile().toURL()});
                ResourceBundle bundle = null;
                String fileName = bundleFile.getName().substring(0, bundleFile.getName().indexOf(".properties"));
                try {
                    bundle = ResourceBundle.getBundle(fileName, locale, loader);
                } catch (MissingResourceException e) {
                    log.error("Error trying to get the panel driver resource bundle: " + fileName, e);
                    continue;
                }

                value = bundle.getString(key);
                if (value != null) resourceFound = true;

            } catch (MalformedURLException e) {
                log.error("Error trying to get the panel driver resource bundle: " + bundleFile.getAbsolutePath(), e);
            } catch (MissingResourceException e) {
                // Do nothing. Its possible that the key does not exists in a bundle
            }
        }

        if (value == null) {
            value = properties.getProperty("resource." + key);
        }

        value = value != null ? value : key;
        localeMap.put(key, value);
        return value;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setBundles(List bundles) {
        this.bundles = bundles;
    }

    public final boolean isEnabled() {
        return enabled;
    }

    /**
     * Called on provider initialization
     */
    public final void initialize() throws Exception {
        getDriver().init(this);
    }

    /**
     * Called on panel initialization
     */
    public void initPanel(PanelInstance instance) throws Exception {
        getDriver().initPanel(instance);
    }

    /**
     * Called on panel status initialization
     */
    public void initSession(PanelSession status, HttpSession session) {
        if (log.isDebugEnabled()) log.debug("Initializing panel status for provider " + getId() + " " + getDriver().getClass().getName());
        getDriver().initPanelSession(status, session);
    }

    public void start() throws Exception {
        super.start();
        for (int i = 0; i < defaultBundleFiles.length; i++) {
            String defaultBundleFile = defaultBundleFiles[i];
            File bundleFile = new File(Application.lookup().getBaseAppDirectory() + defaultBundleFile);
            bundles.add(bundleFile);
        }
    }

    public static PanelProvider getInvalidPanelProvider(String id) throws Exception {
        PanelProvider p = (PanelProvider) Factory.lookup("org.jboss.dashboard.ui.panel.PanelProvider");
        //Add invalid driver page
        p.addPage(PanelDriver.PAGE_MANAGE_INVALID_DRIVER, p.getInvalidDriverPage());
        p.setId(id);
        p.setDescription("?");
        p.setGroup("?");
        PanelDriver driver = new PanelDriver() {
            public void initPanelSession(PanelSession panelSession, HttpSession session) {
                panelSession.setCurrentPageId(PAGE_MANAGE_INVALID_DRIVER);
            }
        };
        driver.initSystemParameters(p);
        p.setDriver(driver);
        p.enabled = true;
        return p;
    }
}
