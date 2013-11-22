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

import java.util.Locale;
import java.util.ResourceBundle;

public class SQLProviderEditor extends DataProviderEditor {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SQLProviderEditor.class.getName());

    private ResourceBundle messages;
    private boolean loadAttemptOk = false;

    protected int nrows;
    protected long elapsedTime;

    /** The locale manager. */
    protected LocaleManager localeManager;

    public static final String SQL_BUNDLE_PREFIX = "editor.sql.";

    public SQLProviderEditor() {
        localeManager = LocaleManager.lookup();
    }

    public SQLDataLoader getSQLDataLoader() {
        return (SQLDataLoader) dataProvider.getDataLoader();
    }

    public boolean isConfiguredOk() {
        try {
            return loadAttemptOk && !StringUtils.isBlank(getSQLDataLoader().getSQLQuery());
        } catch (Exception e) {
            log.error("Error: ", e);
            return false;
        }
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public int getNrows() {
        return nrows;
    }

    public CommandResponse actionSubmit(CommandRequest request) throws Exception {
        loadAttemptOk = false;

        // Get the parameters
        String dataSource = request.getRequestObject().getParameter("dataSource");
        String sqlQuery = request.getRequestObject().getParameter("sqlQuery");

        if (StringUtils.isBlank(sqlQuery)) {
            throw new Exception( getErrorMessage("query.blank") );
        }

        // Set the SQL and try to load the new dataset.
        SQLDataLoader sqlLoader = getSQLDataLoader();

        // Ensure data retrieved is refreshed.
        try {
            if (dataSource != null) sqlLoader.setDataSource(dataSource);
            sqlLoader.setSQLQuery(sqlQuery);

            Chronometer crono = new Chronometer(); crono.start();
            DataSet ds = dataProvider.refreshDataSet();
            crono.stop();
            elapsedTime = crono.elapsedTime();
            nrows = 0;
            if (ds != null && ds.getProperties().length > 0) nrows = ds.getRowCount();
            loadAttemptOk = true;
        } catch (Exception e) {
            Throwable cause = ErrorManager.lookup().getRootCause(e);
            throw new Exception(!StringUtils.isBlank(cause.getMessage()) ? cause.getMessage() : getErrorMessage("query.error") );
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
        loadAttemptOk = false;
    }

    protected String getErrorMessage(String key) {
        if (key == null || "".equals(key)) return null;
        Locale currentLocale = LocaleManager.currentLocale();
        if (messages == null || !messages.getLocale().equals(currentLocale)) messages = localeManager.getBundle("org.jboss.dashboard.ui.components.sql.messages", currentLocale);
        String message = messages.getString(SQL_BUNDLE_PREFIX + key);
        return (message == null || "".equals(message)) ? null : message;
    }
}
