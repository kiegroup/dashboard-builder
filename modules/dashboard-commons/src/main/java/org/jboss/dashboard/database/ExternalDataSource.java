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

import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * A data source implementation that bounds its connections to the underlying transaction.
 * <p>When a connection is requested it gets a connection from the pool and attach such connection
 * to the current transaction. When the transaction completes the data source
 * connection is automatically completed as well.
 */
public class ExternalDataSource implements DataSource {

    /**
     * Get an external data source instance.
     * @param name The name to assign to the datasource.
     * @param dsEntry The object containing the connection configuration for this datasource.
     */
    public static ExternalDataSource lookup(String name, DataSourceEntry dsEntry) {
        ExternalDataSource ds = new ExternalDataSource();
        ds.setName(name);
        ds.setDataSourceEntry(dsEntry);
        ds.setDisableAutoCommit(true);
        return ds;
    }

    protected static transient Log log = LogFactory.getLog(LocalDataSource.class.getName());
    protected String name;
    protected DataSourceEntry dataSourceEntry;
    protected transient Connection currentConnection;
    private PrintWriter printWriter = new PrintWriter(System.out);
    private int loginTimeOut = 0;
    private boolean disableAutoCommit;

    public ExternalDataSource() {
        this.name = null;
        this.dataSourceEntry = null;
        this.currentConnection = null;
        this.disableAutoCommit = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDataSourceEntry(DataSourceEntry dataSourceEntry) {
        this.dataSourceEntry = dataSourceEntry;
    }

    public DataSourceEntry getDataSourceEntry() {
        return dataSourceEntry;
    }

    public int getLoginTimeout() throws SQLException {
        return loginTimeOut;
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        this.loginTimeOut = seconds;
    }

    public PrintWriter getLogWriter() throws SQLException {
        return printWriter;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        this.printWriter = out;
    }

    public boolean isDisableAutoCommit() {
        return disableAutoCommit;
    }

    public void setDisableAutoCommit(boolean disableAutoCommit) {
        this.disableAutoCommit = disableAutoCommit;
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    public Connection getConnection() throws SQLException {
        if (currentConnection == null) initConnection();
        return currentConnection;
    }

    public Logger getParentLogger() {
        return null;
    }

    protected void initConnection() {
        try {
            new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                log.debug("Obtain data source connection: " + name);
                // Bug Fix: under some circumstances, the dataSourceEntry attribute became out of sync with the Hibernate session.
                dataSourceEntry = (DataSourceEntry) session.get(dataSourceEntry.getClass(), dataSourceEntry.getDbid());
                Connection conn = dataSourceEntry.getConnection();
                setAutoCommit(conn, !disableAutoCommit);
                currentConnection = conn;
                registerForCallbackNotifications();
            }
            protected void beforeRollback() throws Exception {
                // Rollback asap.
                completeConnection(false);
            }
            protected void afterRollback() throws Exception {
                // Rollback if before commit fails.
                completeConnection(false);
            }
            protected void afterCommit() throws Exception {
                // Commit as late as possible.
                completeConnection(true);
            }}.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void completeConnection(boolean commit) throws SQLException {
        if (currentConnection != null && !currentConnection.isClosed()) {
            if (!getAutoCommit(currentConnection)) {
                log.debug(commit ? "Commit" : "Rollback" + " data source connection: " + name);
                if (commit) currentConnection.commit();
                else currentConnection.rollback();
            }
            currentConnection.close();
        }
        currentConnection = null;
    }

    protected boolean getAutoCommit(Connection conn) {
        try {
            return conn.getAutoCommit();
        } catch (SQLException e) {
            // Ignore problems when trying to get autocommit.
            // In some environments (Presidencia - Informix) when trying to get autocommit an exception is thrown.
            log.debug("Can not get autocommit for datasource: " + name, e);
            return true;
        }
    }

    protected void setAutoCommit(Connection conn, boolean autocommit) {
        try {
            if (getAutoCommit(conn) != autocommit) {
                conn.setAutoCommit(autocommit);
            }
        } catch (SQLException e) {
            // Ignore problems when trying to change autocommit.
            // In some environments (Presidencia - Informix) when trying to set autocommit=true an exception is thrown.
            log.debug("Can not set autocommit for datasource: " + name, e);
        }
    }

    public boolean isWrapperFor(Class<?> c) {
        return false;
    }

    public <T> T unwrap(Class<T> c) {
        return null;
    }
    
}
