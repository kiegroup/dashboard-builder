/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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

import com.mchange.v2.c3p0.C3P0ProxyConnection;
import com.mchange.v2.c3p0.impl.NewProxyConnection;
import org.apache.commons.dbcp.PoolableConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.HibernateException;
import org.jboss.dashboard.CoreServices;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.commons.misc.ReflectionUtils;
import org.jboss.jca.adapters.jdbc.WrappedConnection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Utility class to help with binary objects in Oracle
 */
@ApplicationScoped
@Named("LOBHelper")
public class LOBHelper {

    private static transient Logger log = LoggerFactory.getLogger(LOBHelper.class.getName());

    private static final String ORACLE_CLASS = "oracle.sql.BLOB";
    private static final String ORACLE_TEMP_METHOD = "createTemporary";
    private static final String ORACLE_DURATION__SESSION = "DURATION_SESSION";
    private static final String ORACLE_JDBC_ORACLE_CONNECTION = "oracle.jdbc.OracleConnection";
    private static final String ORACLE_OPEN_METHOD = "open";
    private static final String ORACLE_MODE__READWRITE = "MODE_READWRITE";
    private static final String ORACLE_GET_BINARY_OUTPUT_STREAM = "getBinaryOutputStream";
    private static final String ORACLE_CLOSE = "close";

    public static LOBHelper lookup() {
        return (LOBHelper) CDIBeanLocator.getBeanByName("LOBHelper");
    }

    public static abstract class ValueWriter {
        public abstract void writeValue(OutputStream os, Object value) throws IOException;

        public abstract void writeValue(PreparedStatement st, Object value, int index) throws SQLException;
    }

    private Method getMethod(Class clazz, String method, Class c1, Class c2, Class c3) throws NoSuchMethodException {
        int nParams = 0;
        if (c3 != null) nParams = 3;
        else if (c2 != null) nParams = 2;
        else if (c1 != null) nParams = 1;

        Class types[] = new Class[nParams];

        if (c3 != null) {
            types[2] = c3;
        }
        if (c2 != null) {
            types[1] = c2;
        }
        if (c1 != null) {
            types[0] = c1;
        }

        return clazz.getDeclaredMethod(method, types);
    }

    public void oracleNullSafeSet(PreparedStatement statement, Object value, int index, ValueWriter vw) throws HibernateException, SQLException {
        try {
            // Invoke by reflection the Oracle classes
            Class oracleBlobClass = Class.forName(ORACLE_CLASS);

            Method createTempMethod = getMethod(oracleBlobClass, ORACLE_TEMP_METHOD, Connection.class, Boolean.TYPE, Integer.TYPE);

            Field durationSession = oracleBlobClass.getField(ORACLE_DURATION__SESSION);
            Object arglist[] = new Object[3];
            Connection conn = statement.getConnection();
            arglist[0] = conn;
            arglist[1] = Boolean.TRUE;
            arglist[2] = durationSession.get(null);

            Object tempBlob = null;

            // Needed to avoid JBoss AS class loading issues...
            Class connClassInCurrentClassLoader = Class.forName(conn.getClass().getName());

            // Direct Oracle connection
            if (Class.forName(ORACLE_JDBC_ORACLE_CONNECTION).isAssignableFrom(connClassInCurrentClassLoader)) {
                tempBlob = createTempMethod.invoke(null, arglist); // null is valid because of static method
            }
            // JBoss AS data source wrapper connection.
            else if (WrappedConnection.class.isAssignableFrom(connClassInCurrentClassLoader)) {
                arglist[0] = ReflectionUtils.invokeMethod(conn, "getUnderlyingConnection", null);
                tempBlob = createTempMethod.invoke(null, arglist); // null is valid because of static method
            }
            // C3P0 pool managed connection.
            else if (NewProxyConnection.class.isAssignableFrom(connClassInCurrentClassLoader)) {
                NewProxyConnection castCon = (NewProxyConnection) conn;
                arglist[0] = C3P0ProxyConnection.RAW_CONNECTION;
                tempBlob = castCon.rawConnectionOperation(createTempMethod, C3P0ProxyConnection.RAW_CONNECTION, arglist);
            }
            // Apache's DBCP pool managed connection.
            else if (PoolableConnection.class.isAssignableFrom(connClassInCurrentClassLoader)) {
                arglist[0] = ((PoolableConnection) statement.getConnection()).getDelegate();
                tempBlob = createTempMethod.invoke(null, arglist); // null is valid because of static method

            } else {

                boolean throwException = true;
                //check if we are running in websphere
                try {
                    String wasConnectionWrapper = "com.ibm.ws.rsadapter.jdbc.WSJdbcConnection";
                    Class wasConnectionWrapperClass = Class.forName(wasConnectionWrapper);

                    if (wasConnectionWrapperClass.isAssignableFrom(connClassInCurrentClassLoader)) {

                        String helperClass = "com.ibm.websphere.rsadapter.WSCallHelper";
                        Class helper = Class.forName(helperClass);

                        Method nativeConnMethod = helper.getMethod("getNativeConnection", new Class[]{Object.class});

                        Connection nativeConn = (Connection) nativeConnMethod.invoke(null, conn);

                        if (Class.forName(ORACLE_JDBC_ORACLE_CONNECTION).isAssignableFrom(nativeConn.getClass())) {

                            arglist[0] = nativeConn;
                            tempBlob = createTempMethod.invoke(null, arglist);
                            throwException = false;
                        }
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                    // do nothing
                }
                if(throwException){
                    throw new HibernateException("JDBC connection object must be a oracle.jdbc.OracleConnection " +
                            "a " + WrappedConnection.class.getName() +
                            "a " + PoolableConnection.class.getName() +
                            "or a " + NewProxyConnection.class.getName() +
                            ". Connection class is " + connClassInCurrentClassLoader.getName());
                }
            }

            Method openMethod = getMethod(oracleBlobClass, ORACLE_OPEN_METHOD, Integer.TYPE, null, null);

            Field fieldReadWrite = oracleBlobClass.getField(ORACLE_MODE__READWRITE);
            arglist = new Object[1];
            arglist[0] = fieldReadWrite.get(null); //null is valid because of static field

            openMethod.invoke(tempBlob, arglist);

            Method getOutputStreamMethod = oracleBlobClass.getDeclaredMethod(ORACLE_GET_BINARY_OUTPUT_STREAM, null);

            OutputStream os = (OutputStream) getOutputStreamMethod.invoke(tempBlob, null);

            try {
                vw.writeValue(os, value);
                os.flush();
            } finally {
                os.close();
            }

            Method closeMethod = oracleBlobClass.getDeclaredMethod(ORACLE_CLOSE, null);

            closeMethod.invoke(tempBlob, null);

            statement.setBlob(index, (Blob) tempBlob);
        } catch (Exception e) {
            throw new HibernateException("Error in oracleNullSafeSet", e);
        }
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index, ValueWriter vw) throws HibernateException, SQLException {
        if (log.isDebugEnabled())
            log.debug("Setting value of class " + (value == null ? "<null>" : value.getClass().getName()));

        if (CoreServices.lookup().getHibernateInitializer().isOracleDatabase()) {
            oracleNullSafeSet(st, value, index, vw);
        } else {
            vw.writeValue(st, value, index);
        }
    }

}
