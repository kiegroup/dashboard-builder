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

import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.CoreServices;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager used to access the DataSource instances configured in the current installation.
 */
@ApplicationScoped
public class DataSourceManager {

    public static final String DEFAULT_DATASOURCE_NAME = "local";
    private static transient Log log = LogFactory.getLog(DataSourceManager.class.getName());

    @PostConstruct
    public void start() throws Exception {
        // Load database drivers.
        for (DataSourceEntry entry : getDataSourceEntries()) {
            checkDriverClassAvailable(entry.getDriverClass());
        }
    }

    public boolean checkDriverClassAvailable(String driverClassName) {
        try {
            if (!StringUtils.isBlank(driverClassName)) {
                Class.forName(driverClassName);
            }
        } catch (ClassNotFoundException e) {
            log.warn("Driver class not found: " + driverClassName);
            return false;
        }
        return true;
    }

    /**
     * Get all registered datasource names
     */
    public List<String> getDataSourceNames() {
        List names = new ArrayList();
        names.add(DEFAULT_DATASOURCE_NAME);
        for (DataSourceEntry entry : getDataSourceEntries()) {
            names.add(entry.getName());
        }
        return names;
    }

    /**
     * Get all registered datasource entries
     */
    public List<DataSourceEntry> getDataSourceEntries() {
        final List result = new ArrayList();
        try {
            new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                Query query = session.createQuery(" from " + DataSourceEntry.class.getName());
                FlushMode oldFlushMode = session.getFlushMode();
                session.setFlushMode(FlushMode.COMMIT);
                query.setCacheable(true);
                result.addAll(query.list());
                session.setFlushMode(oldFlushMode);
            }}.execute();
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return result;
    }

    public DataSourceEntry getDataSourceEntry(final String name) throws Exception {
        if (name == null) return null;

        final List results = new ArrayList();
        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            FlushMode flushMode = session.getFlushMode();
            session.setFlushMode(FlushMode.COMMIT);

            StringBuffer sql = new StringBuffer();
            sql.append("select dse ");
            sql.append("from ").append(DataSourceEntry.class.getName()).append(" as dse ");
            sql.append("where dse.name = :name");

            Query query = session.createQuery(sql.toString());
            query.setString("name", name);
            query.setCacheable(true);
            results.addAll(query.list());
            session.setFlushMode(flushMode);
        }}.execute();
        if (results.size() > 0) {
            if (results.size() > 1) log.error("There are " + results.size() + " data sources with name=" + name);
            return (DataSourceEntry)results.get(0);
        }
        else {
            log.debug("Does not exists data source with name: " + name);
            return null;
        }        
    }

    /**
     * Get a datasource given its name
     */
    public DataSource getDataSource(String name) throws Exception {
        // Check if the requested data source is the default local
        if (DEFAULT_DATASOURCE_NAME.equals(name))  {
            return CoreServices.lookup().getHibernateInitializer().getLocalDataSource();
        }
        DataSourceEntry entry = getDataSourceEntry(name);
        if (entry == null) return null;

        ExternalDataSource ds = new ExternalDataSource();
        ds.setName(name);
        ds.setDataSourceEntry(entry);
        ds.setDisableAutoCommit(false);
        return ds;
    }
}

