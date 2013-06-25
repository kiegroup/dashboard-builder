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
package org.jboss.dashboard.ui.components.sql;

import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.error.ErrorManager;
import org.jboss.dashboard.ui.components.DataProviderEditor;
import org.jboss.dashboard.provider.sql.SQLDataLoader;
import org.jboss.dashboard.commons.misc.Chronometer;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.CommandRequest;

import java.util.ResourceBundle;

public class SQLProviderEditor extends DataProviderEditor {

    protected int nrows;
    protected long elapsedTime;
    protected String queryError;

    public SQLProviderEditor() {
    }

    public SQLDataLoader getSQLDataLoader() {
        return (SQLDataLoader) dataProvider.getDataLoader();
    }

    public boolean isConfiguredOk() {
        try {
            return !StringUtils.isBlank(getSQLDataLoader().getSQLQuery()) && getQueryError() == null;
        } catch (Exception e) {
            return false;
        }
    }

    public String getQueryError() {
        return queryError;
    }

    public void setQueryError(String queryError) {
        this.queryError = queryError;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public int getNrows() {
        return nrows;
    }

    public CommandResponse actionSubmit(CommandRequest request) throws Exception {
        // Get the parameters
        String dataSource = request.getRequestObject().getParameter("dataSource");
        String sqlQuery = request.getRequestObject().getParameter("sqlQuery");

        // Clear previous errors
        setQueryError(null);

        // Set the SQL and try to load the new dataset.
        SQLDataLoader sqlLoader = getSQLDataLoader();
        if (dataSource != null) sqlLoader.setDataSource(dataSource);
        if (!StringUtils.isBlank(sqlQuery)) sqlLoader.setSQLQuery(sqlQuery);
        else {
            ResourceBundle i18n = ResourceBundle.getBundle("org.jboss.dashboard.ui.components.sql.messages", LocaleManager.currentLocale());
            setQueryError(i18n.getString("editor.query.blank"));
            return null;
        }

        // Ensure data retrieved is refreshed.
        if (isConfiguredOk()) {
            try {
                Chronometer crono = new Chronometer(); crono.start();
                DataSet ds = dataProvider.refreshDataSet();
                crono.stop();
                elapsedTime = crono.elapsedTime();
                nrows = 0;
                if (ds != null && ds.getProperties().length > 0) nrows = ds.getRowCount();
            } catch (Exception e) {
                Throwable cause = ErrorManager.lookup().getRootCause(e);
                setQueryError(!StringUtils.isBlank(cause.getMessage()) ? cause.getMessage() : "Unexpected error");
            }
        }
        return null;
    }

    public CommandResponse actionCancel(CommandRequest request) throws Exception {
        clear();
        return null;
    }

    public void clear() {
        super.clear();
        nrows = 0;
        elapsedTime = 0;
        queryError = null;
    }
}
