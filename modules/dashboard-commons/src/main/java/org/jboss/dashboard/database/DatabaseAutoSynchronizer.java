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
package org.jboss.dashboard.database;

import org.jboss.dashboard.Application;
import org.jboss.dashboard.database.hibernate.HibernateInitializer;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.*;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Vector;

/**
 * This component manages the database creation.
 */
@ApplicationScoped
public class DatabaseAutoSynchronizer {

    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(DatabaseAutoSynchronizer.class.getName());

    @Inject @Config("oracle=create-oracle.sql," +
                    "mysql=create-mysql.sql," +
                    "postgres=create-postgres.sql," +
                    "sqlserver=create-sqlserver.sql," +
                    "h2=create-h2.sql")
    private Properties createFiles = new Properties();

    @Inject @Config("select * from dashb_installed_module")
    private String testTableExistsSql;

    @Inject @Config("oracle, mysql, postgres, sqlserver, h2, hsql, oracle10g")
    private String[] supportedDatabases;

    public static final String CUSTOM_DELIMITER = "-- CUSTOM_DELIMITER";
    public static final String CUSTOM_DELIMITER_ENABLER = "-- ENABLE_CUSTOM_DELIMITER";
    public static final String DATABASE_CONFIG_DIR = "sql";

    @Inject @Config("delimiter //, //, , delimiter ;, GO")
    private String[] excludedScriptStatements;

    public String getTestTableExistsSql() {
        return testTableExistsSql;
    }

    public void setTestTableExistsSql(String testTableExistsSql) {
        this.testTableExistsSql = testTableExistsSql;
    }

    public Properties getCreateFiles() {
        return createFiles;
    }

    public void setCreateFiles(Properties createFiles) {
        this.createFiles = createFiles;
    }

    public String[] getSupportedDatabases() {
        return supportedDatabases;
    }

    public void setSupportedDatabases(String[] supportedDatabases) {
        this.supportedDatabases = supportedDatabases;
    }

    public String[] getExcludedScriptStatements() {
        return excludedScriptStatements;
    }

    public void setExcludedScriptStatements(String[] excludedScriptStatements) {
        this.excludedScriptStatements = excludedScriptStatements;
    }

    public void synchronize(HibernateInitializer hibernateInitializer) throws Exception {
        String databaseName = hibernateInitializer.getDatabaseName();
        if (isCurrentDatabaseSupported(databaseName)) {
            boolean tableExists = existsModulesTable(databaseName);
            if (!tableExists) {
                createProductDatabase(databaseName);
            }
        }
    }

    protected void createProductDatabase(String databaseName) throws Exception {
        String sqlFilePath = createDatabaseScriptPath(databaseName);
        runScript(sqlFilePath);
    }

    protected String createDatabaseScriptPath(String databaseName) {
        String sqlBasePath =  Application.lookup().getBaseCfgDirectory() + "/" + DATABASE_CONFIG_DIR;
        databaseName = databaseName.startsWith("oracle") ? "oracle" : databaseName;
        return sqlBasePath + "/" + createFiles.get(databaseName);
    }

    protected void runScript(String filePath) throws Exception {
        File f = new File(filePath);
        if (f.exists() && f.isFile()) {
            log.warn("Creating database. Running file " + filePath);
            BufferedReader reader = new BufferedReader(new FileReader(f));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            runDDL(sb.toString());
        }
    }

    private String[] splitString(String str, String delims) {
        if (str == null)
            return null;
        else if (str.equals("") || delims == null || delims.length() == 0)
            return new String[]{str};
        String[] s;
        Vector v = new Vector();
        int pos = 0;
        int newpos = str.indexOf(delims, pos);
        while (newpos != -1) {
            v.addElement(str.substring(pos, newpos));
            pos = newpos + delims.length();
            newpos = str.indexOf(delims, pos);
        }
        v.addElement(str.substring(pos));
        s = new String[v.size()];
        for (int i = 0, cnt = s.length; i < cnt; i++)
            s[i] = ((String) v.elementAt(i)).trim();
        return s;
    }

    protected void runDDL(final String ddl) throws Exception {
        String separator = ";";
        if (ddl.startsWith(CUSTOM_DELIMITER_ENABLER))
            separator = CUSTOM_DELIMITER;
        String[] statements = splitString(ddl, separator);
        for (int i = 0; i < statements.length; i++) {
            final String ddlStatement = removeComments(statements[i]);
            if (ArrayUtils.contains(getExcludedScriptStatements(), ddlStatement))
                continue;
            if (log.isDebugEnabled())
                log.debug("Running statement: " + ddlStatement);
            new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    Work w = new Work() {
                        public void execute(Connection connection) throws SQLException {
                            Statement statement = null;
                            try {
                                statement = connection.createStatement();
                                statement.execute(ddlStatement);
                            } catch (SQLException e) {
                                log.error("Error executing " + ddlStatement + ": ", e);
                                throw e;
                            } finally {
                                if (statement != null)
                                    statement.close();
                            }
                        }
                    };
                    session.doWork(w);
                    session.flush();
                }
            }.execute();
        }
    }

    protected String removeComments(String ddlStatement) {
        StringBuffer sb = new StringBuffer();
        BufferedReader strreader = new BufferedReader(new StringReader(ddlStatement));
        String line = null;
        try {
            while ((line = strreader.readLine()) != null) {
                if (line.trim().startsWith("--"))
                    continue;
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            log.error("Error: ", e);
        }
        return sb.toString().trim();
    }

    protected boolean existsModulesTable(final String databaseName) throws Exception {
        final boolean[] returnValue = new boolean[]{false};
        new HibernateTxFragment(true) {
            protected void txFragment(Session session) throws Exception {
                Work w = new Work() {
                    public void execute(Connection connection) throws SQLException {
                        Statement statement = null;
                        try {
                            statement = connection.createStatement();
                            statement.execute(getTestTableExistsSql());
                            returnValue[0] = true;
                            log.debug("Execution of getTestTableExistsSql() query didn't throw any error. Database might be installed.");
                        } catch (SQLException e) {
                            markAsRollbackOnly();
                        } catch (UndeclaredThrowableException e) {
                            //SQL server 2008 thows other exception.
                            if (e.getCause() != null && e.getCause().getCause() instanceof SQLException) {
                                markAsRollbackOnly();
                            }else{
                                throw e;
                            }
                        } finally {
                            if (statement != null)
                                statement.close();
                        }
                    }
                };
                session.doWork(w);
            }
        }.execute();
        return returnValue[0];
    }

    public boolean isCurrentDatabaseSupported(String databaseName) {
        for (int i = 0; supportedDatabases != null && i < supportedDatabases.length; i++) {
            if (databaseName.equals(supportedDatabases[i])) return true;
        }
        return false;
    }

}
