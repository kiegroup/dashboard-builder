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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.dashboard.Application;
import org.jboss.dashboard.commons.io.DirectoriesScanner;
import org.jboss.dashboard.database.hibernate.HibernateInitializer;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.jboss.dashboard.error.ErrorManager;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * This component manages the database creation.
 */
@ApplicationScoped
public class DatabaseAutoSynchronizer {

    private static transient Logger log = LoggerFactory.getLogger(DatabaseAutoSynchronizer.class.getName());

    @Inject @Config("sql")
    protected String databaseConfigDir;

    @Inject @Config("dashb_installed_module")
    protected String installedModulesTable;

    @Inject @Config("delimiter //, //, , delimiter ;, GO")
    protected String[] excludedScriptStatements;

    @Inject @Config("-- CUSTOM_DELIMITER")
    protected String customDelimiter;

    @Inject @Config("-- ENABLE_CUSTOM_DELIMITER")
    protected String customDelimiterEnabler;

    public void synchronize(HibernateInitializer hibernateInitializer) throws Exception {
        String databaseName = hibernateInitializer.getDatabaseName();
        boolean tableExists = existsModulesTable();
        if (!tableExists) {
            createDatabase(databaseName);
        }
    }

    protected void createDatabase(String databaseName) throws Exception {
        // Search in the classpath for the SQL files for the given database.
        String sqlDir = Application.lookup().getBaseCfgDirectory() + File.separator + databaseConfigDir;
        Map<String, File> sqlFileMap = new HashMap<String, File>();
        DirectoriesScanner scanner = new DirectoriesScanner("sql");
        File[] sqlFiles = scanner.findFiles(new File(sqlDir));
        for (File sqlFile : sqlFiles) {
            // The file name must start with an ordinal which indicates the order of execution and finish with the databaseName.
            if (sqlFile.getName().endsWith(databaseName + ".sql") && Character.isDigit(sqlFile.getName().charAt(0))) {
                sqlFileMap.put(sqlFile.getName(), sqlFile);
            }
        }

        // Sort by name and run the SQL files encountered.
        List<String> sqlFileNames = new ArrayList<String>(sqlFileMap.keySet());
        Collections.sort(sqlFileNames);
        for (String sqlFileName : sqlFileNames) {
            File sqlFile = sqlFileMap.get(sqlFileName);
            runSQLFile(sqlFile);
        }
    }

    protected void runSQLFile(File f) throws Exception {
        if (f.exists() && f.isFile()) {
            log.warn("Running file " + f.getName());
            BufferedReader reader = new BufferedReader(new FileReader(f));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            runDDL(sb.toString());
        }
    }

    protected void runDDL(final String ddl) throws Exception {
        String separator = ";";
        if (ddl.startsWith(customDelimiterEnabler)) separator = customDelimiter;
        String[] statements = splitString(ddl, separator);
        for (int i = 0; i < statements.length; i++) {
            final String ddlStatement = removeComments(statements[i]);
            if (ArrayUtils.contains(excludedScriptStatements, ddlStatement)) {
                continue;
            }

            if (log.isDebugEnabled()) log.debug("Running statement: " + ddlStatement);
            new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                Work w = new Work() {
                public void execute(Connection connection) throws SQLException {
                    Statement statement = null;
                    try {
                        statement = connection.createStatement();
                        statement.execute(ddlStatement);
                    } catch (Exception e) {
                        Throwable root = ErrorManager.lookup().getRootCause(e);
                        log.error("Error executing " + ddlStatement + ": " + root.getMessage());
                    } finally {
                        if (statement != null) {
                            statement.close();
                        }
                    }
                }};
                session.doWork(w);
                session.flush();
            }}.execute();
        }
    }

    private String[] splitString(String str, String delims) {
        if (str == null) {
            return null;
        } else if (str.equals("") || delims == null || delims.length() == 0) {
            return new String[]{str};
        }
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
        for (int i = 0, cnt = s.length; i < cnt; i++) {
            s[i] = ((String) v.elementAt(i)).trim();
        }
        return s;
    }

    protected String removeComments(String ddlStatement) {
        StringBuffer sb = new StringBuffer();
        BufferedReader strreader = new BufferedReader(new StringReader(ddlStatement));
        String line = null;
        try {
            while ((line = strreader.readLine()) != null) {
                if (line.trim().startsWith("--")) continue;
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            log.error("Error: ", e);
        }
        return sb.toString().trim();
    }

    protected boolean existsModulesTable() throws Exception {
        final boolean[] returnValue = {false};
        new HibernateTxFragment(true) {
        protected void txFragment(Session session) throws Exception {
            Work w = new Work() {
            public void execute(Connection connection) throws SQLException {
                // IMPORTANT NOTE: SQL Server driver closes the previous result set. So it's very important to read the
                // data from the first result set before opening a new one. If not an exception is thrown.
                DatabaseMetaData metaData = connection.getMetaData();
                returnValue[0] = metaData.getTables(null, null, installedModulesTable.toLowerCase(), null).next();
                if (!returnValue[0]) returnValue[0] = metaData.getTables(null, null, installedModulesTable.toUpperCase(), null).next();
            }};
            session.doWork(w);
        }}.execute();
        return returnValue[0];
    }
}
