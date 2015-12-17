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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.database.DataSourceEntry;
import org.jboss.dashboard.database.JDBCDataSourceEntry;
import org.jboss.dashboard.database.JNDIDataSourceEntry;
import org.jboss.dashboard.test.ShrinkWrapHelper;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.io.InputStream;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * Unit test for DataSourceImportManager class.
 */
@RunWith(Arquillian.class)
public class DataSourceImportManagerTest {

    private static transient Logger log = LoggerFactory.getLogger(DataSourceImportManagerTest.class.getName());

    @Deployment
    public static Archive<?> createTestArchive()  {
        return ShrinkWrapHelper.createJavaArchive()
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    BeanManager beanManager;
    
    @Inject
    protected DataSourceImportManager dataSourceImportManager;
    
    @Before
    public void setUp() throws Exception {
        CDIBeanLocator.beanManager = beanManager;
    }

    @Test
    public void testCreateJNDIDatasource() throws Exception {
        InputStream dataStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/jboss/dashboard/export/datasource-jndi.properties");
        DataSourceEntry entry = dataSourceImportManager.doImport(dataStream);

        assertThat(entry).isExactlyInstanceOf(JNDIDataSourceEntry.class);
        assertThat(entry.getName()).isEqualToIgnoringCase("Test JNDI datasource");
        assertThat(entry.getJndiPath()).isEqualToIgnoringCase("java:/comp/env/MyDSTest");
        assertThat(entry.getTestQuery()).isEqualToIgnoringCase("select 1;");
    }

    @Test
    public void testCreateJNDIInvalidDatasource() throws Exception {

        InputStream dataStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/jboss/dashboard/export/datasource-jndi-invalid.properties");
        try {
            DataSourceEntry entry = dataSourceImportManager.doImport(dataStream);
            failBecauseExceptionWasNotThrown(InvalidDataSourceDefinition.class);
        } catch (InvalidDataSourceDefinition e) {
            // It's expected.
        }
    }

    @Test
    public void testCreateJDBCDatasource() throws Exception {
        InputStream dataStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/jboss/dashboard/export/datasource-jdbc.properties");
        DataSourceEntry entry = dataSourceImportManager.doImport(dataStream);

        assertThat(entry).isExactlyInstanceOf(JDBCDataSourceEntry.class);
        assertThat(entry.getName()).isEqualToIgnoringCase("Test JDBC datasource");
        assertThat(entry.getUrl()).isEqualToIgnoringCase("jdbc:h2:tcp://localhost/~/temp/test");
        assertThat(entry.getDriverClass()).isEqualToIgnoringCase("org.h2.SomeDriverClass");
        assertThat(entry.getUserName()).isEqualToIgnoringCase("user1");
        assertThat(entry.getPassword()).isEqualToIgnoringCase("password1");
        assertThat(entry.getTestQuery()).isEqualToIgnoringCase("select 1;");
    }

    @Test
    public void testCreateJDBCInvalidDatasource() throws Exception {
        InputStream dataStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/jboss/dashboard/export/datasource-jdbc-invalid.properties");
        try {
            DataSourceEntry entry = dataSourceImportManager.doImport(dataStream);
            failBecauseExceptionWasNotThrown(InvalidDataSourceDefinition.class);
        } catch (InvalidDataSourceDefinition e) {
            // It's expected.
        }
    }
}
