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

import java.util.List;
import java.util.Set;

/**
 * Manager class that it allows for the retrieval of KPI instances as well as DataProvider's.
 */
public interface KPIManager {

    /**
     * Factory method for the creation of a KPI brand new instance.
     */
    KPI createKPI();

    /**
     * Get all KPIs defined.
     * @link aggregationByValue
     * @associates <{DataDisplayer}>
     * @supplierCardinality 0..*
     */
    Set getAllKPIs() throws Exception;

    /**
     * Get a persistent KPI by its identifier.
     */
    KPI getKPIById(Long id) throws Exception;

    /**
     * Get a persistent KPI by its universal code.
     */
    KPI getKPIByCode(String code) throws Exception;

    /**
     * Sort a list of properties by name.
     */
    void sortKPIsByDescription(List<KPI> propList, boolean ascending);
}

