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

    protected static transient Log log = LogFactory.getLog(ExternalDataSource.class.getName());
    protected String name;
    protected DataSourceEntry dataSourceEntry;
    private PrintWriter printWriter = new PrintWriter(System.out);
    private int loginTimeOut = 0;
    private boolean disableAutoCommit;

    public ExternalDataSource() {
        this.name = null;
        this.dataSourceEntry = null;
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
        try {
            log.debug("Obtain data source connection: " + name);
            Connection conn = dataSourceEntry.getConnection();
            setAutoCommit(conn, !disableAutoCommit);
            return conn;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean getAutoCommit(Connection conn) {
        try {
            return conn.getAutoCommit();
        } catch (SQLException e) {
            // Ignore problems when trying to get autocommit.
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
