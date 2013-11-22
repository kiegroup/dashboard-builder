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
package org.jboss.dashboard.ui.panel.dataSourceManagement;

import org.jboss.dashboard.CoreServices;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.jboss.dashboard.database.DataSourceEntry;
import org.jboss.dashboard.database.JDBCDataSourceEntry;
import org.jboss.dashboard.database.JNDIDataSourceEntry;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.ResourceBundle;

public class DataSourceManagementFormatter extends Formatter {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataSourceManagementFormatter.class.getName());

    private DataSourceManagementHandler dataSourceManagementHandler;
    public static final String JDBC_DATA_SOURCE_ENTRY = "JDBCDataSourceEntry";
    public static final String JNDI_DATA_SOURCE_ENTRY = "JNDIDataSourceEntry";

    /** The locale manager. */
    protected LocaleManager localeManager;

    public DataSourceManagementFormatter() {
        localeManager = LocaleManager.lookup();
    }

    public DataSourceManagementHandler getDataSourceManagementHandler() {
        return dataSourceManagementHandler;
    }

    public void setDataSourceManagementHandler(DataSourceManagementHandler dataSourceManagementHandler) {
        this.dataSourceManagementHandler = dataSourceManagementHandler;
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {
            boolean isEditing = getDataSourceManagementHandler().isEDIT_MODE();
            boolean isCreating = getDataSourceManagementHandler().isCreating();
            List existingDataSources = dataSourceManagementHandler.getDataSourceManager().getDataSourceEntries();
            renderFragment("outputStart");

            // If editing cannot create new DS.
            if (!isEditing) {
                if (isCreating) renderFragment("outputCreatingNewDS");
                else renderFragment("outputNewDS");
            }

            // If creating new DS o editing one render form and not render avaliable DS table.
            if (isCreating || isEditing) renderFragment("outputDatasourceForm");
            else {
                // Render avaliable DS table.
                renderFragment("outputStartTable");
                renderFragment("outputDataSource");
                for (int i = 0; i < existingDataSources.size(); i++) {
                    DataSourceEntry entry = (DataSourceEntry) existingDataSources.get(i);

                    if (getDataSourceManagementHandler().isEDIT_MODE() && entry.getName().equals(getDataSourceManagementHandler().getDS_EDIT()))
                    {
                        setAttribute("selected", new Boolean(true));
                    } else {
                        setAttribute("selected", new Boolean(false));
                    }
                    setAttribute("dataSName", entry.getName());
                    setAttribute("entry", entry);
                    setAttribute("Name", StringEscapeUtils.escapeHtml(entry.getName()));

                    setAttribute("index", i);
                    if (entry instanceof JNDIDataSourceEntry) {
                        setAttribute("entryType", JNDI_DATA_SOURCE_ENTRY);
                        setAttribute("entryPath", StringEscapeUtils.escapeHtml(entry.getJndiPath()));
                    } else if (entry instanceof JDBCDataSourceEntry) {
                        setAttribute("entryType", JDBC_DATA_SOURCE_ENTRY);
                        setAttribute("entryPath", StringEscapeUtils.escapeHtml(entry.getUrl()));
                    }

                    setAttribute("statusIcon", checkDataSource(entry));

                    renderFragment("output");
                }
                renderFragment("outputEndTable");
            }
            renderFragment("outputEnd");
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    protected String checkDataSource(DataSourceEntry entry) {
        try {
            DataSource ds = getDataSourceManagementHandler().getDataSourceManager().getDataSource(entry.getName());
            if (!getDataSourceManagementHandler().getDataSourceManager().checkDriverClassAvailable(entry.getDriverClass())) {
                ResourceBundle i18n = localeManager.getBundle("org.jboss.dashboard.ui.panel.dataSourceManagement.messages", LocaleManager.currentLocale());
                return i18n.getString("datasource.driver.na");
            }
            if (ds == null) return "DataSource null";

            Connection conn = null;
            try {
                conn = ds.getConnection();
                if (conn != null) {
                    Statement s = conn.createStatement();
                    ResultSet rs = s.executeQuery(entry.getTestQuery());
                    rs.close();
                    s.close();
                    return DataSourceManagementHandler.RESULT_OK;
                }
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
            return "";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
