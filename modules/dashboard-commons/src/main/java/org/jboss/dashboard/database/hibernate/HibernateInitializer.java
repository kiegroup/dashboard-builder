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
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.jboss.dashboard.Application;
import org.jboss.dashboard.annotation.Priority;
import org.jboss.dashboard.annotation.Startable;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.commons.misc.ReflectionUtils;
import org.jboss.dashboard.database.DataSourceManager;
import org.jboss.dashboard.database.DatabaseAutoSynchronizer;
import org.jboss.dashboard.database.JNDIDataSourceEntry;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static transient Logger log = LoggerFactory.getLogger(HibernateInitializer.class.getName());

    public static final String DB_H2 = "h2";
    public static final String DB_POSTGRES = "postgres";
    public static final String DB_MYSQL = "mysql";
    public static final String DB_ORACLE = "oracle";
    public static final String DB_SQLSERVER = "sqlserver";
    public static final String DB_DB2 = "db2";
    public static final String DB_TEIID = "teiid";
    public static final String DB_SYBASE = "sybase";

    @Inject
    protected HibernateSessionFactoryProvider hibernateSessionFactoryProvider;

    @Inject
    protected DatabaseAutoSynchronizer databaseAutoSynchronizer;

    @Inject
    protected DataSourceManager databaseSourceManager;

    @Inject @Config(DB_H2 +        "=org.hibernate.dialect.H2Dialect," +
                    DB_POSTGRES +  "=org.hibernate.dialect.PostgreSQLDialect," +
                    DB_ORACLE +    "=org.hibernate.dialect.Oracle10gDialect," +
                    DB_MYSQL +     "=org.hibernate.dialect.MySQLDialect," +
                    DB_SQLSERVER + "=org.hibernate.dialect.SQLServerDialect," +
                    DB_DB2 + "=org.hibernate.dialect.DB2Dialect," +
                    DB_TEIID + "=org.teiid.dialect.TeiidDialect," +
                    DB_SYBASE + "=org.hibernate.dialect.SybaseASE157Dialect")
    protected Map<String,String> supportedDialects;

    @Inject @Config("org.hibernate.dialect.H2Dialect," +
                    "org.hibernate.dialect.DB2Dialect," +
                    "org.teiid.dialect.TeiidDialect")
    protected String[] nativeToSequenceReplaceableDialects;

    @Inject @Config("org.hibernate.dialect.MySQLDialect," +
                    "org.hibernate.dialect.SQLServerDialect," +
                    "org.hibernate.dialect.SybaseASE157Dialect")
    protected String[] nativeToHiloReplaceableDialects;

    @Inject @Config("org.jboss.dashboard.ui.resources.Envelope," +
                    "org.jboss.dashboard.ui.resources.Skin," +
                    "org.jboss.dashboard.ui.resources.Layout," +
                    "org.jboss.dashboard.ui.resources.GraphicElement")
    protected String[] verificationExcludedClassNames;

    protected Configuration hbmConfig;
    protected String databaseName;
    protected DataSource localDataSource;

    @Inject  @Config
    protected String defaultSchema;

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
        ServiceRegistryBuilder serviceRegistryBuilder = new ServiceRegistryBuilder().applySettings(hbmConfig.getProperties());
        ServiceRegistry serviceRegistry = (ServiceRegistry) ReflectionUtils.invokeMethod(serviceRegistryBuilder, "buildServiceRegistry", null);
        if (serviceRegistry == null) serviceRegistry = (ServiceRegistry) ReflectionUtils.invokeMethod(serviceRegistryBuilder, "build", null);
        SessionFactory factory = hbmConfig.buildSessionFactory(serviceRegistry);
        hibernateSessionFactoryProvider.setSessionFactory(factory);

        // Set the default schema if specified via system property or via hibernate configuration.
        // NOTE: To set the default schema via hibernate configuration, the hibernate.cfg.xml file must be modified.
        //       This file is located inside the generated webapp. So, if the user wants to change the working schema,
        //       the fastest way is to add a System property when starting the application server to modify the
        //       defaultSchema bean property, instead of opening the war, modifying the file and reassembling it.
        if (StringUtils.isBlank(defaultSchema)) {
            String _defaultSchema = hbmConfig.getProperty(Environment.DEFAULT_SCHEMA);
            if (!StringUtils.isBlank(_defaultSchema)) defaultSchema = _defaultSchema;
        } else {
            hbmConfig.setProperty(Environment.DEFAULT_SCHEMA, defaultSchema);
        }

        // Synchronize the database schema.
        if (databaseAutoSynchronizer != null) {
            databaseAutoSynchronizer.synchronize(this);
        }

        // Verify the Hibernate mappings.
        verifyHibernateConfig();
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDefaultSchema(String defaultSchema) {
        this.defaultSchema = defaultSchema;
    }

    public String getDefaultSchema() {
        return defaultSchema;
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

    public boolean isDB2Database() {
        return isDatabase(DB_DB2);
    }

    public boolean isTeiidDatabase() {
        return isDatabase(DB_TEIID);
    }

    public boolean isSybaseDatabase() {
        return isDatabase(DB_SYBASE);
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
        if (ds == null) return null;
        Connection connection = null;
        String dbProductName = null;
        try {
            connection = ds.getConnection();
            dbProductName = connection.getMetaData().getDatabaseProductName().toLowerCase();
            if ("h2".contains(dbProductName)) return DB_H2;
            if ("postgre".contains(dbProductName) || dbProductName.contains("enterprisedb")) return DB_POSTGRES;
            if ("mysql".contains(dbProductName)) return DB_MYSQL;
            if ("oracle".contains(dbProductName)) return DB_ORACLE;
            if ("microsoft".contains(dbProductName) || dbProductName.contains("sqlserver") || dbProductName.contains("sql server")) return DB_SQLSERVER;
            if ("db2".contains(dbProductName)) return DB_DB2;
            if ("teiid".contains(dbProductName)) return DB_TEIID;
            if ("ase".contains(dbProductName)) return DB_SYBASE;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        log.error("The underlying database code [" + dbProductName + "] is not supported.");
        return null;
    }

    protected void verifyHibernateConfig() throws Exception {
        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            Map metadata = session.getSessionFactory().getAllClassMetadata();
            for (Iterator i = metadata.values().iterator(); i.hasNext();) {
                final AbstractEntityPersister persister = (AbstractEntityPersister) i.next();
                final String className = persister.getName();
                if (!ArrayUtils.contains(verificationExcludedClassNames, className)) {
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
                    xml = processXMLContents(xml);
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

    protected String processXMLContents(String fileContent) {
        if (ArrayUtils.contains(nativeToSequenceReplaceableDialects, hbmConfig.getProperty("hibernate.dialect"))) {
            String line = "class=\"sequence\" />";
            fileContent = StringUtils.replace(fileContent, "class=\"native\"/>", line);
            fileContent = StringUtils.replace(fileContent, "class=\"native\" />", line);
        }
        if (ArrayUtils.contains(nativeToHiloReplaceableDialects, hbmConfig.getProperty("hibernate.dialect"))) {
            String line = "class=\"hilo\"><param name=\"table\">hibernate_unique_key</param><param name=\"column\">next_hi</param><param name=\"max_lo\">0</param></generator>";
            fileContent = StringUtils.replace(fileContent, "class=\"native\"/>", line);
            fileContent = StringUtils.replace(fileContent, "class=\"native\" />", line);
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
    }
}
