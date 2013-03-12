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

import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DataSourceManagementFormFormatter extends Formatter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(DataSourceManagementFormFormatter.class.getName());

    private DataSourceManagementHandler dataSourceManagementHandler;

    public DataSourceManagementHandler getDataSourceManagementHandler() {
        return dataSourceManagementHandler;
    }

    public void setDataSourceManagementHandler(DataSourceManagementHandler dataSourceManagementHandler) {
        this.dataSourceManagementHandler = dataSourceManagementHandler;
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {

            if (getDataSourceManagementHandler().isEDIT_MODE()) {
                // Editing DS.
                setAttribute("dsName", getDataSourceManagementHandler().getName());
                renderFragment("outputStartEditing");
            }
            renderFragment("outputStart");
            if (getDataSourceManagementHandler().getTEST_MODE() && getDataSourceManagementHandler().getTEST_RESULT()!=null && !"".equals(getDataSourceManagementHandler().getTEST_RESULT())){
                setAttribute("testResult", getDataSourceManagementHandler().getTEST_RESULT());
                renderFragment("outputResult");
            }

            if(!getDataSourceManagementHandler().isEDIT_MODE() && getDataSourceManagementHandler().isCreating()){
                setAttribute("error", (getDataSourceManagementHandler().hasError("jndi") || getDataSourceManagementHandler().hasError("jdbc")));
                setAttribute("type", getDataSourceManagementHandler().getType());
                renderFragment("outputRadios");
            }

            setAttribute("errorName", getDataSourceManagementHandler().hasError("name"));
            setAttribute("Name", getDataSourceManagementHandler().getName() != null ? StringEscapeUtils.escapeHtml(getDataSourceManagementHandler().getName()) : "");

            renderFragment("outputName");

            setAttribute("errorJndiPath", getDataSourceManagementHandler().hasError("jndiPath"));

            if (getDataSourceManagementHandler().getType() != null && getDataSourceManagementHandler().getType().equals(DataSourceManagementHandler.JNDI_TYPE)) {
                setAttribute("JndiPath", getDataSourceManagementHandler().getJndiPath() != null ? StringEscapeUtils.escapeHtml(getDataSourceManagementHandler().getJndiPath()) : "");
            }

            renderFragment("outputJNDI");

            setAttribute("errorUrl", getDataSourceManagementHandler().hasError("url"));
            setAttribute("errorDriverClass", getDataSourceManagementHandler().hasError("driverClass"));
            setAttribute("errorUserName", getDataSourceManagementHandler().hasError("userName"));
            setAttribute("errorPassw", getDataSourceManagementHandler().hasError("password"));
            if (getDataSourceManagementHandler().getType() != null && getDataSourceManagementHandler().getType().equals(DataSourceManagementHandler.CUSTOM_TYPE)) {

                setAttribute("Url", getDataSourceManagementHandler().getUrl() != null ? StringEscapeUtils.escapeHtml(getDataSourceManagementHandler().getUrl()) : "");

                String driverClass = getDataSourceManagementHandler().getDriverClass();
                if (driverClass != null) {
                    setAttribute("selectedNone", "".equals(getDataSourceManagementHandler().getDriverClass()) ? "selected" : "");
                    setAttribute("selectedMySQL", getDataSourceManagementHandler().getDriverClass().equals("com.mysql.jdbc.Driver") ? "selected" : "");
                    setAttribute("selectedPostgres", getDataSourceManagementHandler().getDriverClass().equals("org.postgresql.Driver") ? "selected" : "");
                    setAttribute("selectedOracle", getDataSourceManagementHandler().getDriverClass().equals("oracle.jdbc.driver.OracleDriver") ? "selected" : "");
                    setAttribute("selectedSQLServer", getDataSourceManagementHandler().getDriverClass().equals("com.microsoft.sqlserver.jdbc.SQLServerDriver") ? "selected" : "");
                    setAttribute("selectedH2", getDataSourceManagementHandler().getDriverClass().equals("org.h2.Driver") ? "selected" : "");
                }
                setAttribute("DriverClassName", driverClass != null ? StringEscapeUtils.escapeHtml(driverClass) : "");

                setAttribute("UserName", getDataSourceManagementHandler().getUserName() != null ? StringEscapeUtils.escapeHtml(getDataSourceManagementHandler().getUserName()) : "");
                setAttribute("Passw", getDataSourceManagementHandler().getPassword() != null ? StringEscapeUtils.escapeHtml(getDataSourceManagementHandler().getPassword()) : "");
            }
            renderFragment("outputLocal");

            setAttribute("errorTestQ", getDataSourceManagementHandler().hasError("testQuery"));
            setAttribute("TestQ", getDataSourceManagementHandler().getTestQuery() != null ? StringEscapeUtils.escapeHtml(getDataSourceManagementHandler().getTestQuery()) : "");

            renderFragment("ouputTestQ");
            if (getDataSourceManagementHandler().isCreating() || getDataSourceManagementHandler().isEDIT_MODE()) {
                renderFragment("outputRowButtonsBegin");
                renderFragment("outputTryButton");
//                if (getDataSourceManagementHandler().getEDIT_MODE())
//                    renderFragment("outputIntrospectButton");
                renderFragment("outputSubmitButton");

                if (!getDataSourceManagementHandler().getINTROSPECT_MODE())
                    renderFragment("outputCancel");
                renderFragment("outputRowButtonsEnd");
            }
            setAttribute("typeSelect", getDataSourceManagementHandler().getType() != null ? getDataSourceManagementHandler().getType() : "");
            setAttribute("introspectMode", getDataSourceManagementHandler().getINTROSPECT_MODE());
            renderFragment("outputEnd");

        } catch (Exception e) {
            log.error("Error rendering FormManagement: ", e);
        }
    }
}
