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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.jboss.dashboard.CoreServices;

import java.io.*;
import java.sql.*;
import java.util.Arrays;

/**
 * Note that we don't use CLOBs to avoid ORACLE handling the encoding. We just store String bytes using default encoding,
 * so encoding related problems rely always in our application.
 */
public class StringBlobType implements UserType, Serializable {

    private static transient Logger log = LoggerFactory.getLogger(StringBlobType.class.getName());

    public static final String STRING_ENCODING = "UTF-8";

    public int[] sqlTypes() {
        return new int[]{Types.BLOB};
    }

    public Class returnedClass() {
        return String.class;
    }

    public boolean equals(Object x, Object y) {
        return (x == y)
                || (x != null
                && y != null
                && x.equals(y));
    }

    public int hashCode(Object o) throws HibernateException {
        return o == null ? 0 : o.hashCode();
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        LOBHelper.lookup().nullSafeSet(st, value, index, new LOBHelper.ValueWriter() {
            public void writeValue(OutputStream os, Object value) throws IOException {
                if (value != null) {
                    os.write(((String) value).getBytes(STRING_ENCODING));
                }
            }

            public void writeValue(PreparedStatement st, Object value, int index) throws SQLException {
                HibernateInitializer hi = CoreServices.lookup().getHibernateInitializer();
                if (hi.isPostgresDatabase()) {
                    st.setString(index, ((String) value));
                } else if (hi.isH2Database()) {
                    st.setString(index, ((String) value));
                } else if (hi.isMySQLDatabase()) {
                    try {
                        st.setBytes(index, value == null ? null : ((String) value).getBytes(STRING_ENCODING));
                    } catch (UnsupportedEncodingException e) {
                        log.error("Error: ", e);
                    }
                } else if (hi.isSQLServerDatabase()) {
                    st.setString(index, (String) value);
                } else {
                    throw new IllegalArgumentException("Unknown database name: " + hi.getDatabaseName());
                }
            }
        });
    }

    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        if (log.isDebugEnabled()) log.debug("Getting value with names " + Arrays.asList(names));
        Object o = rs.getObject(names[0]);
        if (o == null) {
            return null;
        } else if (o instanceof Blob) {
            final Blob blob = (Blob) o;
            try {
                byte bytes[] = blob.getBytes(1, (int) blob.length());
                if (bytes == null) return null;
                return new String(bytes, STRING_ENCODING);
            } catch (UnsupportedEncodingException e) {
                log.error("Error:", e);
                return new String(blob.getBytes(1, (int) blob.length()));
            }
        } else if (o instanceof String) {
            return o;
        } else if (o instanceof byte[]) {
            try {
                return new String((byte[]) o, STRING_ENCODING);
            } catch (UnsupportedEncodingException e) {
                log.error("Error: ", e);
                return null;
            }
        } else if (o instanceof Clob) {
            //added for H2 support
            final Clob clob = (Clob)o;
            return clob.getSubString(1, (int) clob.length());
        } else {
            throw new IllegalArgumentException("Unexpected value read. Must be Blob or String, but it is " + o.getClass());
        }
    }


    public Object deepCopy(Object value) {
        if (value == null) {
            return null;
        }
        else if (value instanceof String) {
            return value;
        } else {
            throw new IllegalArgumentException("Unexpected value to copy. Must be String, but it is " + value.getClass());
        }
    }

    public boolean isMutable() {
        return false;
    }

    public Serializable disassemble(Object o) throws HibernateException {
        if (o == null) return null;
        return (String) o; // o is a String
    }

    public Object assemble(Serializable serializable, Object owner) throws HibernateException {
        if (serializable == null) return null;
        return serializable.toString();
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
}

