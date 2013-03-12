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
package org.jboss.dashboard.provider.csv;

import org.jboss.dashboard.annotation.Install;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.export.DataLoaderXMLFormat;
import org.jboss.dashboard.provider.DataLoader;
import org.jboss.dashboard.provider.DataProviderType;
import javax.inject.Inject;
import java.util.Locale;
import java.util.ResourceBundle;

@Install
public class CSVDataProviderType implements DataProviderType {

    public static final String UID = "csv";

    @Inject @Config(UID)
    protected String uid;

    @Inject @Config(";")
    protected String csvSeparatedBy;

    @Inject @Config("\"")
    protected String csvQuoteChar;

    @Inject @Config("\\")
    protected String csvEscapeChar;

    @Inject @Config("MM-dd-yyyy HH:mm:ss")
    protected String csvDatePattern;

    @Inject @Config("#,###.##")
    protected String csvNumberPattern;

    protected CSVDataLoaderXMLFormat xmlFormat;

    public CSVDataProviderType() {
        xmlFormat = new CSVDataLoaderXMLFormat();
    }

    public String getUid() {
        return uid;
    }

    public DataLoaderXMLFormat getXmlFormat() {
        return xmlFormat;
    }

    public String getDescription(Locale l) {
        ResourceBundle i18n = ResourceBundle.getBundle("org.jboss.dashboard.provider.messages", l);
        return i18n.getString("provider.csv.description");
    }

    public DataLoader createDataLoader() {
        CSVDataLoader loader = new CSVDataLoader();
        loader.setCsvSeparatedBy(csvSeparatedBy);
        loader.setCsvQuoteChar(csvQuoteChar);
        loader.setCsvEscapeChar(csvEscapeChar);
        loader.setCsvDatePattern(csvDatePattern);
        loader.setCsvNumberPattern(csvNumberPattern);
        loader.setDataProviderType(this);
        return loader;
    }
}