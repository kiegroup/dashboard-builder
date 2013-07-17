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
package org.jboss.dashboard.ui.components.csv;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.ui.components.DataProviderEditor;
import org.jboss.dashboard.provider.csv.CSVDataLoader;
import org.jboss.dashboard.commons.misc.Chronometer;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Locale;
import java.util.ResourceBundle;

public class CSVProviderEditor extends DataProviderEditor {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CSVProviderEditor.class.getName());

    private ResourceBundle messages;
    private boolean loadAttemptOk = false;

    protected int nrows;
    protected long elapsedTime;

    public static final String CSV_BUNDLE_PREFIX = "editor.csv.";

    public CSVProviderEditor() {
    }

    public CSVDataLoader getCSVDataLoader() {
        return (CSVDataLoader) dataProvider.getDataLoader();
    }

    public boolean isConfiguredOk() {
        try {
            return !StringUtils.isBlank(getCSVDataLoader().getFileURL()) &&
                   !StringUtils.isEmpty(getCSVDataLoader().getCsvSeparatedBy()) &&
                   !StringUtils.isBlank(getCSVDataLoader().getCsvQuoteChar()) &&
                   !StringUtils.isBlank(getCSVDataLoader().getCsvEscapeChar()) &&
                   !StringUtils.isBlank(getCSVDataLoader().getCsvDatePattern()) &&
                   !StringUtils.isBlank(getCSVDataLoader().getCsvNumberPattern()) &&
                   loadAttemptOk;
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
        String csvSeparatedBy = StringEscapeUtils.unescapeHtml(request.getRequestObject().getParameter("csvSeparatedBy"));
        String csvQuoteChar = StringEscapeUtils.unescapeHtml(request.getRequestObject().getParameter("csvQuoteChar"));
        String csvEscapeChar = StringEscapeUtils.unescapeHtml(request.getRequestObject().getParameter("csvEscapeChar"));
        String csvDatePattern = request.getRequestObject().getParameter("csvDatePattern");
        String csvNumberPattern = request.getRequestObject().getParameter("csvNumberPattern");
        String csvUrlFile = request.getRequestObject().getParameter("csvUrlFile");

        if (StringUtils.isBlank(csvUrlFile)) {
            throw new Exception( getErrorMessage("error1") );
        }
        if (StringUtils.isEmpty(csvSeparatedBy)) {
            throw new Exception( getErrorMessage("error2") );
        }
        if (StringUtils.isBlank(csvQuoteChar)) {
            throw new Exception( getErrorMessage("error6") );
        }
        if (StringUtils.isBlank(csvEscapeChar)) {
            throw new Exception( getErrorMessage("error7") );
        }
        if (StringUtils.isBlank(csvDatePattern)) {
            throw new Exception( getErrorMessage("error3") );
        }
        if (StringUtils.isBlank(csvNumberPattern)) {
            throw new Exception( getErrorMessage("error4") );
        }

        // Set the CSV file and try to load the new dataset.
        CSVDataLoader csvLoader = getCSVDataLoader();
        csvLoader.setCsvSeparatedBy(csvSeparatedBy);
        csvLoader.setCsvQuoteChar(csvQuoteChar);
        csvLoader.setCsvEscapeChar(csvEscapeChar);
        csvLoader.setCsvDatePattern(csvDatePattern);
        csvLoader.setCsvNumberPattern(csvNumberPattern);
        csvLoader.setFileURL(csvUrlFile);

        try {
            // Ensure data retrieved is refreshed.
            Chronometer crono = new Chronometer();
            crono.start();
            DataSet ds = dataProvider.refreshDataSet();
            crono.stop();
            elapsedTime = crono.elapsedTime();
            nrows = 0;
            if (ds != null && ds.getProperties().length > 0) nrows = ds.getRowCount();
            loadAttemptOk = true;
        } catch (Exception e) {
            throw new Exception(e.getMessage() != null ? e.getMessage() : getErrorMessage("error5") );
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
        if (messages == null || !messages.getLocale().equals(currentLocale)) messages = ResourceBundle.getBundle("org.jboss.dashboard.ui.components.csv.messages", currentLocale);
        String message = messages.getString(CSV_BUNDLE_PREFIX + key);
        return (message == null || "".equals(message)) ? null : message;
    }
}
