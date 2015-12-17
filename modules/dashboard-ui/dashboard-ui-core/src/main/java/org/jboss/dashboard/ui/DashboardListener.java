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
package org.jboss.dashboard.ui;

import org.jboss.dashboard.commons.events.Subscriber;

/**
 * Interface for the listening of dashboard events such as "onDrillDown".
 */
public class DashboardListener implements Subscriber {

    public static final int EVENT_DRILL_DOWN = 0;
    public static final int EVENT_DRILL_UP = 1;

    public int getTransactionContext() {
        return TXCONTEXT_DEFAULT;
    }

    public void notifyEvent(int eventId, Object eventInfo) {
        switch (eventId) {
            case EVENT_DRILL_DOWN: drillDownPerformed((Dashboard)((Object[])eventInfo)[0], (Dashboard)(((Object[])eventInfo)[1])); break;
            case EVENT_DRILL_UP: drillUpPerformed((Dashboard)((Object[])eventInfo)[0], (Dashboard)(((Object[])eventInfo)[1])); break;
            default: throw new IllegalArgumentException("Event id. unrecognized: " + eventId);
        }
    }

    public void drillDownPerformed(Dashboard parent, Dashboard child) {
    }

    public void drillUpPerformed(Dashboard parent, Dashboard child) {        
    }
}
