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
package org.jboss.dashboard.dataset.sql;

import org.jboss.dashboard.dataset.AbstractDataSet;
import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.provider.sql.SQLDataLoader;
import org.jboss.dashboard.provider.sql.SQLDataProperty;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.provider.DataFilter;
import org.jboss.dashboard.database.DataSourceFragment;

import java.sql.*;

/**
 * A data set implementation that holds as a matrix in-memory all the rows returned by a given SQL query executed
 * against a local or remote database.
 */
public class SQLDataSet extends AbstractDataSet {

    /**
     * Logger
     */
    private transient static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(SQLDataSet.class);

    /**
     * Where the data comes from.
     */
    protected String dataSource;

    /**
     * The SQL query that retrieves the data from the data source.
     */
    protected String sqlQuery;

    /**
     * The last executed SQL statement
     */
    protected transient SQLStatement lastExecutedStmt;

    public SQLDataSet(DataProvider provider, SQLDataLoader loader) {
        super(provider);
        this.dataSource = loader.getDataSource();
        this.sqlQuery = loader.getSQLQuery();
    }

    public SQLDataSet(DataProvider provider, String dataSource, String sqlQuery) {
        super(provider);
        this.dataSource = dataSource;
        this.sqlQuery = sqlQuery;
    }

    public String getDataSource() {
        return dataSource;
    }

    public String getSQLQuery() {
        return sqlQuery;
    }

    public SQLDataProperty createSQLProperty() {
        return new SQLDataProperty();
    }

    public SQLStatement createSQLStatament() throws Exception {
        return new SQLStatement(sqlQuery);
    }

    public void load() throws Exception {
        new DataSourceFragment(dataSource) {
        protected void fragment(Connection connection) throws Exception {
            // Execute the query.
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try {
                lastExecutedStmt = createSQLStatament();
                log.debug("Load data set from datasource=" + dataSource + " SQL=" + lastExecutedStmt.getSQLSentence());
                stmt = lastExecutedStmt.getPreparedStatement(connection);
                rs = stmt.executeQuery();

                // Get the properties definition.
                ResultSetMetaData meta = rs.getMetaData();
                int propsSize = meta.getColumnCount();
                SQLDataSet.this.setPropertySize(propsSize);
                for (int i = 0; i < propsSize; i++) {
                    SQLDataProperty dp = createSQLProperty();
                    dp.setPropertyId(meta.getColumnName(i + 1).toLowerCase());
                    dp.setType(meta.getColumnType(i + 1));
                    dp.setTableName(meta.getTableName(i + 1));
                    dp.setColumnName(meta.getColumnName(i + 1));
                    addProperty(dp, i);
                }

                // Get rows and populate the data set values.
                while (rs.next()) {
                    Object[] row = new Object[propsSize];
                    for (int i = 0; i < propsSize; i++) row[i] = rs.getObject(i + 1);
                    SQLDataSet.this.addRowValues(row);
                }

                // Once we got the dataset initialized then calculate the domain for each property.
                for (int i = 0; i < properties.length; i++) {
                    SQLDataProperty property = (SQLDataProperty) properties[i];
                    property.calculateDomain();
                }
            }
            catch (Exception e) {
                if (lastExecutedStmt != null) {
                    log.error("Error in load() SQLDataset. SQL = " + lastExecutedStmt.getSQLSentence(), e);
                }
                throw e;
            }
            finally {
                try {
                    if (rs != null) rs.close();
                } catch (Exception e) {
                    log.warn("Error closing ResultSet: ", e);
                }
                try {
                    if (stmt != null) stmt.close();
                } catch (Exception e) {
                    log.warn("Error closing PreparedStatement: ", e);
                }
            }
        }}.execute();
    }

    public DataSet filter(DataFilter filter) throws Exception {
        // Check if the SQL changes when filtering is requested. If so then reload the full dataset.
        SQLStatement currentStatement = createSQLStatament();
        if (!lastExecutedStmt.equals(currentStatement)) {
            SQLDataSet sqlDataSet = new SQLDataSet(provider, dataSource, sqlQuery);
            sqlDataSet.load();

            // Apply the in-memory filter in order to cover all the filter properties non specified as SQL conditions.
            DataSet result = sqlDataSet.filter(filter);

            // If the in-memory filter applies then return it.
            if (result != null) return result;
            return sqlDataSet;
        }
        // Apply the in-memory filter by default.
        return super.filter(filter);
    }
}
