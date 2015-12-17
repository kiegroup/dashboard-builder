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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;

/**
 * Class that defines some dashboard UI-related settings.
 */
@ApplicationScoped
public class DashboardSettings {

    public static DashboardSettings lookup() {
        return CDIBeanLocator.getBeanByType(DashboardSettings.class);
    }

    @Inject @Config("1000")
    private int maxEntriesInFilters;

    public int getMaxEntriesInFilters() {
        return maxEntriesInFilters;
    }

    public void setMaxEntriesInFilters(int maxEntriesInFilters) {
        this.maxEntriesInFilters = maxEntriesInFilters;
    }
}
