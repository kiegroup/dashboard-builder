/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jboss.dashboard.export;

import org.jboss.dashboard.database.DataSourceEntry;
import org.jboss.dashboard.database.JDBCDataSourceEntry;
import org.jboss.dashboard.database.JNDIDataSourceEntry;

import javax.enterprise.context.ApplicationScoped;
import java.io.InputStream;
import java.util.Properties;

/**
 * <p>Import manager for datasource definitions.</p>
 * 
 * <p>The datasource definitions are represented in properties files.</p>
 * 
 * <p>The properties for a JNDI datasource are:</p>
 * <ul>
 *     <li>type - MUST be <code>JNDI</code></li>
 *     <li>name</li>
 *     <li>jndiPath</li>
 *     <li>testQuery</li>
 * </ul>
 *
 * <p>The properties for a JDBC datasource are:</p>
 * <ul>
 *     <li>type - MUST be <code>JBBC</code></li>
 *     <li>name</li>
 *     <li>jdbcUrl</li>
 *     <li>driverClass</li>
 *     <li>user</li>
 *     <li>password</li>
 *     <li>testQuery</li>
 * </ul>
 */
@ApplicationScoped
public class DataSourceImportManager {

    /**
     * The properties for the datasource definition.
     */
    protected static final String PROPERTY_TYPE = "type";
    protected static final String PROPERTY_NAME = "name";
    protected static final String PROPERTY_JNDI = "jndiPath";
    protected static final String PROPERTY_URL = "jdbcUrl";
    protected static final String PROPERTY_DRIVER_CLASS = "driverClass";
    protected static final String PROPERTY_USER = "user";
    protected static final String PROPERTY_PASSWORD = "password";
    protected static final String PROPERTY_TEST_QUERY = "testQuery";

    public static final String DATASOURCE_EXTENSION = "datasource";


    protected enum DataSourceType {
        JNDI, JDBC;
    }

    /**
     * Creates a new data source definitions based on its properties.
     * Note that the entry is not saved, the returning object it's a transient one.
     * 
     * @param dataSourceInputStream The data source properties input stream..
     * @return The new data source definition.
     */
    public DataSourceEntry doImport(InputStream dataSourceInputStream) throws InvalidDataSourceDefinition, Exception {
        if (dataSourceInputStream == null) return null;


        Properties dataSourceProperties = new Properties();
        dataSourceProperties.load(dataSourceInputStream);
        
        String type = dataSourceProperties.getProperty(PROPERTY_TYPE);
        if (type == null || type.trim().length() == 0) throw new InvalidDataSourceDefinition("Property type is not present in datasource definition.");

        DataSourceEntry entry = null;
        if (type.equalsIgnoreCase(DataSourceType.JNDI.name())) {
            entry = createJNDIDatasource(dataSourceProperties);
        } else if (type.equalsIgnoreCase(DataSourceType.JDBC.name())) {
            entry = createJDBCDatasource(dataSourceProperties);
        } else {
            throw new InvalidDataSourceDefinition("The value '" + type + "' for property type is not supported.");
        }
        
        return entry;
    }

    /**
     * Creates a new instance of JNDI data source.
     * 
     * @param dataSourceProperties The JNDI data source definition properties.
     * @return The new JNDI data source instance.
     * @throws InvalidDataSourceDefinition Invalid definition.
     */
    protected JNDIDataSourceEntry createJNDIDatasource(Properties dataSourceProperties) throws InvalidDataSourceDefinition {

        String name = dataSourceProperties.getProperty(PROPERTY_NAME);
        String jndi = dataSourceProperties.getProperty(PROPERTY_JNDI);
        String testQuery = dataSourceProperties.getProperty(PROPERTY_TEST_QUERY);
        
        // Check if required properties exists.
        if (name == null || name.trim().length() == 0) throw new InvalidDataSourceDefinition("Datasource name is not present in the datasource definition.");
        if (jndi == null || jndi.trim().length() == 0) throw new InvalidDataSourceDefinition("Datasource JNDI path is not present in the datasource definition.");
        
        JNDIDataSourceEntry entry = new JNDIDataSourceEntry();
        entry.setName(name);
        entry.setJndiPath(jndi);
        entry.setTestQuery(testQuery);
        
        return entry;
    }

    /**
     * Creates a new instance of JDBC data source.
     *
     * @param dataSourceProperties The JDBC data source definition properties.
     * @return The new JDBC data source instance.
     * @throws InvalidDataSourceDefinition Invalid definition.
     */
    protected JDBCDataSourceEntry createJDBCDatasource(Properties dataSourceProperties) throws InvalidDataSourceDefinition {
        String name = dataSourceProperties.getProperty(PROPERTY_NAME);
        String jdbc = dataSourceProperties.getProperty(PROPERTY_URL);
        String driver = dataSourceProperties.getProperty(PROPERTY_DRIVER_CLASS);
        String user = dataSourceProperties.getProperty(PROPERTY_USER);
        String password = dataSourceProperties.getProperty(PROPERTY_PASSWORD);
        String testQuery = dataSourceProperties.getProperty(PROPERTY_TEST_QUERY);

        // Check if required properties exists.
        if (name == null || name.trim().length() == 0) throw new InvalidDataSourceDefinition("Datasource name is not present in the datasource definition.");
        if (jdbc == null || jdbc.trim().length() == 0) throw new InvalidDataSourceDefinition("Datasource JDBC path is not present in the datasource definition.");
        if (driver == null || driver.trim().length() == 0) throw new InvalidDataSourceDefinition("Datasource driver classname is not present in the datasource definition.");
        if (user == null || user.trim().length() == 0) throw new InvalidDataSourceDefinition("Datasource user is not present in the datasource definition.");
        if (password == null || password.trim().length() == 0) throw new InvalidDataSourceDefinition("Datasource password is not present in the datasource definition.");

        JDBCDataSourceEntry entry = new JDBCDataSourceEntry();
        entry.setName(name);
        entry.setUrl(jdbc);
        entry.setDriverClass(driver);
        entry.setUserName(user);
        entry.setPassword(password);
        entry.setTestQuery(testQuery);

        return entry;
    }
}
