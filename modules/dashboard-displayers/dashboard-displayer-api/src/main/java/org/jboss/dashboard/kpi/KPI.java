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
package org.jboss.dashboard.kpi;

import org.jboss.dashboard.displayer.DataDisplayer;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.database.Persistent;

import java.util.Locale;
import java.util.Map;

/**
 * A Key Performance Indicator is composed by two instances: 
 * <ul>
 * <li>A DataProvider which knows how to retrieve the data set to display.
 * <li>And a DataDisplayer which is responsible to render the graph and manage the UI issues.
 * </ul>
 */
public interface KPI extends Cloneable, Persistent {

    /**
     * The object identifier.
     */
    Long getId();
    void setId(Long newId);

    /**
     * The kpi code is an unique identifier that is universal and is not tied to the persistent storage.
     */
    String getCode();
    void setCode(String code);

    /**
     * The KPI descripton.
     */
    String getDescription(Locale l);
    void setDescription(String  descr, Locale l);

    /**
     * The localized descriptions.
     */
    void setDescriptionI18nMap(Map<String, String> descriptions);
    Map<String,String> getDescriptionI18nMap();

    /**
     * The KPI data diplayer.
     */
    DataDisplayer getDataDisplayer();
    void setDataDisplayer(DataDisplayer displayer);

    /**
     * The KPI data provider.
     */
    DataProvider getDataProvider();
    void setDataProvider(DataProvider provider);
}
