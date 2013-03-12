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
package org.jboss.dashboard.provider;

import org.jboss.dashboard.dataset.DataSet;
import org.jboss.dashboard.domain.Domain;

import java.util.Locale;
import java.util.List;
import java.util.Map;

/**
 * A data set is composed by a fixed number of properties (columns).
 * A data property belongs to a data set and is tied to an specified domain.
 *
 * @see org.jboss.dashboard.dataset.DataSet
 * @see org.jboss.dashboard.domain.Domain
 */
public interface DataProperty extends Cloneable {

    String getPropertyId();
    void setPropertyId(String id);
    String getName(Locale l);
    void setName(String name, Locale l);
    Map getNameI18nMap();
    void setNameI18nMap(Map nameI18nMap);

    Domain getDomain();
    void setDomain(Domain domain);
    DataSet getDataSet();
    void setDataSet(DataSet dataSet);
    List getValues();
    DataProperty cloneProperty();
}
