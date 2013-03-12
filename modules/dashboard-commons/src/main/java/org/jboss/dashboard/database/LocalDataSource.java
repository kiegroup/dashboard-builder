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
 * A data source to the underlying local database.
 */
public class LocalDataSource implements DataSource {

    private static transient Log log = LogFactory.getLog(LocalDataSource.class.getName());

    private PrintWriter printWriter = new PrintWriter(System.out);
    private int loginTimeOut = 0;

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

    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    public Connection getConnection() throws SQLException {
        final Connection[] connection = new Connection[]{null};
        try {
            new HibernateTxFragment() {
                protected void txFragment(Session session) throws Exception {
                    connection[0] = session.connection();
                }
            }.execute();
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new SQLException();
        }
        return connection[0];
    }

    public boolean isWrapperFor(Class<?> c) {
        return false;
    }

    public <T> T unwrap(Class<T> c) {
        return null;
    }

    public Logger getParentLogger() {
        return null;
    }
}
