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

import org.hibernate.SessionFactory;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.jboss.dashboard.Application;
import org.jboss.dashboard.annotation.Priority;
import org.jboss.dashboard.annotation.Startable;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.database.DataSourceManager;
import org.jboss.dashboard.database.DatabaseAutoSynchronizer;
import org.jboss.dashboard.database.JNDIDataSourceEntry;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.persister.entity.AbstractEntityPersister;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
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

    public static final String DB_H2 = "h2";
    public static final String DB_POSTGRES = "postgres";
    public static final String DB_MYSQL = "mysql";
    public static final String DB_ORACLE = "oracle";
    public static final String DB_SQLSERVER = "sqlserver";

    @Inject
    protected HibernateSessionFactoryProvider hibernateSessionFactoryProvider;

    @Inject
    protected DatabaseAutoSynchronizer databaseAutoSynchronizer;

    @Inject
    protected DataSourceManager databaseSourceManager;

    @Inject @Config("true")
    protected boolean performNativeToHiloReplace;

    @Inject @Config("true")
    protected boolean enableDatabaseStructureVerification;

    @Inject @Config("true")
    protected boolean enableDatabaseAutoSynchronization;

    @Inject @Config(DB_H2 +        "=org.jboss.dashboard.database.H2Dialect," +
                    DB_POSTGRES +  "=org.hibernate.dialect.PostgreSQLDialect," +
                    DB_ORACLE +    "=org.hibernate.dialect.Oracle10gDialect," +
                    DB_MYSQL +     "=org.hibernate.dialect.MySQLDialect," +
                    DB_SQLSERVER + "=org.hibernate.dialect.SQLServerDialect")
    protected Map<String,String> supportedDialects;

    @Inject @Config("org.hibernate.dialect.MySQLDialect," +
                    "org.hibernate.dialect.SQLServerDialect")
    protected String[] nativeToHiloReplaceableDialects;

    @Inject @Config("org.jboss.dashboard.ui.resources.Envelope," +
                    "org.jboss.dashboard.ui.resources.Skin," +
                    "org.jboss.dashboard.ui.resources.Layout," +
                    "org.jboss.dashboard.ui.resources.ResourceGallery," +
                    "org.jboss.dashboard.ui.resources.GraphicElement")
    protected String[] databaseStructureVerificationExcludedClassNames;

    protected Configuration hbmConfig;
    protected String databaseName;
    protected DataSource localDataSource;

    public DatabaseAutoSynchronizer getDatabaseAutoSynchronizer() {
        return databaseAutoSynchronizer;
    }

    public boolean isEnableDatabaseStructureVerification() {
        return enableDatabaseStructureVerification;
    }

    public void setEnableDatabaseStructureVerification(boolean enableDatabaseStructureVerification) {
        this.enableDatabaseStructureVerification = enableDatabaseStructureVerification;
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

        // Configure Hibernate using the hibernate.cfg.xml.
        String hbnCfgPath = Application.lookup().getBaseCfgDirectory() + File.separator + "hibernate.cfg.xml";
        hbmConfig = new Configuration().configure(new File(hbnCfgPath));

        // Infer the underlying database name.
        databaseName = inferDatabaseName();
        log.info("The underlying database is: " + databaseName);

        // Set the proper dialect according to the database detected.
        String hbnDialect = supportedDialects.get(databaseName);
        hbmConfig.setProperty("hibernate.dialect", hbnDialect);

        // Fetch and pre-process the Hibernate mapping descriptors.
        loadHibernateDescriptors(hbmConfig);

        // Initialize the Hibernate session factory.
        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(hbmConfig.getProperties()).buildServiceRegistry();
        SessionFactory factory = hbmConfig.buildSessionFactory(serviceRegistry);
        hibernateSessionFactoryProvider.setSessionFactory(factory);

        // Synchronize the database schema.
        if (isEnableDatabaseAutoSynchronization() && getDatabaseAutoSynchronizer() != null) {
            getDatabaseAutoSynchronizer().synchronize(this);
        }

        // Verify the Hibernate mappings.
        if (enableDatabaseStructureVerification) {
            verifyHibernateConfig();
        }
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public boolean isOracleDatabase() {
        return isDatabase(DB_ORACLE);
    }

    public boolean isPostgresDatabase() {
        return isDatabase(DB_POSTGRES);
    }

    public boolean isSQLServerDatabase() {
        return isDatabase(DB_SQLSERVER);
    }

    public boolean isMySQLDatabase() {
        return isDatabase(DB_MYSQL);
    }

    public boolean isH2Database() {
        return isDatabase(DB_H2);
    }

    protected boolean isDatabase(String dbId) {
        return dbId.equals(databaseName);
    }

    public DataSource getLocalDataSource() throws Exception {
        if (localDataSource != null) return localDataSource;

        String jndiName = hbmConfig.getProperty("hibernate.connection.datasource");
        if (StringUtils.isBlank(jndiName)) throw new Exception("Property 'hibernate.connection.datasource' not specified.");

        // Obtain the application data source.
        JNDIDataSourceEntry jndiDS = new JNDIDataSourceEntry();
        jndiDS.setJndiPath(jndiName);
        return localDataSource = jndiDS.getDataSource();
    }

    protected String inferDatabaseName() throws Exception {
        String databaseName = inferDatabaseName(getLocalDataSource());
        if (StringUtils.isBlank(databaseName)) {
            throw new Exception ("The underlying database is unknown or the system is unable to recognize it.");
        } else {
            return databaseName;
        }
    }

    public String inferDatabaseName(DataSource ds) throws Exception {
        Connection connection = null;
        try {
            connection = ds.getConnection();
            String dbProductName = connection.getMetaData().getDatabaseProductName().toLowerCase();
            if (dbProductName.contains("h2")) return DB_H2;
            if (dbProductName.contains("postgre")) return DB_POSTGRES;
            if (dbProductName.contains("mysql")) return DB_MYSQL;
            if (dbProductName.contains("oracle")) return DB_ORACLE;
            if (dbProductName.contains("microsoft") || dbProductName.contains("sqlserver") || dbProductName.contains("sql server")) return DB_SQLSERVER;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return null;
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
                            log.error("The following stack trace may help you to determine the current error cause: ", e);
                        }
                    }}.execute();
                }
            }
        }}.execute();
    }

    protected void loadHibernateDescriptors(Configuration hbmConfig) throws IOException {
        Set<File> jars = Application.lookup().getJarFiles();
        for (File jar : jars) {
            ZipFile zf = new ZipFile(jar);
            for (Enumeration en = zf.entries(); en.hasMoreElements();) {
                ZipEntry entry = (ZipEntry) en.nextElement();
                String entryName = entry.getName();
                if (entryName.endsWith("hbm.xml") && !entry.isDirectory()) {
                    InputStream is = zf.getInputStream(entry);
                    String xml = readXMLForFile(entryName, is);
                    xml = processXMLContents(entryName, xml);
                    hbmConfig.addXML(xml);
                }
            }
        }
    }

    protected String readXMLForFile(String fileName, InputStream is) throws IOException {
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
            log.error("Error processing the Hibernate XML mapping descriptor: " + fileName, e);
            return null;
        }
    }

    protected String processXMLContents(String fileName, String fileContent) {
        if (isPerformNativeToHiloReplace()) {
            if (ArrayUtils.contains(getNativeToHiloReplaceableDialects(), hbmConfig.getProperty("hibernate.dialect"))) {
                String line = "class=\"hilo\"><param name=\"table\">hibernate_unique_key</param><param name=\"column\">next_hi</param><param name=\"max_lo\">0</param></generator>";
                fileContent = StringUtils.replace(fileContent, "class=\"native\"/>", line);
                fileContent = StringUtils.replace(fileContent, "class=\"native\" />", line);
            }
        }
        return fileContent;
    }

    /**
     * Evict all cache information.
     */
    public synchronized void evictAllCaches() {
        Cache cache = getSessionFactory().getCache();
        cache.evictQueryRegions();
        cache.evictEntityRegions();
        cache.evictCollectionRegions();
        cache.evictNaturalIdRegions();
    }
}
