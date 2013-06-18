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

/**
 * Adapter class that offers dummy implementations of the KPIListener interface.
 * This by-default implementation can be used by those classes that want to implement only a part of the interface.
 */
public class KPIListenerAdapter implements KPIListener {

    public int getTransactionContext() {
        return TXCONTEXT_DEFAULT;
    }

    public void notifyEvent(int eventId, Object eventInfo) {
        switch (eventId) {
            case KPIManager.EVENT_KPI_CREATED:   kpiCreated((KPI) eventInfo); break;
            case KPIManager.EVENT_KPI_SAVED:   kpiSaved((KPI) eventInfo); break;
            case KPIManager.EVENT_KPI_DELETED:   kpiDeleted((KPI) eventInfo); break;
            default: throw new IllegalArgumentException("Event id. unrecognized: " + eventId);
        }
    }

    public void kpiCreated(KPI kpi) {

    }

    public void kpiSaved(KPI kpi) {

    }

    public void kpiDeleted(KPI kpi) {

    }
}
