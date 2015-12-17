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
package org.jboss.dashboard.kpi;

import org.jboss.dashboard.commons.events.Publisher;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manager class that it allows for the retrieval of KPI instances as well as DataProvider's.
 */
public interface KPIManager {

    int EVENT_ALL         = Publisher.EVENT_ALL;
    int EVENT_KPI_CREATED = 1;
    int EVENT_KPI_SAVED   = 2;
    int EVENT_KPI_DELETED = 3;

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
    Set<KPI> getAllKPIs() throws Exception;

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

    // Event handling

    /**
     * Retrieve registered kpi listeners.
     * @return A map of eventId / eventListeners.
     */
    Map<Integer,List<KPIListener>> getKPIListenerMap();

    /**
     * Retrieve registered kpi listeners for a specified event.
     * @param eventId The event being listened.
     */
    List<KPIListener> getKPIListeners(int eventId);

    /**
     * Register a listener interested in all process events.
     *
     * <p>WARNING: In order to solve the -<i>lapsed listener</i>- problem the reference to the listener stored by the manager is weak.
     * This means that if the listener reference is lost then is also automatically removed from the listeners list.
     * To avoid this issue ensure that your listener instance is referenced in your object model
     * and exists as long as the manager is alive.
     *
     * @param kpiListener Be sure your listener instance added is referred by another object
     * instance in order to avoid garbage collector to finalize your listener unexpectedly.
     * @param eventId The event interested in.
     */
    void addKPIListener(KPIListener kpiListener, int eventId);

    /**
     * Removes a registered listener.
     *
     * @param eventId The event listener was interested in.
     */
    void removeKPIListener(KPIListener kpiListener, int eventId);

    /**
     * Removes a registered listener.
     */
    void removeKPIListener(KPIListener kpiListener);

    /**
     * Notify the specified event to the registered listeners.
     */
    void notifyKPIListener(int eventId, KPI kpi);
}

