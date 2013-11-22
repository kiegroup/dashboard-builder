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
package org.jboss.dashboard.provider.sql;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.annotation.Install;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.export.DataLoaderXMLFormat;
import org.jboss.dashboard.provider.DataLoader;
import org.jboss.dashboard.provider.DataProviderType;

import javax.inject.Inject;
import java.util.Locale;
import java.util.ResourceBundle;

@Install
public class SQLDataProviderType implements DataProviderType {

    public static final String UID = "sql";

    @Inject @Config(UID)
    protected String uid;

    @Inject
    protected LocaleManager localeManager;

    protected SQLDataLoaderXMLFormat xmlFormat;

    public SQLDataProviderType() {
        xmlFormat = new SQLDataLoaderXMLFormat();
    }

    public String getUid() {
        return uid;
    }

    public DataLoaderXMLFormat getXmlFormat() {
        return xmlFormat;
    }

    public String getDescription(Locale l) {
        ResourceBundle i18n = localeManager.getBundle("org.jboss.dashboard.provider.messages", l);
        return i18n.getString("provider.sql.description");
    }

    public DataLoader createDataLoader() {
        SQLDataLoader loader  = new SQLDataLoader();
        loader.setDataProviderType(this);
        return loader;
    }
}
