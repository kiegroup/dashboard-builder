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
package org.jboss.dashboard.function;

import java.util.Collection;
import java.util.Locale;

/**
 * Interface used to get the scalar value of a given collection.
 * <p>Scalar functions are used in BAM to calculate the scalar
 * values of the intervals belonging to a given data set domain property.
 *
 * @see org.jboss.dashboard.domain.Domain
 * @see org.jboss.dashboard.domain.Interval
 */
public interface ScalarFunction {

    String getCode();
    String getName(Locale l);
    String getDescription(Locale l);
    double scalar(Collection values);
    boolean isTypeSupported(Class type);
}
