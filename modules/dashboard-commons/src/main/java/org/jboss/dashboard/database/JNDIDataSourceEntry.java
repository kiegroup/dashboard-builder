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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * A data source entry that gets the database connection doing a JNDI lookup of the target data source.
 */
public class JNDIDataSourceEntry extends DataSourceEntry {

    private static transient Log log = LogFactory.getLog(JNDIDataSourceEntry.class.getName());

    private DataSource dataSource = null;

    public DataSource getDataSource() throws NamingException {
        if (dataSource != null) return dataSource;

        Context context = new InitialContext();
        return dataSource = (DataSource) context.lookup(getJndiPath());
    }

    public Connection getConnection() throws Exception {
        log.debug("Obtain connection from: " + getJndiPath());
        return getDataSource().getConnection();
    }
}
