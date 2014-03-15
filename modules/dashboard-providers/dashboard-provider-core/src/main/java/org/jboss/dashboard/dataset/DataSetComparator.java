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
package org.jboss.dashboard.dataset;

import java.util.Iterator;

import javax.inject.Inject;

import org.jboss.dashboard.commons.comparator.AbstractComparatorByCriteria;
import org.jboss.dashboard.commons.comparator.ComparatorUtils;
import org.slf4j.Logger;

/**
 * Utility class that can be used to compare two dataset rows by a given column index.
 */
public class DataSetComparator extends AbstractComparatorByCriteria {

    @Inject
    private transient Logger log;

    public int compare(Object o1, Object o2) {
        try {
            // Objects must be not null arrays.
            if (o1 == null && o2 != null) return -1;
            else if (o1 != null && o2 == null) return 1;
            else if (o1 == null && o2 == null) return 0;

            // Compare the two rows.
            Object[] row1 = (Object[]) o1;
            Object[] row2 = (Object[]) o2;

            Iterator it = sortCriterias.iterator();
            while (it.hasNext()) {
                Object[] criteriaProps =  (Object[]) it.next();
                String criteriaId = (String) criteriaProps[0];
                int column = Integer.parseInt(criteriaId);
                int ordering = ((Integer)criteriaProps[1]).intValue();
                int compById = ComparatorUtils.compare(row1[column], row2[column], ordering);
                if (compById != 0) return compById;
            }
            // Comparison gives equality.
            return 0;

        } catch (ClassCastException e) {
            log.warn("Cannot compare the given objects for the data set.");
            return 0;
        }
    }
}
