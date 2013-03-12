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

import org.jboss.dashboard.ui.DashboardFilter;
import org.jboss.dashboard.ui.components.DashboardFilterProperty;
import org.jboss.dashboard.function.ScalarFunction;
import org.jboss.dashboard.LocaleManager;

import java.util.Collection;
import java.util.Locale;
import java.util.Iterator;
import java.text.NumberFormat;

/**
 * It applies a scalar function over a data set property. 
 */
public class ScalarFunctionCommand extends AbstractCommand {

    protected ScalarFunction scalarFunction;

    public ScalarFunctionCommand(String commandName, ScalarFunction scalarFunction) {
        super(commandName);
        this.scalarFunction = scalarFunction;
    }

    public ScalarFunction getScalarFunction() {
        return scalarFunction;
    }

    public void setScalarFunction(ScalarFunction scalarFunction) {
        this.scalarFunction = scalarFunction;
    }

    public String execute() throws Exception {
        if (getArguments().size() < 1) return "[" + name + ", missing arguments]";
        String propId = getArgument(0);
        DashboardFilter dashboardFilter = (DashboardFilter) dataFilter;
        DashboardFilterProperty filterProp = dashboardFilter.getFilterPropertyById(propId);
        if (filterProp == null || filterProp.getDataProviderCode() == null) {
            return "[" + name + ", property '" + propId + "' not found]";
        }

        Locale locale = LocaleManager.currentLocale();
        Collection values = filterProp.getPropertyAllValues();
        if (values == null || values.isEmpty()) return filterProp.formatPropertyValue(new Double(0), locale);

        // Get the first not null value.
        Object first = null;
        Iterator it = values.iterator();
        while (first == null && it.hasNext()) first = it.next();
        if (first == null) return filterProp.formatPropertyValue(new Double(0), locale);

        if (!scalarFunction.isTypeSupported(first.getClass())) return "[" + name + ", " + first.getClass().getName() + " type unsupported]";
        return NumberFormat.getInstance(locale).format(scalarFunction.scalar(values));
    }

    public boolean containsProperty(String propertyId) throws Exception {
        String arg = getArgument(0);
        return (arg != null && arg.equals(propertyId));
    }
}
