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

import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DataSourceManagementFormFormatter extends Formatter {

    @Inject
    private transient Logger log;

    @Inject
    private DataSourceManagementHandler dataSourceManagementHandler;

    @Override
    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {

            if (dataSourceManagementHandler.isEDIT_MODE()) {
                // Editing DS.
                setAttribute("dsName", dataSourceManagementHandler.getName() != null ? StringEscapeUtils.ESCAPE_HTML4.translate(dataSourceManagementHandler.getName()) : "");
                renderFragment("outputStartEditing");
            }
            renderFragment("outputStart");
            if (dataSourceManagementHandler.getTEST_MODE() && dataSourceManagementHandler.getTEST_RESULT()!=null && !"".equals(dataSourceManagementHandler.getTEST_RESULT())){
                setAttribute("testResult", dataSourceManagementHandler.getTEST_RESULT());
                renderFragment("outputResult");
            }

            if(!dataSourceManagementHandler.isEDIT_MODE() && dataSourceManagementHandler.isCreating()){
                setAttribute("error", (dataSourceManagementHandler.hasError("jndi") || dataSourceManagementHandler.hasError("jdbc")));
                setAttribute("type", dataSourceManagementHandler.getType());
                renderFragment("outputRadios");
            }

            setAttribute("errorName", dataSourceManagementHandler.hasError("name"));
            setAttribute("Name", dataSourceManagementHandler.getName() != null ? StringEscapeUtils.ESCAPE_HTML4.translate(dataSourceManagementHandler.getName()) : "");

            renderFragment("outputName");

            setAttribute("errorJndiPath", dataSourceManagementHandler.hasError("jndiPath"));

            if (dataSourceManagementHandler.getType() != null && dataSourceManagementHandler.getType().equals(DataSourceManagementHandler.JNDI_TYPE)) {
                setAttribute("JndiPath", dataSourceManagementHandler.getJndiPath() != null ? StringEscapeUtils.ESCAPE_HTML4.translate(dataSourceManagementHandler.getJndiPath()) : "");
            }

            renderFragment("outputJNDI");

            setAttribute("errorUrl", dataSourceManagementHandler.hasError("url"));
            setAttribute("errorDriverClass", dataSourceManagementHandler.hasError("driverClass"));
            setAttribute("errorUserName", dataSourceManagementHandler.hasError("userName"));
            setAttribute("errorPassw", dataSourceManagementHandler.hasError("password"));
            if (dataSourceManagementHandler.getType() != null && dataSourceManagementHandler.getType().equals(DataSourceManagementHandler.CUSTOM_TYPE)) {

                setAttribute("Url", dataSourceManagementHandler.getUrl() != null ? StringEscapeUtils.ESCAPE_HTML4.translate(dataSourceManagementHandler.getUrl()) : "");

                String driverClass = dataSourceManagementHandler.getDriverClass();
                if (driverClass != null) {
                    setAttribute("selectedNone", driverClass.equals("") ? "selected" : "");
                    setAttribute("selectedDB2", driverClass.equals("com.ibm.db2.jcc.DB2Driver") ? "selected" : "");
                    setAttribute("selectedH2", driverClass.equals("org.h2.Driver") ? "selected" : "");
                    setAttribute("selectedMySQL", driverClass.equals("com.mysql.jdbc.Driver") ? "selected" : "");
                    setAttribute("selectedOracle", driverClass.equals("oracle.jdbc.driver.OracleDriver") ? "selected": "");
                    setAttribute("selectedPostgres", driverClass.equals("org.postgresql.Driver") ? "selected" : "");
                    setAttribute("selectedSQLServer", driverClass.equals("com.microsoft.sqlserver.jdbc.SQLServerDriver") ? "selected" : "");
                    setAttribute("selectedSybase", driverClass.equals("com.sybase.jdbc4.jdbc.SybDriver") ? "selected" : "");
                    setAttribute("selectedTeiid", driverClass.equals("org.teiid.jdbc.TeiidDriver") ? "selected" : "");
                }
                setAttribute("DriverClassName", driverClass != null ? StringEscapeUtils.ESCAPE_HTML4.translate(driverClass) : "");

                setAttribute("UserName", dataSourceManagementHandler.getUserName() != null ? StringEscapeUtils.ESCAPE_HTML4.translate(dataSourceManagementHandler.getUserName()) : "");
                setAttribute("Passw", dataSourceManagementHandler.getPassword() != null ? StringEscapeUtils.ESCAPE_HTML4.translate(dataSourceManagementHandler.getPassword()) : "");
            }
            renderFragment("outputLocal");

            setAttribute("errorTestQ", dataSourceManagementHandler.hasError("testQuery"));
            setAttribute("TestQ", dataSourceManagementHandler.getTestQuery() != null ? StringEscapeUtils.ESCAPE_HTML4.translate(dataSourceManagementHandler.getTestQuery()) : "");

            renderFragment("ouputTestQ");
            if (dataSourceManagementHandler.isCreating() || dataSourceManagementHandler.isEDIT_MODE()) {
                renderFragment("outputRowButtonsBegin");
                renderFragment("outputTryButton");
                renderFragment("outputSubmitButton");

                if (!dataSourceManagementHandler.getINTROSPECT_MODE())
                    renderFragment("outputCancel");
                renderFragment("outputRowButtonsEnd");
            }
            setAttribute("typeSelect", dataSourceManagementHandler.getType() != null ? dataSourceManagementHandler.getType() : "");
            setAttribute("introspectMode", dataSourceManagementHandler.getINTROSPECT_MODE());
            renderFragment("outputEnd");

        } catch (Exception e) {
            log.error("Error rendering FormManagement: ", e);
        }
    }
}
