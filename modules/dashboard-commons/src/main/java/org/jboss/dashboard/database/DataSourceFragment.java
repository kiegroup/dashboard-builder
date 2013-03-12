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

import org.jboss.dashboard.CoreServices;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.hibernate.Session;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * A component to be used when access to remote databases is needed. Sample usage:<br/>
 * <pre>new DataSourceFragment("&lt;remoteDatasourceName&gt;") {
 * protected void fragment(Connection conn) throws Exception {
 *    Statement stmt = conn.createStatement();
 *    ...
 * }}.execute();</pre>

 */
public abstract class DataSourceFragment {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(DataSourceFragment.class.getName());

    private String dataSourceName;

    /**
     * Create a DataSourceFragment to perform queries against given datasource
     *
     * @param dataSourceName datasource name to use
     */
    public DataSourceFragment(String dataSourceName) {
        if (dataSourceName == null)
            throw new NullPointerException();
        this.dataSourceName = dataSourceName;
    }

    /**
     * Get the datasource this fragment is using
     *
     * @return the datasource this fragment is using
     * @throws Exception
     */
    protected DataSource getDataSource() throws Exception {
        DataSourceManager dsMgr = CoreServices.lookup().getDataSourceManager();
        return dsMgr.getDatasource(dataSourceName);
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    /**
     * Executes this fragment.
     *
     * @throws Exception
     */
    public void execute() throws Exception {
        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            DataSource dataSource = getDataSource();
            if (dataSource == null) {
                noDataSource();
            } else {
                Connection conn = dataSource.getConnection();
                fragment(conn);
            }
        }}.execute();
    }

    /**
     * Called when the datasource exists. Subclasses need to overwrite it to make their own
     * queries here. The connection must be left open. It will be closed by the fragment.
     *
     * @param conn a Connection from the datasource.
     * @throws Exception
     */
    protected abstract void fragment(Connection conn) throws Exception;

    /**
     * Called instead of fragment() when the datasource is invalid. Default implementation prints an
     * error message. Custom subclasses may generate custom error messages.
     */
    protected void noDataSource() {
        log.error("There is no datasource with name " + dataSourceName + ". Ignoring fragment.");
    }
}
