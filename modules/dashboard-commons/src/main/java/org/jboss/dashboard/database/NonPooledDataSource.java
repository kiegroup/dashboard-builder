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

import org.jboss.dashboard.commons.misc.ReflectionUtils;
import org.jboss.dashboard.error.ErrorManager;
import org.jboss.dashboard.profiler.CodeBlockTrace;
import org.jboss.dashboard.profiler.CodeBlockType;
import org.jboss.dashboard.profiler.CoreCodeBlockTypes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A data source implementation that bounds every connection to the underlying thread.
 */
public class NonPooledDataSource implements DataSource {

    private static transient Log log = LogFactory.getLog(NonPooledDataSource.class.getName());
    protected PrintWriter printWriter;
    protected int loginTimeOut;

    // Data source properties
    protected String url;
    protected String user;
    protected String password;
    protected String driver;
    protected int isolation;
    protected boolean autoCommit;

    public NonPooledDataSource() {
        this.loginTimeOut = 0;
        this.printWriter = new PrintWriter(System.out);
        this.autoCommit = false;
        this.isolation = Connection.TRANSACTION_SERIALIZABLE;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public int getIsolation() {
        return isolation;
    }

    public void setIsolation(int isolation) {
        this.isolation = isolation;
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    // javax.sql.DataSource implementation

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

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url, user, password);
            setAutoCommit(conn, autoCommit);
            setIsolation(conn, isolation);
            return createConnectionProxy(conn);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Logger getParentLogger() {
        return null;
    }

    protected boolean getAutoCommit(Connection conn) {
        try {
            return conn.getAutoCommit();
        } catch (SQLException e) {
            // Ignore problems when trying to get autocommit.
            // In some environments (Presidencia - Informix) when trying to get autocommit an exception is thrown.
            log.debug("Can not get autocommit.", e);
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
            log.debug("Can not set autocommit.", e);
        }
    }

    protected void setIsolation(Connection conn,int isolation) {
        try {
            if (conn.getTransactionIsolation() != isolation) {
                conn.setTransactionIsolation(isolation);
            }
        } catch (SQLException e) {
            log.debug("Can not set connection isolation.", e);
        }
    }

    // java.sql.Connection proxy

    public Connection createConnectionProxy(Connection conn) throws SQLException {
        return (Connection) Proxy.newProxyInstance(
                conn.getClass().getClassLoader(),
                getClassInterfaces(conn.getClass()),
                new ConnectionInvocationHandler(conn));
    }

    private class ConnectionInvocationHandler implements InvocationHandler {

        private Connection conn = null;

        public ConnectionInvocationHandler(Connection conn) {
            this.conn = conn;
        }

        public Object invoke(Object proxy, final Method m, final Object[] args) throws Throwable {
            // Capture commit.
            if (m.getName().equals("commit")) {
                CodeBlockTrace trace = new SQLStatementTrace("commit").begin();
                try {
                    return m.invoke(conn, args);
                } catch (Throwable e) {
                    ErrorManager.lookup().notifyError(e, true);
                    throw e;
                } finally {
                    trace.end();
                }
            }
            // Capture rollback.
            if (m.getName().equals("rollback")) {
                CodeBlockTrace trace = new SQLStatementTrace("rollback").begin();
                try {
                    return m.invoke(conn, args);
                } catch (Throwable e) {
                    ErrorManager.lookup().notifyError(e, true);
                    throw e;
                } finally {
                    trace.end();
                }
            }
            // Capture Statement creation.
            Object result = m.invoke(conn, args);
            if (m.getReturnType() != null) {
                if (m.getReturnType().equals(PreparedStatement.class)) {
                    String sql = (String) args[0];
                    return createPreparedStatementProxy((PreparedStatement) result, sql);
                }
                if (m.getReturnType().equals(Statement.class)) {
                    return createStatementProxy((Statement) result);
                }
            }
            return result;
        }
    }

    // java.sql.Statement proxy

    /** A cache of java class interfaces */
    protected transient Map<Class,Class[]> _classInterfacesMap = new HashMap<Class,Class[]>();

    protected Class[] getClassInterfaces(Class clazz) {
        Class[] result = _classInterfacesMap.get(clazz);
        if (result != null) return result;

        _classInterfacesMap.put(clazz, result = ReflectionUtils.getClassHierarchyInterfaces(clazz));
        return result;
    }

    protected Statement createStatementProxy(Statement stmt) throws SQLException {
        return (Statement)Proxy.newProxyInstance(
                stmt.getClass().getClassLoader(),
                getClassInterfaces(stmt.getClass()),
                new StatementInvocationHandler(stmt));
    }

    protected Statement createPreparedStatementProxy(PreparedStatement stmt, String sql) throws SQLException {
        return (Statement)Proxy.newProxyInstance(
                stmt.getClass().getClassLoader(),
                getClassInterfaces(stmt.getClass()),
                new PreparedStatementInvocationHandler(stmt, sql));
    }

    static class StatementInvocationHandler implements InvocationHandler {

        protected Statement stmt;

        public StatementInvocationHandler(Statement stmt) {
            this.stmt = stmt;
        }

        public Object invoke(Object proxy, final Method m, final Object[] args) throws Throwable {
            if (m.getName().startsWith("execute") && args != null && args.length > 0) {
                String sqlToExec = (String) args[0];
                CodeBlockTrace trace = new SQLStatementTrace(sqlToExec).begin();
                try {
                    if (log.isDebugEnabled()) log.debug(sqlToExec);
                    return m.invoke(stmt, args);
                } catch (Throwable e) {
                    ErrorManager.lookup().notifyError(e, true);
                    throw e;
                } finally {
                    trace.end();
                }
            } else {
                return m.invoke(stmt, args);
            }
        }
    }

    static class PreparedStatementInvocationHandler extends StatementInvocationHandler {

        protected String sql;

        public PreparedStatementInvocationHandler(Statement stmt, String sql) {
            super(stmt);
            this.sql = sql;
        }

        public Object invoke(Object proxy, final Method m, final Object[] args) throws Throwable {
            if (m.getName().startsWith("execute")) {
                String sqlToExec = args != null && args.length > 0 ? (String) args[0] : sql;
                CodeBlockTrace trace = new SQLStatementTrace(sqlToExec).begin();
                try {
                    if (log.isDebugEnabled()) log.debug(sqlToExec);
                    return m.invoke(stmt, args);
                } catch (Throwable e) {
                    ErrorManager.lookup().notifyError(e, true);
                    throw e;
                } finally {
                    trace.end();
                }
            } else {
                return m.invoke(stmt, args);
            }
        }
    }

    static class SQLStatementTrace extends CodeBlockTrace {

        protected Map<String,Object> context;

        public SQLStatementTrace(String sql) {
            super(stripAfterWhere(sql));
            context = new HashMap<String,Object>();
            context.put("SQL", sql);
        }

        public CodeBlockType getType() {
            return CoreCodeBlockTypes.SQL;
        }

        public String getDescription() {
            return (String) context.get("SQL");
        }

        public Map<String,Object> getContext() {
            return context;
        }

        /**
         * To group sensibly and to avoid recording sensitive data, Don't record the where clause
         * (only used for dynamic SQL since parameters aren't included in prepared statements)
         * @return subset of passed SQL up to the where clause.
         */
        public static String stripAfterWhere(String sql) {
            for (int i=0; i<sql.length()-4; i++) {
                if (sql.charAt(i)=='w' || sql.charAt(i)=='W') {
                    if (sql.substring(i+1, i+5).equalsIgnoreCase("here")) {
                        return sql.substring(0, i);
                    }
                }
            }
            return sql;
        }
    }
}