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
package org.jboss.dashboard.ui.panel.dataSourceManagement;

import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * Data source tables manager
 */
@ApplicationScoped
public class DataSourceTableManager {

    /**
     * Get the singleton instance.
     */
    public static DataSourceTableManager lookup() {
        return CDIBeanLocator.getBeanByType(DataSourceTableManager.class);
    }

     public List<DataSourceTableEntry> getSelectedTablesEntries(final String datasource) throws Exception {
        final List<DataSourceTableEntry> existingEntries = new ArrayList<DataSourceTableEntry>();
        new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                Query query = session.createQuery(" from " + DataSourceTableEntry.class.getName() + " entry where entry.datasource = :datasource");
                query.setString("datasource", datasource);
                query.setCacheable(true);
                FlushMode oldFlushMode = session.getFlushMode();
                session.setFlushMode(FlushMode.COMMIT);
                existingEntries.addAll(query.list());
                session.setFlushMode(oldFlushMode);
            }
        }.execute();
        return existingEntries;
    }

    public List<String> getSelectedTablesName(String datasource) throws Exception {
        ArrayList<String> results = new ArrayList<String>();
        for (DataSourceTableEntry tableEntry : getSelectedTablesEntries(datasource)) {
            String tableName = tableEntry.getName();
            results.add(tableName);
        }
        return results;
    }

    public List<DataSourceColumnEntry> getSelectedColumnsEntries(final String datasource, final String tableName) throws Exception {
        final List<DataSourceColumnEntry> existingEntries = new ArrayList<DataSourceColumnEntry>();
        new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                Query query = session.createQuery(" from " + DataSourceColumnEntry.class.getName() + " entry where entry.datasource = :datasource and entry.tableName = :tableName");
                query.setString("datasource", datasource);
                query.setString("tableName", tableName);
                FlushMode oldFlushMode = session.getFlushMode();
                session.setFlushMode(FlushMode.NEVER);
                query.setCacheable(true);
                existingEntries.addAll(query.list());
                session.setFlushMode(oldFlushMode);
            }
        }.execute();
        return existingEntries;
    }
}