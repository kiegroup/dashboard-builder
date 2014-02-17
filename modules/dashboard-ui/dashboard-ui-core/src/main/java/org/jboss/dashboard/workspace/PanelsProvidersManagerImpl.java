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
package org.jboss.dashboard.workspace;

import org.jboss.dashboard.Application;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.annotation.Priority;
import org.jboss.dashboard.annotation.Startable;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.commons.io.DirectoriesScanner;
import org.jboss.dashboard.ui.panel.PanelDriver;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.ui.panel.help.PanelHelp;
import org.jboss.dashboard.ui.panel.help.PanelHelpManager;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.xml.sax.SAXException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Files based implementation for the PanelsProvidersManager interface.
 * It reads the information about the installed providers by scanning all
 * the properties files with the extension .panel in a given directory.
 */
@ApplicationScoped
@Named("panelsProvidersManager")
public class PanelsProvidersManagerImpl implements PanelsProvidersManager, Startable {

    @Inject
    private transient Logger log;

    @Inject @Config("WEB-INF/etc/panels")
    private String panelDriversDir;

    @Inject @Config("/panels/helpPage.jsp")
    private String helpModePage;

    @Inject @Config("navigation.sections_horiz," +
                    "navigation.workspace_vert," +
                    "navigation.workspace," +
                    "navigation.sections_vert," +
                    "navigation.language_horiz," +
                    "navigation.sections," +
                    "navigation.workspace_horiz," +
                    "navigation.bread_crumb," +
                    "navigation.tree_menu," +
                    "navigation.logout," +
                    "dashboard.html_editor," +
                    "dashboard.data_provider_management," +
                    "dashboard.filter," +
                    "dashboard.kpi," +
                    "dashboard.export," +
                    "dashboard.import," +
                    "system.workspace_import_export," +
                    "system.data_source_manager")
    private String[] basePanelsIds;

    @Inject @Config("")
    private String[] deprecatedPanelsIds;

    @Inject @Config("panels/thumbnails/defaultProviderThumbnail.png")
    private String defaultProviderThumbnail;

    @Inject @Config("panels/groups/defaultProviderGroup.png")
    private String defaultProviderGroupThumbnail;

    @Inject @Config("panel_group_navigation=panels/groups/navigation.png," +
                    "panel_group_contents=panels/groups/contents.png")
    private Map<String,String> providerGroupImg = new HashMap<String,String>();

    @Inject
    private PanelHelpManager panelHelpManager;

    private Map<String,PanelProvider> panels = new HashMap<String,PanelProvider>();

    public Priority getPriority() {
        return Priority.HIGH;
    }

    public void start() throws Exception {
        log.debug("Start");

        if (getPanelDriversDir() == null) {
            throw new Exception("Parameter 'panelsDriversDir' not set");
        }
        String dir = Application.lookup().getBaseAppDirectory() + "/" + getPanelDriversDir();

        File fdir = new File(dir);
        log.info("Loading panels. Scanning dir: " + fdir.getCanonicalPath());

        DirectoriesScanner scanner = new DirectoriesScanner("panel");
        File[] files = scanner.findFiles(fdir);

        for (int i = 0; i < files.length; i++) {
            File f = files[i];

            log.info("Reading file: " + f);

            PanelProvider p = null;
            try {
                p = loadPanelProvider(f);
                p.initialize();
            } catch (Exception e) {
                log.error("Error loading panel from file " + f, e);
            }
            if (p != null) {
                panels.put(p.getId(), p);
            } else {
                log.error("File format is not valid: " + f);
            }
        }

        DirectoriesScanner scannerHelp = new DirectoriesScanner("phelp");
        File[] helpFiles = scannerHelp.findFiles(fdir);
        for (int i = 0; i < helpFiles.length; i++) {
            File helpFile = helpFiles[i];
            PanelHelp pHelp = null;
            try {
                pHelp = loadHelpFile(helpFile);
            } catch (IOException e) {
                log.error("Error loading help file:", e);
            } catch (SAXException e) {
                log.error("Error loading help file:", e);
            }
            if (pHelp != null) {
                String[] ids = pHelp.getIds();
                for (int j = 0; j < ids.length; j++) {
                    String id = ids[j];
                    PanelProvider provider = getProvider(id);
                    if (provider != null) {
                        provider.setPanelHelp(pHelp);
                    } else {
                        log.error("Invalid panel provider id " + id + " in help file: " + helpFile);
                    }
                }
            } else {
                log.error("Invalid help file: " + helpFile);
            }
        }
    }

    public String[] getDeprecatedPanelsIds() {
        return deprecatedPanelsIds;
    }

    public void setDeprecatedPanelsIds(String[] deprecatedPanelsIds) {
        this.deprecatedPanelsIds = deprecatedPanelsIds;
    }

    public String[] getBasePanelsIds() {
        return basePanelsIds;
    }

    public void setBasePanelsIds(String[] basePanelsIds) {
        this.basePanelsIds = basePanelsIds;
    }

    public String getPanelDriversDir() {
        return panelDriversDir;
    }

    public void setPanelDriversDir(String panelDriversDir) {
        this.panelDriversDir = panelDriversDir;
    }

    public String getHelpModePage() {
        return helpModePage;
    }

    public void setHelpModePage(String helpModePage) {
        this.helpModePage = helpModePage;
    }

    public String getDefaultProviderThumbnail() {
        return defaultProviderThumbnail;
    }

    public void setDefaultProviderThumbnail(String defaultProviderThumbnail) {
        this.defaultProviderThumbnail = defaultProviderThumbnail;
    }

    public String getDefaultProviderGroupThumbnail() {
        return defaultProviderGroupThumbnail;
    }

    public void setDefaultProviderGroupThumbnail(String defaultProviderGroupThumbnail) {
        this.defaultProviderGroupThumbnail = defaultProviderGroupThumbnail;
    }

    public Map getProviderGroupImg() {
        if (providerGroupImg == null)
            providerGroupImg = new HashMap();
        return providerGroupImg;
    }

    public void setProviderGroupImg(Map providerGroupImg) {
        this.providerGroupImg = providerGroupImg;
    }

    /**
     * Returns all providers, sorted by description
     *
     * @return
     */
    public PanelProvider[] getProviders() {
        return getProviders(null);
    }

    /**
     * Returns all panels providers installed in the system allowed for given workspace
     *
     * @param workspace Workspace that allows returned providers.
     */
    public PanelProvider[] getProviders(Workspace workspace) {
        HashSet panelProviders = new HashSet();
        for (Iterator it = panels.values().iterator(); it.hasNext();) {
            PanelProvider p = (PanelProvider) it.next();
            if (!p.isEnabled()) continue;
            if (workspace != null && !workspace.isProviderAllowed(p.getId()) && !workspace.isProviderAllowed("*"))
                continue;
            panelProviders.add(p);
        }
        PanelProvider[] p = (PanelProvider[]) panelProviders.toArray(new PanelProvider[panels.size()]);
        // Sort providers by description (just for display purpouses)
        Arrays.sort(p, new PanelProviderComparator());
        return p;
    }

    /**
     * Returns all providers groups
     *
     * @return
     */
    public String[] enumerateProvidersGroups() {
        return enumerateProvidersGroups(null);
    }

    /**
     * Enumerates all existing groups of providers containing panel Instances allowed by given workspace
     *
     * @param workspace Workspace that allows returned providers.
     * @return
     */
    public String[] enumerateProvidersGroups(Workspace workspace) {
        Set groups = new HashSet();
        for (Iterator iterator = panels.values().iterator(); iterator.hasNext();) {
            PanelProvider panelProvider = (PanelProvider) iterator.next();
            if (workspace != null && !workspace.isProviderAllowed(panelProvider.getId()) && !workspace.isProviderAllowed("*"))
                continue;
            if (panelProvider.isEnabled() && panelProvider.getGroup() != null) {
                groups.add(panelProvider.getGroup());
            }
        }
        return (String[]) groups.toArray(new String[groups.size()]);
    }

    /**
     * Returns group display name
     *
     * @return
     */
    public String getGroupDisplayName(String groupId) {
        return getGroupDisplayName(groupId, null);
    }

    /**
     * Returns group display name
     *
     * @return
     */
    public String getGroupDisplayName(String groupId, Locale locale) {
        for (Iterator iterator = panels.values().iterator(); iterator.hasNext();) {
            PanelProvider panelProvider = (PanelProvider) iterator.next();
            if (panelProvider.getGroup().equals(groupId)) {
                String resourceStr = panelProvider.getResource(groupId, locale);
                if (!resourceStr.equals(groupId)) {
                    return resourceStr;
                }
            }
        }
        return groupId;
    }

    /**
     * Returns all providers belonging to a given group, sorted by description
     *
     * @param group or null if we want all panels NOT belonging to any group
     * @return
     */
    public PanelProvider[] getProvidersInGroup(String group) {
        return getProvidersInGroup(group, null);
    }

    /**
     * Returns all providers belonging to a given group, sorted by description,
     * and allowed by given workspace
     *
     * @param group  or null if we want all panels NOT belonging to any group
     * @param workspace Workspace that allows returned providers.
     * @return
     */
    public PanelProvider[] getProvidersInGroup(String group, Workspace workspace) {
        List groupPanels = new ArrayList();
        for (Iterator iterator = panels.values().iterator(); iterator.hasNext();) {
            PanelProvider panelProvider = (PanelProvider) iterator.next();
            if (!panelProvider.isEnabled()) continue;
            if (workspace != null && !workspace.isProviderAllowed(panelProvider.getId()) && !workspace.isProviderAllowed("*"))
                continue;
            if ((group == null && panelProvider.getGroup() == null) ||
                    (group != null && group.equals(panelProvider.getGroup()))) {
                groupPanels.add(panelProvider);
            }
        }
        PanelProvider[] p = (PanelProvider[]) groupPanels.toArray(new PanelProvider[groupPanels.size()]);
        Arrays.sort(p, new PanelProviderComparator());
        return p;
    }

    /**
     * @return The providers installed, but not licensed for use in the workspace.
     */
    public PanelProvider[] getDisabledProviders() {
        HashSet panelProviders = new HashSet();
        for (Iterator it = panels.values().iterator(); it.hasNext();) {
            PanelProvider p = (PanelProvider) it.next();
            if (!p.isEnabled()) panelProviders.add(p);
        }
        PanelProvider[] p = (PanelProvider[]) panelProviders.toArray(new PanelProvider[panels.size()]);
        // Sort providers by description (just for display purpouses)
        Arrays.sort(p, new PanelProviderComparator());
        return p;
    }

    /**
     * Returns all providers belonging to a given group, sorted by description, but
     * disabled by license.
     *
     * @param group or null if we want all panels NOT belonging to any group
     * @return
     */
    public PanelProvider[] getDisabledProvidersInGroup(String group) {
        List groupPanels = new ArrayList();
        for (Iterator iterator = panels.values().iterator(); iterator.hasNext();) {
            PanelProvider panelProvider = (PanelProvider) iterator.next();
            if (panelProvider.isEnabled()) continue;
            if ((group == null && panelProvider.getGroup() == null) ||
                    (group != null && group.equals(panelProvider.getGroup()))) {
                groupPanels.add(panelProvider);
            }
        }
        PanelProvider[] p = (PanelProvider[]) groupPanels.toArray(new PanelProvider[groupPanels.size()]);
        Arrays.sort(p, new PanelProviderComparator());
        return p;
    }

    /**
     * Enumerates all existing groups of providers containing only disabled panel Instances
     *
     * @return all existing groups of providers containing only disabled panel Instances
     */
    public String[] enumerateDisabledProvidersGroups() {
        Set groups = new HashSet();
        for (Iterator iterator = panels.values().iterator(); iterator.hasNext();) {
            PanelProvider panelProvider = (PanelProvider) iterator.next();
            if (!panelProvider.isEnabled() && panelProvider.getGroup() != null) {
                groups.add(panelProvider.getGroup());
            }
        }
        return (String[]) groups.toArray(new String[groups.size()]);
    }

    public PanelProvider getProvider(String id) {
        return (PanelProvider) panels.get(id);
    }

    /**
     * Loads a panelHelp from a helpFile.
     *
     * @param helpFile
     * @return a help file information loaded from a file.
     */
    protected PanelHelp loadHelpFile(File helpFile) throws IOException, SAXException {
        return panelHelpManager.readPanelHelp(new FileInputStream(helpFile));
    }

    /**
     * Loads a provider from its file definition
     */
    protected PanelProvider loadPanelProvider(File f) throws Exception {
        Properties prop = new Properties();

        FileInputStream str = new FileInputStream(f);
        prop.load(str);
        str.close();

        // Create driver
        String driver = prop.getProperty("panel.driver");
        PanelProvider p = CDIBeanLocator.getBeanByType(PanelProvider.class);
        try {
            PanelDriver pdriver = (PanelDriver) CDIBeanLocator.getBeanByNameOrType(driver);
            pdriver = pdriver == null ? (PanelDriver) Class.forName(driver).newInstance() : pdriver;
            p.setDriver(pdriver);
        }
        catch (Throwable e) {
            log.error("Error creating instance for driver "+driver+" :", e);
        }
        // Set attributes
        p.setId(prop.getProperty("panel.id"));
        p.setBasePanel(ArrayUtils.contains(getBasePanelsIds(), prop.getProperty("panel.id")));
        p.setDeprecatedPanel(ArrayUtils.contains(getDeprecatedPanelsIds(), prop.getProperty("panel.id")));
        p.setDescription(prop.getProperty("panel.description"));
        p.setGroup(prop.getProperty("panel.group"));
        p.setStyleSheet(prop.getProperty("stylesheet"));
        if(prop.getProperty("panel.thumbnail")== null){
            p.setThumbnail(defaultProviderThumbnail);
        } else {
            p.setThumbnail(prop.getProperty("panel.thumbnail"));
        }

        // Iterate over properties
        Iterator it = prop.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            String value = prop.getProperty(key).trim();

            if (key.startsWith("jsp.")) {
                String text = key.substring(4).trim();
                p.addPage(text, value);
            } else if (key.startsWith("resources.")) {
                // Import resource bundles
                String bundleName = value;
                if (value.endsWith(".properties")) bundleName = value.substring(0, value.length() - 11);
                String[] locales = LocaleManager.lookup().getInstalledLocaleIds();
                for (int i = 0; i < locales.length; i++) {
                    String locale = locales[i];
                    File localeFile = new File(f.getParent() + "/" + bundleName + "_" + locale + ".properties");
                    if (localeFile.exists() && localeFile.isFile()) {
                        File bundleFile = new File(f.getParent() + "/" + bundleName + ".properties");
                        p.getBundles().add(bundleFile);
                    }
                }
            }
        }

        // Add the help page
        p.addPage(PanelDriver.PAGE_HELP_MODE, getHelpModePage());

        // Make the properties file available to the driver
        p.setProperties(prop);
        return p;

    }

    public class PanelProviderComparator implements Comparator {
        final Locale locale = LocaleManager.currentLocale();

        public int compare(Object o1, Object o2) {
            PanelProvider p1 = (PanelProvider) o1;
            PanelProvider p2 = (PanelProvider) o2;
            return p1.getResource(p1.getDescription(), locale).compareTo(p2.getResource(p2.getDescription(), locale));
        }
    }

    public String getProviderGroupImage(String groupId){
        try{
            String img=(String)providerGroupImg.get(groupId.replace(".","_"));
            if(img!=null)
                return img;
        }catch (Exception e){
        }
        return defaultProviderGroupThumbnail;
    }
}
