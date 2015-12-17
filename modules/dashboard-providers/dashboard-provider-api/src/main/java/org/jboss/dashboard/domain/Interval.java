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

import org.jboss.dashboard.provider.DataProperty;
import java.util.Locale;
import java.util.List;

/**
 * An interval contains a subset of the dataset values for a given domain property.
 */
public interface Interval {

    Domain getDomain();
    void setDomain(Domain d);
    boolean contains(Object value);
    String getDescription(Locale l);

    /**
     * Get the property values belonging to this interval.
     * @param p The property which values are to be to obtained.
     */
    List getValues(DataProperty p);
}
