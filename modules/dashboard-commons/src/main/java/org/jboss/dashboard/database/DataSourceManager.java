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
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager used to access the DataSource instances configured in the current installation.
 */
@ApplicationScoped
public class DataSourceManager {

    public static final String DEFAULT_DATASOURCE_NAME = "local";
    private static transient Log log = LogFactory.getLog(DataSourceManager.class.getName());

    /**
     * Set of data sources used by the current thread.
     * this thread local is required to support distributed transactions. 
     */
    protected static transient ThreadLocal currentThreadDataSources = new ThreadLocal();

    protected String[] initialClassesToLoad;

    public String[] getInitialClassesToLoad() {
        return initialClassesToLoad;
    }

    public void setInitialClassesToLoad(String[] initialClassesToLoad) {
        this.initialClassesToLoad = initialClassesToLoad;
    }

    @PostConstruct
    public void start() throws Exception {
        // Load database drivers.
        if (initialClassesToLoad != null) {
            for (int i = 0; i < initialClassesToLoad.length; i++) {
                checkDriverClassAvailable(initialClassesToLoad[i]);
            }
        }
        for (DataSourceEntry entry : getDataSourceEntries()) {
            checkDriverClassAvailable(entry.getDriverClass());
        }
    }

    public boolean checkDriverClassAvailable(String driverClassName) {
        try {
            Class.forName(driverClassName);
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

    public DataSourceEntry getDataSourceEntryByName(final String name) throws Exception {
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
    public DataSource getDatasource(final String name) throws Exception {
        // Data sources are cached at a thread level because they are attached to the underlying thread distributed transaction.
        DataSource ds = getCurrentThreadDataSource(name);
        if (ds != null) return ds;

        // Search the data source with the given name into the database.
        final DataSourceEntry[] entryResult = new DataSourceEntry[] {null};
        try {
            new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                Query query = session.createQuery(" from " + DataSourceEntry.class.getName() + " entry where entry.name = :entryName ");
                FlushMode oldFlushMode = session.getFlushMode();
                session.setFlushMode(FlushMode.COMMIT);
                query.setString("entryName", name);
                query.setCacheable(true);
                List results = query.list();
                if (results.size() == 1) entryResult[0] = (DataSourceEntry) results.get(0);
                else log.warn("There are " + results.size() + " datasource entries with name " + name);
                session.setFlushMode(oldFlushMode);
            }}.execute();
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        // Data source not found.
        if (entryResult[0] == null) return null;

        // Add a proxy data source to the cache before returning.
        ds = ExternalDataSource.lookup(name, entryResult[0]);
        setCurrentThreadDataSource(name, ds);
        return ds;
    }

    /**
     * Retrieve a data sources being used by the current thread.
     */
    protected DataSource getCurrentThreadDataSource(String name) {
        Map<String, DataSource> dsMap = getCurrentThreadDataSources();
        return dsMap.get(name);
    }

    /**
     * Store a data source as used by the current thread.
     */
    protected void setCurrentThreadDataSource(String name, DataSource ds) {
        Map<String, DataSource> dsMap = getCurrentThreadDataSources();
        dsMap.put(name, ds);
    }

    /**
     * Retrieve the data sources being used by the current thread.
     */
    protected Map<String, DataSource> getCurrentThreadDataSources() {
        Map<String, DataSource> dsMap = (Map) currentThreadDataSources.get();
        if (dsMap == null) {
            dsMap = new HashMap();
            dsMap.put(DEFAULT_DATASOURCE_NAME, new LocalDataSource());
            currentThreadDataSources.set(dsMap);
        }
        return dsMap;
    }
}

