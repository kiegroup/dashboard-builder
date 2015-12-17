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
package org.jboss.dashboard.domain;

import org.jboss.dashboard.DataProviderServices;
import org.jboss.dashboard.function.ScalarFunction;
import org.jboss.dashboard.provider.DataProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for the implementation of the different Domain classes. 
 */
public abstract class AbstractDomain implements Domain {

    // i18n
    public static final String I18N_PREFFIX = "abstractDomain.";

    protected DataProperty property;
    protected int maxNumberOfIntervals;
    protected List<ScalarFunction> scalarFunctionsSupported;

    public AbstractDomain() {
        property = null;
        maxNumberOfIntervals = 10;
        scalarFunctionsSupported = null;
    }

    public DataProperty getProperty() {
        return property;
    }

    public void setProperty(DataProperty property) {
        this.property = property;
    }

    public int getMaxNumberOfIntervals() {
        return maxNumberOfIntervals;
    }

    public void setMaxNumberOfIntervals(int maxNumberOfIntervals) {
        this.maxNumberOfIntervals = maxNumberOfIntervals;
    }

    public boolean isScalarFunctionSupported(String functionCode) {
        ScalarFunction function = DataProviderServices.lookup().getScalarFunctionManager().getScalarFunctionByCode(functionCode);
        if (function == null) return false;
        return isScalarFunctionSupported(function);
    }

    public List<ScalarFunction> getScalarFunctionsSupported() {
        // This change allow you to restrict the scalar functions supported
        if (scalarFunctionsSupported != null) return scalarFunctionsSupported;
        List<ScalarFunction> supported = new ArrayList<ScalarFunction>();
        ScalarFunction[] sfs = DataProviderServices.lookup().getScalarFunctionManager().getAllScalarFunctions();
        for (ScalarFunction sf : sfs) {
            if (sf != null && isScalarFunctionSupported(sf)) supported.add(sf);
        }
        return supported;
    }

    public void setScalarFunctionsSupported(List<ScalarFunction> scalarFunctionsSupported) {
        this.scalarFunctionsSupported = scalarFunctionsSupported;
    }

    public Domain cloneDomain() {
        try {
            AbstractDomain clone = (AbstractDomain) super.clone();
            clone.scalarFunctionsSupported = null;
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
