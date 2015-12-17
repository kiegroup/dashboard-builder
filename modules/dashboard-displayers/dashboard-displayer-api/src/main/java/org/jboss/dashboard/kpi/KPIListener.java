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

import org.jboss.dashboard.commons.events.Subscriber;

/**
 * Interface designed for the capturing and processing of events related to a single KPI instance.
 */
public interface KPIListener extends Subscriber {

    /**
     * Invoked when a KPI is created.
     */
    void kpiCreated(KPI kpi);

    /**
     * Invoked when a KPI is saved.
     */
    void kpiSaved(KPI kpi);

    /**
     * Invoked when a KPI is about to be deleted.
     */
    void kpiDeleted(KPI kpi);
}
