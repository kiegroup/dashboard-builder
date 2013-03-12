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
package org.jboss.dashboard.database.hibernate;

import org.jboss.dashboard.Application;
import org.jboss.dashboard.annotation.Priority;
import org.jboss.dashboard.annotation.Startable;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.database.DatabaseAutoSynchronizer;
import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.commons.io.DirectoriesScanner;
import org.jboss.dashboard.database.cache.CacheConfigurationGenerator;
import org.jboss.dashboard.database.cache.CacheConfigurationManager;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.persister.entity.AbstractEntityPersister;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Class that initializes the Hibernate framework. It reads all the *.hbm.xml files and push them as part of the
 * Hibernate configuration. Furthermore, initializes a SessionFactory object that will be used further by transactions. 
 * To do so it reads first the configuration stored into the HibernateProperties factory bean.
 */
@ApplicationScoped
public class HibernateInitializer implements Startable {

    private static transient Log log = LogFactory.getLog(HibernateInitializer.class.getName());
    private static final String HIBERNATE_FILE_EXTENSION = "hbm.xml";

    @Inject @Config("true")
    protected boolean performNativeToHiloReplace;

    @Inject @Config("org.hibernate.dialect.MySQLDialect, org.hibernate.dialect.SQLServerDialect")
    protected String[] nativeToHiloReplaceableDialects;

    @Inject @Config("true")
    protected boolean enableDatabaseStructureVerification = true;

    @Inject @Config("true")
    protected boolean enableDatabaseAutoSynchronization = true;

    @Inject @Config("org.jboss.dashboard.ui.resources.Envelope," +
                    "org.jboss.dashboard.ui.resources.Skin," +
                    "org.jboss.dashboard.ui.resources.Layout," +
                    "org.jboss.dashboard.ui.resources.ResourceGallery," +
                    "org.jboss.dashboard.ui.resources.GraphicElement")
    protected String[] databaseStructureVerificationExcludedClassNames;

    @Inject
    protected HibernateSessionFactoryProvider hibernateSessionFactoryProvider;

    @Inject
    protected DatabaseAutoSynchronizer databaseAutoSynchronizer;

    protected Configuration hbmConfig;
    protected String databaseName;
    protected boolean c3p0Enabled = true;
    protected boolean cacheEnabled = true;
    protected List processedJars = new ArrayList();


    public Properties getHibernateProperties() {
        // TODO: migrate to CDI
        return (Properties) Factory.lookup("org.jboss.dashboard.database.HibernateProperties");
    }

    public CacheConfigurationManager getCacheConfigurationManager() {
        // TODO: migrate to CDI
        return (CacheConfigurationManager) Factory.lookup("org.jboss.dashboard.database.cache.CacheConfigurationManager");
    }

    public DatabaseAutoSynchronizer getDatabaseAutoSynchronizer() {
        return databaseAutoSynchronizer;
    }

    public boolean isEnableDatabaseStructureVerification() {
        return enableDatabaseStructureVerification;
    }

    public void setEnableDatabaseStructureVerification(boolean enableDatabaseStructureVerification) {
        this.enableDatabaseStructureVerification = enableDatabaseStructureVerification;
    }

    public Configuration getHbmConfig() {
        return hbmConfig;
    }

    public boolean isPerformNativeToHiloReplace() {
        return performNativeToHiloReplace;
    }

    public void setPerformNativeToHiloReplace(boolean performNativeToHiloReplace) {
        this.performNativeToHiloReplace = performNativeToHiloReplace;
    }

    public String[] getNativeToHiloReplaceableDialects() {
        return nativeToHiloReplaceableDialects;
    }

    public void setNativeToHiloReplaceableDialects(String[] nativeToHiloReplaceableDialects) {
        this.nativeToHiloReplaceableDialects = nativeToHiloReplaceableDialects;
    }

    public String[] getDatabaseStructureVerificationExcludedClassNames() {
        return databaseStructureVerificationExcludedClassNames;
    }

    public void setDatabaseStructureVerificationExcludedClassNames(String[] databaseStructureVerificationExcludedClassNames) {
        this.databaseStructureVerificationExcludedClassNames = databaseStructureVerificationExcludedClassNames;
    }

    public boolean isC3p0Enabled() {
        return c3p0Enabled;
    }

    public void setC3p0Enabled(boolean c3p0Enabled) {
        this.c3p0Enabled = c3p0Enabled;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public boolean isEnableDatabaseAutoSynchronization() {
        return enableDatabaseAutoSynchronization;
    }

    public void setEnableDatabaseAutoSynchronization(boolean enableDatabaseAutoSynchronization) {
        this.enableDatabaseAutoSynchronization = enableDatabaseAutoSynchronization;
    }

    public SessionFactory getSessionFactory() {
        return hibernateSessionFactoryProvider.getSessionFactory();
    }

    public Priority getPriority() {
        return Priority.URGENT;
    }

    public void start() throws Exception {
        // Get the caches configured before proceeding.
        getCacheConfigurationManager().initializeCaches();

        Properties properties = getHibernateProperties();
        processHibernateProperties(properties);
        System.getProperties().putAll(properties);
        hbmConfig = new Configuration();
        hbmConfig.setProperties(properties);

        String dialect = properties.getProperty("hibernate.dialect");
        String dialectName = dialect.substring(dialect.lastIndexOf('.') + 1);
        if (dialectName.endsWith("Dialect")) dialectName = dialectName.substring(0, dialectName.length() - 7);
        dialectName = dialectName.toLowerCase();
        if (dialectName.equals("postgresql")) databaseName = "postgres";
        else databaseName = dialectName;

        loadFiles(hbmConfig);
        hibernateSessionFactoryProvider.setSessionFactory(hbmConfig.buildSessionFactory());

        if (isEnableDatabaseAutoSynchronization() && getDatabaseAutoSynchronizer() != null) {
            getDatabaseAutoSynchronizer().synchronize(this);
        }

        if (enableDatabaseStructureVerification) {
            verifyHibernateConfig();
        }
    }

    protected void processHibernateProperties(Properties p) {
        if (!isCacheEnabled()) log.warn("\nHIBERNATE CACHE DISABLED");
        if (!isC3p0Enabled()) log.warn("\nHIBERNATE C3P0 DISABLED");
        Iterator it = p.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (!isC3p0Enabled() && key.startsWith("hibernate.c3p0")) it.remove();
            if (!isCacheEnabled() && key.startsWith("hibernate.cache")) it.remove();
        }
        // If Hibernate has been configured to use a data source then remove JDBC properties to avoid Hibernate error message.
        if (p.containsKey("hibernate.connection.datasource")) {
            p.remove("hibernate.connection.url");
            p.remove("hibernate.connection.username");
            p.remove("hibernate.connection.password");
        }
    }

    protected void verifyHibernateConfig() throws Exception {
        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            Map metadata = session.getSessionFactory().getAllClassMetadata();
            for (Iterator i = metadata.values().iterator(); i.hasNext();) {
                final AbstractEntityPersister persister = (AbstractEntityPersister) i.next();
                final String className = persister.getName();
                if (!ArrayUtils.contains(getDatabaseStructureVerificationExcludedClassNames(), className)) {
                    log.debug("Verifying: " + className);
                    new HibernateTxFragment(true) {
                    protected void txFragment(Session session) throws Exception {
                        try {
                            boolean usingOracle = isOracleDatabase();
                            Query query = session.createQuery("from " + className + " c " + (usingOracle ? " where rownum < 6" : ""));
                            if (!usingOracle) query.setMaxResults(5);
                            query.list();
                        }
                        catch (Exception e) {
                            log.error("Structure verification error for class " + className);
                            log.error("Error seems to affect table named " + persister.getTableName());
                            log.error("Please verify that all required database upgrades/creates are applied. Following stack trace may help you to determine the current error cause: ", e);
                        }
                    }}.execute();
                }
            }
        }}.execute();
    }

    protected void loadFiles(Configuration hbmConfig) throws IOException {
        List<String> modules = Application.lookup().getGlobalFactory().getModules();
        String libPath = Application.lookup().getBaseAppDirectory() + File.separator + "WEB-INF" + File.separator + "lib";
        File libDir = new File(libPath);
        File[] jars = new DirectoriesScanner("jar").findFiles(libDir);
        for (int i = 0; i < jars.length; i++) {
            File jar = jars[i];
            for (String moduleName : modules) {
                String jarName = jar.getName();
                if (!processedJars.contains(jarName) && jarName.startsWith(moduleName)) {
                    loadFiles(hbmConfig, jar);
                    processedJars.add(jarName);
                }
            }
        }
    }

    protected void loadFiles(Configuration hbmConfig, File jarFile) throws IOException {
        ZipFile zf = new ZipFile(jarFile);
        for (Enumeration en = zf.entries(); en.hasMoreElements();) {
            ZipEntry entry = (ZipEntry) en.nextElement();
            if (entry.getName().endsWith(HIBERNATE_FILE_EXTENSION) && !entry.isDirectory()) {
                InputStream is = zf.getInputStream(entry);
                String xml = readXMLForFile(is);
                xml = processXMLContents(entry.getName(), xml);
                hbmConfig.addXML(xml);
            }
        }
    }

    protected String readXMLForFile(InputStream is) throws IOException {
        try {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(is));
                StringBuffer fileContents = new StringBuffer();
                String lineRead;
                while ((lineRead = reader.readLine()) != null) {
                    fileContents.append(lineRead);
                }
                return fileContents.toString();
            } finally {
                if (reader != null) reader.close();
            }
        } catch (IOException e) {
            log.error("Error:", e);
            return null;
        }
    }

    protected String processXMLContents(String fileName, String fileContent) {
        if (isPerformNativeToHiloReplace()) {
            if (getHibernateProperties() != null &&
                    ArrayUtils.contains(getNativeToHiloReplaceableDialects(), getHibernateProperties().getProperty("hibernate.dialect"))) {
                /* s = StringUtils.replace(s, "class=\"native\"", "class=\"hilo\"");*/
                String line = "class=\"hilo\"><param name=\"table\">hibernate_unique_key</param><param name=\"column\">next_hi</param><param name=\"max_lo\">0</param></generator>";
                fileContent = StringUtils.replace(fileContent, "class=\"native\"/>", line);
                fileContent = StringUtils.replace(fileContent, "class=\"native\" />", line);
            }
        }

        CacheConfigurationGenerator configGenerator = getCacheConfigurationManager().getConfigGenerator();
        String cacheUsage = null;
        if (configGenerator != null) {
            cacheUsage = configGenerator.getCacheUsage();
        }
        if (cacheUsage != null) { //Replace the cache usage
            String oldCacheUsage = "<cache usage=\"transactional\"/>";
            String newCacheUsage = "<cache usage=\"" + cacheUsage + "\"/>";
            fileContent = StringUtils.replace(fileContent, oldCacheUsage, newCacheUsage);
        }
        return fileContent;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Evict all cache information.
     */
    public synchronized void evictAllCaches() {
        // Cache manager might support it
        if (!getCacheConfigurationManager().freeAllCache()) {
            SessionFactory sf = getSessionFactory();
            sf.evictQueries();
            Map metadata = getSessionFactory().getAllClassMetadata();
            for (Object o : metadata.values()) {
                final AbstractEntityPersister persister = (AbstractEntityPersister) o;
                final String className = persister.getName();
                sf.evictEntity(className);
            }
        }
    }

    /**
     * Create a custom cache region
     * @param regionName name for the region
     * @return true if it was created.
     */
    public synchronized boolean createCustomCacheRegion(String regionName) {
        return getCacheConfigurationManager().createCustomCacheRegion(regionName);
    }

    public boolean isOracleDatabase() {
        return isDatabase("Oracle");
    }

    public boolean isPostgresDatabase() {
        return isDatabase("Postgre");
    }

    public boolean isSQLServerDatabase() {
        return isDatabase("SQLServer");
    }

    public boolean isMySQLDatabase() {
        return isDatabase("MySQL");
    }

    public boolean isH2Database() {
        return isDatabase("H2");
    }

    protected boolean isDatabase(String dbId) {
        String dialect = getHibernateProperties().getProperty("hibernate.dialect");
        return dialect.indexOf(dbId) != -1 || dialect.indexOf(dbId.toLowerCase()) != -1;
    }
}
