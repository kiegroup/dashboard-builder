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

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.*;
import java.util.Arrays;

public class BinaryBlobType extends org.hibernate.lob.BlobImpl implements UserType, Serializable {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(BinaryBlobType.class.getName());

    public BinaryBlobType(InputStream inputStream, int i) {
        super(inputStream, i);
    }

    public BinaryBlobType(byte[] bytes) {
        super(bytes);
    }

    public BinaryBlobType() {
        super(new byte[0]);
    }

    public int[] sqlTypes() {
        return new int[]{Types.BLOB};
    }

    public Class returnedClass() {
        return byte[].class;
    }

    public boolean equals(Object x, Object y) {
        return (x == y)
                || (x != null
                && y != null
                && java.util.Arrays.equals((byte[]) x, (byte[]) y));
    }

    public int hashCode(Object o) throws HibernateException {
        return o == null ? 0 : o.hashCode();
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        LOBHelper.lookup().nullSafeSet(st, value, index, new LOBHelper.ValueWriter() {
            public void writeValue(OutputStream os, Object value) throws IOException {
                if (value != null) {
                    os.write((byte[]) value);
                }
            }
            public void writeValue(PreparedStatement st, Object value, int index) throws SQLException {
                st.setBytes(index, (byte[]) value);
            }
        });
    }

    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        if (log.isDebugEnabled()) log.debug("Getting value with names " + Arrays.asList(names));

        Object o = rs.getObject(names[0]);
        if (o == null) {
            return null;
        } else if (o instanceof Blob) {
            final Blob blob = (Blob) o;
            if (blob == null) return null;
            return blob.getBytes(1, (int) blob.length());
        } else if (o instanceof byte[]) {
            return o;
        } else {
            throw new IllegalArgumentException("Unexpected value read. Must be Blob or byte[], but it is " + o.getClass());
        }
    }


    public Object deepCopy(Object value) {
        if (value == null) return null;

        byte[] bytes = (byte[]) value;
        byte[] result = new byte[bytes.length];
        System.arraycopy(bytes, 0, result, 0, bytes.length);

        return result;
    }

    public boolean isMutable() {
        return true;
    }

    public Serializable disassemble(Object o) throws HibernateException {
        if (o == null) return null;
        byte[] b = (byte[]) o;
        return b; // o is a byte[]
    }

    public Object assemble(Serializable serializable, Object owner) throws HibernateException {
        if (serializable == null) return null;
        return (byte[]) serializable;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        if (original == null) return null;
        byte[] copy = new byte[((byte[]) original).length];
        System.arraycopy((byte[]) original, 0, copy, 0, copy.length);
        return copy;
    }
}

