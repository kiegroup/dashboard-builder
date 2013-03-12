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
package org.jboss.dashboard.command;

import org.jboss.dashboard.provider.DataFormatterRegistry;
import org.jboss.dashboard.provider.DataPropertyFormatter;
import org.jboss.dashboard.ui.DashboardFilter;
import org.jboss.dashboard.ui.components.DashboardFilterProperty;
import org.jboss.dashboard.LocaleManager;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Command for the access to the data filter of the current dashboard.
 */
public class DashboardFilterCommand extends AbstractCommand {

    public static final String FILTER_MIN_VALUE = "dashboard_minvalue";
    public static final String FILTER_MAX_VALUE = "dashboard_maxvalue";
    public static final String FILTER_ALL       = "dashboard_allvalues";
    public static final String FILTER_SELECTED  = "dashboard_selectedvalues";

    public DashboardFilterCommand(String commandName) {
        super(commandName);
    }

    public String execute() throws Exception {
        if (getArguments().size() < 1) return "[" + name + ", missing arguments]";
        String propId = getArgument(0);
        DashboardFilter dashboardFilter = (DashboardFilter) dataFilter;
        DashboardFilterProperty filterProp = dashboardFilter.getFilterPropertyById(propId);
        if (filterProp == null) return "[" + name + ", property '" + propId + "' not found]";

        String commandName = getName();
        DataPropertyFormatter dpf = DataFormatterRegistry.lookup().getPropertyFormatter(propId);
        Locale locale = LocaleManager.currentLocale();
        
        if (FILTER_MIN_VALUE.equals(commandName)) {
            Comparable min = filterProp.getPropertyMinValue();
            return dpf.formatValue(propId, min, locale);
        }
        if (FILTER_MAX_VALUE.equals(commandName)) {
            Comparable max = filterProp.getPropertyMaxValue();
            return dpf.formatValue(propId, max, locale);
        }
        if (FILTER_SELECTED.equals(commandName)) {
            List values = filterProp.getPropertySelectedValues();
            if (values.isEmpty()) return null;

            Collections.sort(values);
            String separator = getArgument(1);
            if (separator == null) separator = ", ";

            StringBuffer result = new StringBuffer();
            Iterator it = values.iterator();
            while (it.hasNext()) {
                Object value = it.next();
                if (result.length() > 0) result.append(separator);
                result.append(dpf.formatValue(propId, value, locale));
            }
            return result.toString();
        }
        if (FILTER_ALL.equals(commandName)) {
            List values = filterProp.getPropertyDistinctValues();
            if (values.isEmpty()) return null;

            Collections.sort(values);
            String separator = getArgument(1);
            if (separator == null) separator = ", ";

            StringBuffer result = new StringBuffer();
            Iterator it = values.iterator();
            while (it.hasNext()) {
                Object value = it.next();
                if (result.length() > 0) result.append(separator);
                result.append(dpf.formatValue(propId, value, locale));
            }
            return result.toString();
        }
        return "[" + commandName + ", command not supported]";
    }

    public boolean containsProperty(String propertyId) throws Exception {
        String arg = getArgument(0);
        return (arg != null && arg.equals(propertyId));
    }
}
