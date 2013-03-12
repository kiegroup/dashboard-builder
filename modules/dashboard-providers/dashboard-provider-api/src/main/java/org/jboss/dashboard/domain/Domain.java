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
package org.jboss.dashboard.domain;

import org.jboss.dashboard.function.ScalarFunction;
import org.jboss.dashboard.provider.DataProperty;

import java.util.List;

/**
 * A "domain" is the property used to group a given data set into a set of intervals.
 * Once a domain is fixed for a given data set then it makes sense to apply a scalar
 * function on each interval. The "domain" is the base building block for the definition of
 * key performance indicators.
 */
public interface Domain extends Cloneable {

    DataProperty getProperty();
    void setProperty(DataProperty property);
    Interval[] getIntervals();
    int getMaxNumberOfIntervals();
    void setMaxNumberOfIntervals(int maxIntervals);
    boolean isScalarFunctionSupported(String functionCode);
    boolean isScalarFunctionSupported(ScalarFunction sf);
    List getScalarFunctionsSupported();
    Class getValuesClass();    
    Domain cloneDomain();
}
