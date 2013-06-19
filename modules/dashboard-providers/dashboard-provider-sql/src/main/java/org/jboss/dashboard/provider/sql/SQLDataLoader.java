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
package org.jboss.dashboard.provider.sql;

import org.jboss.dashboard.database.hibernate.HibernateInitializer;
import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.dataset.sql.SQLDataSet;
import org.jboss.dashboard.provider.AbstractDataLoader;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.database.DataSourceManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.dashboard.CoreServices;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class SQLDataLoader extends AbstractDataLoader {

    private transient static Log log = LogFactory.getLog(SQLDataLoader.class);

    public static final String PARAM_DEFAULT_QUERY = "default";
    protected String dataSource;
    protected String dataBaseName;
    protected Map<String,String> queryMap;

    public SQLDataLoader() {
        dataBaseName = null;
        queryMap = new HashMap();
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    protected void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public Map getQueryMap() {
        return queryMap;
    }

    public void setQueryMap(Map queryMap) {
        this.queryMap = queryMap;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
        try {
            if (dataSource != null) {
                HibernateInitializer hbnInitializer = CoreServices.lookup().getHibernateInitializer();
                DataSourceManager dataSourceManager = CoreServices.lookup().getDataSourceManager();
                DataSource ds = dataSourceManager.getDataSource(dataSource);
                dataBaseName = hbnInitializer.inferDatabaseName(ds);
            }
        } catch (Exception e) {
            log.error("Cannot get datasource named " + dataSource,e);
        }
    }

    public String getSQLQuery() {
        return getSQLQuery(dataBaseName);
    }

    public String getSQLQuery(String dataBaseName) {
        String query = null;
        if (dataBaseName != null) query = queryMap.get(dataBaseName);
        if (query != null) return query;
        return queryMap.get(PARAM_DEFAULT_QUERY);
    }

    public void setSQLQuery(String SQLQuery) {
        setSQLQuery(SQLQuery, dataBaseName);
    }

    public void setSQLQuery(String query, String dataBaseName) {
        queryMap.put(dataBaseName,query);
    }

    public boolean isReady() {
        return dataSource != null && getSQLQuery() != null;
    }

    public DataSet load(DataProvider provider) throws Exception {
        SQLDataSet newDs = new SQLDataSet(provider, this);
        newDs.load();
        return newDs;
    }
}
