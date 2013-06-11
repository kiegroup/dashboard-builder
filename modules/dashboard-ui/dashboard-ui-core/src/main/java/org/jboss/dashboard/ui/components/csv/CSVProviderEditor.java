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

import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.ui.components.DataProviderEditor;
import org.jboss.dashboard.provider.csv.CSVDataLoader;
import org.jboss.dashboard.commons.misc.Chronometer;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

public class CSVProviderEditor extends DataProviderEditor {

    protected int nrows;
    protected long elapsedTime;
    protected String loadError;

    public CSVProviderEditor() {
    }

    public CSVDataLoader getCSVDataLoader() {
        return (CSVDataLoader) dataProvider.getDataLoader();
    }

    public boolean isConfiguredOk() {
        try {
            return getLoadError() == null;
        } catch (Exception e) {
            return false;
        }
    }

    public String getLoadError() {
        return loadError;
    }

    public void setLoadError(String queryError) {
        this.loadError = queryError;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public int getNrows() {
        return nrows;
    }

    public CommandResponse actionSubmit(CommandRequest request) throws Exception {
        // Get the parameters
        String csvSeparatedBy = StringEscapeUtils.unescapeHtml(request.getRequestObject().getParameter("csvSeparatedBy"));
        String csvQuoteChar = StringEscapeUtils.unescapeHtml(request.getRequestObject().getParameter("csvQuoteChar"));
        String csvEscapeChar = StringEscapeUtils.unescapeHtml(request.getRequestObject().getParameter("csvEscapeChar"));
        String csvDatePattern = request.getRequestObject().getParameter("csvDatePattern");
        String csvNumberPattern = request.getRequestObject().getParameter("csvNumberPattern");
        String csvUrlFile = request.getRequestObject().getParameter("csvUrlFile");

        if (StringUtils.isEmpty(csvUrlFile)) {
            setLoadError("error1");
            return null;
        }
        if (StringUtils.isEmpty(csvSeparatedBy)) {
            setLoadError("error2");
            return null;
        }
        if (StringUtils.isEmpty(csvQuoteChar)) {
            setLoadError("error6");
            return null;
        }
        if (StringUtils.isEmpty(csvEscapeChar)) {
            setLoadError("error7");
            return null;
        }
        if (StringUtils.isEmpty(csvDatePattern)) {
            setLoadError("error3");
            return null;
        }
        if (StringUtils.isEmpty(csvNumberPattern)) {
            setLoadError("error4");
            return null;
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
            // Clear previous errors
            setLoadError(null);

            // Ensure data retrieved is refreshed.
            Chronometer crono = new Chronometer();
            crono.start();
            DataSet ds = dataProvider.refreshDataSet();
            crono.stop();
            elapsedTime = crono.elapsedTime();
            nrows = 0;
            if (ds != null && ds.getProperties().length > 0) nrows = ds.getRowCount();

        } catch (Exception e) {
            setLoadError(e.getMessage() != null ? e.getMessage() : "error5");
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
        loadError = null;
    }
}
