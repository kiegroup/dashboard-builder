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
package org.jboss.dashboard.provider.csv;

import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.dataset.csv.CSVDataSet;
import org.jboss.dashboard.provider.AbstractDataLoader;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.Application;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class CSVDataLoader extends AbstractDataLoader {

    protected String csvSeparatedBy;
    protected String csvQuoteChar;
    protected String csvEscapeChar;
    protected String csvDatePattern;
    protected String csvNumberPattern;
    protected String fileURL;
    protected File csvProviderFile;

    public boolean isReady() {
        return getFileURL() != null;
    }

    public DataSet load(DataProvider provider) throws Exception {
        if (StringUtils.isBlank(fileURL)) {
            throw new IllegalStateException("CSV file URL has not been specified.");
        }

        String filePath = calculateUrl(getFileURL());
        InputStream in = null;
        if (getFileURL().contains("$APPLICATION_DIR")) { // local file URL
            in = new FileInputStream(filePath.replace("file://", ""));
        } else {
            URL url = new URL(filePath);
            in = url.openStream();
        }

        return load(provider, in);
    }

    public DataSet load(DataProvider provider, InputStream is) throws Exception {
        CSVDataSet newDs = create(provider, is);
        newDs.load();
        return newDs;
    }

    public CSVDataSet create(DataProvider provider, InputStream in) throws Exception {
        File f = File.createTempFile("csv_temp.", ".csv");
        f.deleteOnExit();

        byte[] buf = new byte[512];
        int len;

        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(f);
            while ((len = in.read(buf)) != -1) {
                fout.write(buf, 0, len);
            }
        } finally {
            if (fout != null) fout.close();
            in.close();
        }

        setCsvProviderFile(f);
        return new CSVDataSet(provider, this);
    }

    public String getCsvSeparatedBy() {
        return csvSeparatedBy;
    }

    public void setCsvSeparatedBy(String csvSeparatedBy) {
        this.csvSeparatedBy = csvSeparatedBy;
    }

    public String getCsvQuoteChar() {
        return csvQuoteChar;
    }

    public void setCsvQuoteChar(String csvQuoteChar) {
        this.csvQuoteChar = csvQuoteChar;
    }

    public String getCsvEscapeChar() {
        return csvEscapeChar;
    }

    public void setCsvEscapeChar(String csvEscapeChar) {
        this.csvEscapeChar = csvEscapeChar;
    }

    public String getCsvDatePattern() {
        return csvDatePattern;
    }

    public void setCsvDatePattern(String csvDatePattern) {
        this.csvDatePattern = csvDatePattern;
    }

    public String getCsvNumberPattern() {
        return csvNumberPattern;
    }

    public void setCsvNumberPattern(String csvNumberPattern) {
        this.csvNumberPattern = csvNumberPattern;
    }

    public char getCsvNumberGroupSeparator() {
        if (csvNumberPattern.length() < 2) return ',';
        else return csvNumberPattern.charAt(1);
    }

    public char getCsvNumberDecimalSeparator() {
        if (csvNumberPattern.length() < 6) return '.';
        else return csvNumberPattern.charAt(5);
    }

    public File getCsvProviderFile() {
        return csvProviderFile;
    }

    public void setCsvProviderFile(File csvProviderFile) {
        this.csvProviderFile = csvProviderFile;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    protected String calculateUrl(String url){
        if(url == null) return null;
        return url.replace("$APPLICATION_DIR", String.valueOf(Application.lookup().getBaseAppDirectory()));
    }
}
